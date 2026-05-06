package com.upc.gessi.qrapids.app.presentation.rest.services;

import com.upc.gessi.qrapids.app.domain.controllers.FactorsController;
import com.upc.gessi.qrapids.app.domain.controllers.ProjectsController;
import com.upc.gessi.qrapids.app.domain.controllers.StrategicIndicatorsController;
import com.upc.gessi.qrapids.app.domain.models.Project;
import com.upc.gessi.qrapids.app.domain.models.Strategic_Indicator;
import com.upc.gessi.qrapids.app.presentation.rest.dto.DTODetailedFactorEvaluation;
import com.upc.gessi.qrapids.app.presentation.rest.dto.DTODetailedStrategicIndicatorEvaluation;
import com.upc.gessi.qrapids.app.presentation.rest.dto.DTOFactorEvaluation;
import com.upc.gessi.qrapids.app.presentation.rest.dto.DTOStrategicIndicatorEvaluation;
import com.upc.gessi.qrapids.app.testHelpers.DomainObjectsBuilder;
import com.upc.gessi.qrapids.app.testHelpers.HelperFunctions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StrategicIndicatorsTest {

    private MockMvc mockMvc;
    private DomainObjectsBuilder builder;

    @Mock
    private FactorsController factorsController;

    @Mock
    private StrategicIndicatorsController strategicIndicatorsDomainController;

    @Mock
    private ProjectsController projectsController;

    @InjectMocks
    private StrategicIndicators strategicIndicatorsController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(strategicIndicatorsController).build();
        builder = new DomainObjectsBuilder();
    }

    @Test
    public void getStrategicIndicatorsEvaluationReturnsCurrentEvaluations() throws Exception {
        DTOStrategicIndicatorEvaluation si = builder.buildDTOStrategicIndicatorEvaluation();
        when(strategicIndicatorsDomainController.getAllStrategicIndicatorsCurrentEvaluation("test", null))
                .thenReturn(Arrays.asList(si));

        mockMvc.perform(get("/api/strategicIndicators/current").param("prj", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(si.getId())))
                .andExpect(jsonPath("$[0].name", is(si.getName())))
                .andExpect(jsonPath("$[0].value.first",
                        is(HelperFunctions.getFloatAsDouble(si.getValue().getFirst()))))
                .andExpect(jsonPath("$[0].hasFeedback", is(false)));

        verify(strategicIndicatorsDomainController).getAllStrategicIndicatorsCurrentEvaluation("test", null);
    }

    @Test
    public void getSingleStrategicIndicatorEvaluationReturnsCurrentEvaluation() throws Exception {
        DTOStrategicIndicatorEvaluation si = builder.buildDTOStrategicIndicatorEvaluation();
        when(strategicIndicatorsDomainController.getSingleStrategicIndicatorsCurrentEvaluation(
                "processperformance", "test", null)).thenReturn(si);

        mockMvc.perform(get("/api/strategicIndicators/{id}/current", "processperformance")
                        .param("prj", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(si.getId())))
                .andExpect(jsonPath("$.name", is(si.getName())));

        verify(strategicIndicatorsDomainController)
                .getSingleStrategicIndicatorsCurrentEvaluation("processperformance", "test", null);
    }

    @Test
    public void getDetailedSICurrentEvaluationReturnsDetailedEvaluations() throws Exception {
        DTOFactorEvaluation factor = builder.buildDTOFactor();
        DTODetailedStrategicIndicatorEvaluation detailed =
                new DTODetailedStrategicIndicatorEvaluation("si", "Strategic indicator", Arrays.asList(factor));
        when(strategicIndicatorsDomainController.getAllDetailedStrategicIndicatorsCurrentEvaluation(
                "test", null, true)).thenReturn(Arrays.asList(detailed));

        mockMvc.perform(get("/api/strategicIndicators/qualityFactors/current").param("prj", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(detailed.getId())))
                .andExpect(jsonPath("$[0].factors[0].id", is(factor.getId())));

        verify(strategicIndicatorsDomainController)
                .getAllDetailedStrategicIndicatorsCurrentEvaluation("test", null, true);
    }

    @Test
    public void getQualityFactorsWithMetricsForOneStrategicIndicatorReturnsDetailedFactors() throws Exception {
        DTODetailedFactorEvaluation detailedFactor = builder.buildDTOQualityFactor();
        when(factorsController.getFactorsWithMetricsForOneStrategicIndicatorCurrentEvaluation("si", "test"))
                .thenReturn(Arrays.asList(detailedFactor));

        mockMvc.perform(get("/api/strategicIndicators/{id}/qualityFactors/metrics/current", "si")
                        .param("prj", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(detailedFactor.getId())))
                .andExpect(jsonPath("$[0].metrics[0].id", is(detailedFactor.getMetrics().get(0).getId())));

        verify(factorsController).getFactorsWithMetricsForOneStrategicIndicatorCurrentEvaluation("si", "test");
    }

    @Test
    public void getStrategicIndicatorsHistoricalDataParsesDateRange() throws Exception {
        DTOStrategicIndicatorEvaluation si = builder.buildDTOStrategicIndicatorEvaluation();
        LocalDate from = LocalDate.parse("2024-01-01");
        LocalDate to = LocalDate.parse("2024-01-31");
        when(strategicIndicatorsDomainController.getAllStrategicIndicatorsHistoricalEvaluation(
                "test", null, from, to)).thenReturn(Arrays.asList(si));

        mockMvc.perform(get("/api/strategicIndicators/historical")
                        .param("prj", "test")
                        .param("from", "2024-01-01")
                        .param("to", "2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(si.getId())));

        verify(strategicIndicatorsDomainController)
                .getAllStrategicIndicatorsHistoricalEvaluation("test", null, from, to);
    }

    @Test
    public void getAllStrategicIndicatorsMapsDomainObjectsToDto() throws Exception {
        Project project = builder.buildProject();
        Strategic_Indicator si = builder.buildStrategicIndicator(project);
        when(strategicIndicatorsDomainController.getStrategicIndicatorsByProjectAndProfile("test", null))
                .thenReturn(Arrays.asList(si));

        mockMvc.perform(get("/api/strategicIndicators").param("prj", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(si.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(si.getName())))
                .andExpect(jsonPath("$[0].qualityFactors", is(si.getQuality_factorsIds())));

        verify(strategicIndicatorsDomainController).getStrategicIndicatorsByProjectAndProfile("test", null);
    }

    @Test
    public void assessStrategicIndicatorsRunsFactorsThenStrategicIndicators() throws Exception {
        when(projectsController.getAllProjectsExternalID()).thenReturn(Arrays.asList("test"));
        when(factorsController.assessQualityFactors("test", null)).thenReturn(true);
        when(strategicIndicatorsDomainController.assessStrategicIndicators("test", null)).thenReturn(true);

        mockMvc.perform(get("/api/strategicIndicators/assess").param("prj", "test"))
                .andExpect(status().isOk());

        verify(factorsController).assessQualityFactors("test", null);
        verify(strategicIndicatorsDomainController).assessStrategicIndicators("test", null);
    }
}
