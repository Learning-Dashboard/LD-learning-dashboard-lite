package com.upc.gessi.qrapids.app.domain.controllers;

import com.upc.gessi.qrapids.app.domain.adapters.QMA.QMAQualityFactors;
import com.upc.gessi.qrapids.app.domain.exceptions.CategoriesException;
import com.upc.gessi.qrapids.app.domain.exceptions.ProjectNotFoundException;
import com.upc.gessi.qrapids.app.domain.models.QFCategory;
import com.upc.gessi.qrapids.app.domain.repositories.QFCategory.QFCategoryRepository;
import com.upc.gessi.qrapids.app.presentation.rest.dto.DTODetailedFactorEvaluation;
import com.upc.gessi.qrapids.app.presentation.rest.dto.DTOFactorEvaluation;
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
public class FactorEvaluationControllerTest {

    private DomainObjectsBuilder builder;

    @Mock
    private QMAQualityFactors qmaQualityFactors;

    @Mock
    private QFCategoryRepository factorCategoryRepository;

    @InjectMocks
    private FactorsController factorsController;

    @Before
    public void setUp() {
        builder = new DomainObjectsBuilder();
    }

    @Test
    public void getFactorCategoriesReturnsAllCategoriesWhenNameIsMissing() {
        List<QFCategory> categories = builder.buildFactorCategoryList();
        when(factorCategoryRepository.findAll()).thenReturn(categories);

        List<QFCategory> result = factorsController.getFactorCategories(null);

        assertEquals(categories, result);
    }

    @Test
    public void newFactorCategoriesPersistsThresholdsAsFractions() throws CategoriesException {
        List<Map<String, String>> rawCategories = builder.buildRawFactorCategoryList();

        factorsController.newFactorCategories(rawCategories, "Default", "Default");

        verify(factorCategoryRepository).existsByName("Default");
        ArgumentCaptor<QFCategory> captor = ArgumentCaptor.forClass(QFCategory.class);
        verify(factorCategoryRepository, times(3)).save(captor.capture());

        List<QFCategory> saved = captor.getAllValues();
        assertEquals(rawCategories.get(0).get("type"), saved.get(0).getType());
        assertEquals(rawCategories.get(0).get("color"), saved.get(0).getColor());
        assertEquals(Float.parseFloat(rawCategories.get(0).get("upperThreshold")) / 100f,
                saved.get(0).getUpperThreshold(), 0f);
        assertEquals("Default", saved.get(0).getName());
        assertEquals("Default", saved.get(0).getPatternGroup());
    }

    @Test(expected = CategoriesException.class)
    public void newFactorCategoriesRejectsDuplicateTypes() throws CategoriesException {
        List<Map<String, String>> rawCategories = builder.buildRawFactorCategoryList();
        rawCategories.get(1).put("type", rawCategories.get(0).get("type"));

        factorsController.newFactorCategories(rawCategories, "Default", "Default");
    }

    @Test
    public void getAllFactorsEvaluationDelegatesToQmaQualityFactors() throws IOException {
        List<DTOFactorEvaluation> evaluations = Arrays.asList(builder.buildDTOFactor());
        when(qmaQualityFactors.getAllFactors("test", "profile", true)).thenReturn(evaluations);

        List<DTOFactorEvaluation> result = factorsController.getAllFactorsEvaluation("test", "profile", true);

        assertSame(evaluations, result);
    }

    @Test
    public void getDetailedCurrentEvaluationDelegatesToQmaQualityFactors()
            throws IOException, ProjectNotFoundException {
        List<DTODetailedFactorEvaluation> evaluations = Arrays.asList(builder.buildDTOQualityFactor());
        when(qmaQualityFactors.CurrentEvaluation(null, "test", null, true)).thenReturn(evaluations);

        List<DTODetailedFactorEvaluation> result =
                factorsController.getAllFactorsWithMetricsCurrentEvaluation("test", null, true);

        assertSame(evaluations, result);
    }

    @Test
    public void getDetailedHistoricalEvaluationDelegatesToQmaQualityFactors()
            throws IOException, ProjectNotFoundException {
        List<DTODetailedFactorEvaluation> evaluations = Arrays.asList(builder.buildDTOQualityFactor());
        LocalDate from = LocalDate.parse("2024-01-01");
        LocalDate to = LocalDate.parse("2024-01-31");
        when(qmaQualityFactors.HistoricalData("si", from, to, "test", null)).thenReturn(evaluations);

        List<DTODetailedFactorEvaluation> result =
                factorsController.getFactorsWithMetricsForOneStrategicIndicatorHistoricalEvaluation(
                        "si", "test", from, to);

        assertSame(evaluations, result);
    }
}
