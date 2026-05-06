package com.upc.gessi.qrapids.app.domain.controllers;

import com.upc.gessi.qrapids.app.domain.adapters.QMA.QMADetailedStrategicIndicators;
import com.upc.gessi.qrapids.app.domain.adapters.QMA.QMAStrategicIndicators;
import com.upc.gessi.qrapids.app.domain.exceptions.CategoriesException;
import com.upc.gessi.qrapids.app.domain.exceptions.ProjectNotFoundException;
import com.upc.gessi.qrapids.app.domain.models.SICategory;
import com.upc.gessi.qrapids.app.domain.repositories.SICategory.SICategoryRepository;
import com.upc.gessi.qrapids.app.presentation.rest.dto.DTOAssessment;
import com.upc.gessi.qrapids.app.presentation.rest.dto.DTODetailedStrategicIndicatorEvaluation;
import com.upc.gessi.qrapids.app.presentation.rest.dto.DTOFactorEvaluation;
import com.upc.gessi.qrapids.app.presentation.rest.dto.DTOStrategicIndicatorEvaluation;
import com.upc.gessi.qrapids.app.testHelpers.DomainObjectsBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StrategicIndicatorsControllerTest {

    private DomainObjectsBuilder builder;

    @Mock
    private QMAStrategicIndicators qmaStrategicIndicators;

    @Mock
    private QMADetailedStrategicIndicators qmaDetailedStrategicIndicators;

    @Mock
    private SICategoryRepository strategicIndicatorCategoryRepository;

    @InjectMocks
    private StrategicIndicatorsController strategicIndicatorsController;

    @Before
    public void setUp() {
        builder = new DomainObjectsBuilder();
    }

    @Test
    public void getCurrentStrategicIndicatorsDelegatesToQmaStrategicIndicators()
            throws IOException, CategoriesException, ProjectNotFoundException {
        List<DTOStrategicIndicatorEvaluation> evaluations =
                Arrays.asList(builder.buildDTOStrategicIndicatorEvaluation());
        when(qmaStrategicIndicators.CurrentEvaluation("test", "profile")).thenReturn(evaluations);

        List<DTOStrategicIndicatorEvaluation> result =
                strategicIndicatorsController.getAllStrategicIndicatorsCurrentEvaluation("test", "profile");

        assertSame(evaluations, result);
    }

    @Test
    public void getSingleCurrentStrategicIndicatorDelegatesToQmaStrategicIndicators()
            throws IOException, CategoriesException, ProjectNotFoundException {
        DTOStrategicIndicatorEvaluation evaluation = builder.buildDTOStrategicIndicatorEvaluation();
        when(qmaStrategicIndicators.SingleCurrentEvaluation("test", null, "processperformance"))
                .thenReturn(evaluation);

        DTOStrategicIndicatorEvaluation result =
                strategicIndicatorsController.getSingleStrategicIndicatorsCurrentEvaluation(
                        "processperformance", "test", null);

        assertSame(evaluation, result);
    }

    @Test
    public void getDetailedCurrentStrategicIndicatorsDelegatesToQmaDetailedStrategicIndicators()
            throws IOException, ProjectNotFoundException {
        DTOFactorEvaluation factor = builder.buildDTOFactor();
        List<DTODetailedStrategicIndicatorEvaluation> evaluations =
                Arrays.asList(new DTODetailedStrategicIndicatorEvaluation("si", "Strategic indicator",
                        Arrays.asList(factor)));
        when(qmaDetailedStrategicIndicators.CurrentEvaluation(null, "test", null, true)).thenReturn(evaluations);

        List<DTODetailedStrategicIndicatorEvaluation> result =
                strategicIndicatorsController.getAllDetailedStrategicIndicatorsCurrentEvaluation("test", null, true);

        assertSame(evaluations, result);
    }

    @Test
    public void getHistoricalStrategicIndicatorsDelegatesToQmaStrategicIndicators()
            throws IOException, CategoriesException, ProjectNotFoundException {
        List<DTOStrategicIndicatorEvaluation> evaluations =
                Arrays.asList(builder.buildDTOStrategicIndicatorEvaluation());
        LocalDate from = LocalDate.parse("2024-01-01");
        LocalDate to = LocalDate.parse("2024-01-31");
        when(qmaStrategicIndicators.HistoricalData(from, to, "test", null)).thenReturn(evaluations);

        List<DTOStrategicIndicatorEvaluation> result =
                strategicIndicatorsController.getAllStrategicIndicatorsHistoricalEvaluation("test", null, from, to);

        assertSame(evaluations, result);
    }

    @Test
    public void buildDescriptiveLabelAndValueUsesLabelAndFormattedValue() {
        String result = StrategicIndicatorsController.buildDescriptiveLabelAndValue(Pair.of(0.75f, "Good"));

        assertEquals("Good (0.75)", result);
    }

    @Test
    public void getValueAndLabelFromCategoriesReturnsDominantAssessment() {
        List<SICategory> categories = builder.buildSICategoryList();
        when(strategicIndicatorCategoryRepository.findAll()).thenReturn(categories);
        List<DTOAssessment> assessments = Arrays.asList(
                new DTOAssessment(1L, "Good", 0.8f, "#00ff00", 1f),
                new DTOAssessment(2L, "Neutral", 0.1f, "#ff8000", 0.66f),
                new DTOAssessment(3L, "Bad", 0.1f, "#ff0000", 0.33f));

        Pair<Float, String> result = strategicIndicatorsController.getValueAndLabelFromCategories(assessments);

        assertEquals("Good", result.getSecond());
        assertEquals(0.8333334f, result.getFirst(), 0.0001f);
    }

    @Test
    public void getLabelReturnsHighestCategoryWhenValueIsAtLeastOne() {
        List<SICategory> categories = builder.buildSICategoryList();
        when(strategicIndicatorCategoryRepository.findAll()).thenReturn(categories);

        String result = strategicIndicatorsController.getLabel(2f);

        assertEquals("Good", result);
    }
}
