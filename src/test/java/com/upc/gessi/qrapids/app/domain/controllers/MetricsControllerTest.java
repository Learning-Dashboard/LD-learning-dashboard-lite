package com.upc.gessi.qrapids.app.domain.controllers;

import com.upc.gessi.qrapids.app.domain.adapters.QMA.QMAMetrics;
import com.upc.gessi.qrapids.app.domain.exceptions.CategoriesException;
import com.upc.gessi.qrapids.app.domain.models.MetricCategory;
import com.upc.gessi.qrapids.app.domain.repositories.MetricCategory.MetricCategoryRepository;
import com.upc.gessi.qrapids.app.presentation.rest.dto.DTOMetricEvaluation;
import com.upc.gessi.qrapids.app.testHelpers.DomainObjectsBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MetricsControllerTest {

    private DomainObjectsBuilder builder;

    @Mock
    private QMAMetrics qmaMetrics;

    @Mock
    private MetricCategoryRepository metricCategoryRepository;

    @InjectMocks
    private MetricsController metricsController;

    @Before
    public void setUp() {
        builder = new DomainObjectsBuilder();
    }

    @Test
    public void getMetricCategoriesReturnsAllCategoriesWhenNameIsMissing() {
        List<MetricCategory> categories = builder.buildMetricCategoryList();
        when(metricCategoryRepository.findAll()).thenReturn(categories);

        List<MetricCategory> result = metricsController.getMetricCategories(null);

        assertEquals(categories, result);
    }

    @Test
    public void newMetricCategoriesPersistsThresholdsAsFractions() throws CategoriesException {
        List<Map<String, String>> rawCategories = builder.buildRawMetricCategoryList();

        metricsController.newMetricCategories(rawCategories, "Default", "Default");

        verify(metricCategoryRepository).existsByName("Default");
        ArgumentCaptor<MetricCategory> captor = ArgumentCaptor.forClass(MetricCategory.class);
        verify(metricCategoryRepository, times(3)).save(captor.capture());

        List<MetricCategory> saved = captor.getAllValues();
        assertEquals(rawCategories.get(0).get("type"), saved.get(0).getType());
        assertEquals(rawCategories.get(0).get("color"), saved.get(0).getColor());
        assertEquals(Float.parseFloat(rawCategories.get(0).get("upperThreshold")) / 100f,
                saved.get(0).getUpperThreshold(), 0f);
        assertEquals("Default", saved.get(0).getName());
        assertEquals("Default", saved.get(0).getPatternGroup());
    }

    @Test(expected = CategoriesException.class)
    public void newMetricCategoriesRejectsDuplicateTypes() throws CategoriesException {
        List<Map<String, String>> rawCategories = builder.buildRawMetricCategoryList();
        rawCategories.get(1).put("type", rawCategories.get(0).get("type"));

        metricsController.newMetricCategories(rawCategories, "Default", "Default");
    }

    @Test
    public void getAllMetricsCurrentEvaluationDelegatesToQmaMetrics() throws IOException {
        List<DTOMetricEvaluation> evaluations = Arrays.asList(builder.buildDTOMetric());
        when(qmaMetrics.CurrentEvaluation(null, "test", "profile")).thenReturn(evaluations);

        List<DTOMetricEvaluation> result = metricsController.getAllMetricsCurrentEvaluation("test", "profile");

        assertSame(evaluations, result);
    }

    @Test
    public void getSingleMetricHistoricalEvaluationDelegatesToQmaMetrics() throws IOException {
        List<DTOMetricEvaluation> evaluations = Arrays.asList(builder.buildDTOMetric());
        LocalDate from = LocalDate.parse("2024-01-01");
        LocalDate to = LocalDate.parse("2024-01-31");
        when(qmaMetrics.SingleHistoricalData("fasttests", from, to, "test", null)).thenReturn(evaluations);

        List<DTOMetricEvaluation> result =
                metricsController.getSingleMetricHistoricalEvaluation("fasttests", "test", null, from, to);

        assertSame(evaluations, result);
    }
}
