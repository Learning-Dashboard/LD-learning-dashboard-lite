package com.upc.gessi.qrapids.app.presentation.rest.services;

import com.google.gson.Gson;
import com.upc.gessi.qrapids.app.domain.controllers.FactorsController;
import com.upc.gessi.qrapids.app.domain.controllers.MetricsController;
import com.upc.gessi.qrapids.app.domain.controllers.ProjectsController;
import com.upc.gessi.qrapids.app.domain.models.Factor;
import com.upc.gessi.qrapids.app.domain.models.Project;
import com.upc.gessi.qrapids.app.presentation.rest.dto.DTODetailedFactorEvaluation;
import com.upc.gessi.qrapids.app.presentation.rest.dto.DTOFactorEvaluation;
import com.upc.gessi.qrapids.app.presentation.rest.dto.DTOMetricEvaluation;
import com.upc.gessi.qrapids.app.testHelpers.DomainObjectsBuilder;
import com.upc.gessi.qrapids.app.testHelpers.HelperFunctions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FactorEvaluationTest {

    private MockMvc mockMvc;
    private DomainObjectsBuilder builder;

    @Mock
    private FactorsController qualityFactorsDomainController;

    @Mock
    private MetricsController metricsDomainController;

    @Mock
    private ProjectsController projectsController;

    @InjectMocks
    private Factors factorsController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(factorsController).build();
        builder = new DomainObjectsBuilder();
    }

    @Test
    public void newFactorCategoriesDelegatesToDomainController() throws Exception {
        List<Map<String, String>> rawCategories = builder.buildRawFactorCategoryList();

        mockMvc.perform(post("/api/factors/categories")
                        .param("name", "Default")
                        .param("patternGroup", "Default")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(rawCategories)))
                .andExpect(status().isCreated());

        verify(qualityFactorsDomainController).newFactorCategories(rawCategories, "Default", "Default");
    }

    @Test
    public void getAllQualityFactorsReturnsConfiguredFactors() throws Exception {
        Project project = builder.buildProject();
        Factor factor = builder.buildFactor(project);
        when(qualityFactorsDomainController.getQualityFactorsByProjectAndProfile("test", null))
                .thenReturn(Arrays.asList(factor));

        mockMvc.perform(get("/api/qualityFactors").param("prj", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(factor.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(factor.getName())))
                .andExpect(jsonPath("$[0].categoryName", is(factor.getCategoryName())));

        verify(qualityFactorsDomainController).getQualityFactorsByProjectAndProfile("test", null);
    }

    @Test
    public void getAllQualityFactorsEvaluationReturnsCurrentEvaluations() throws Exception {
        DTOFactorEvaluation factor = builder.buildDTOFactor();
        when(qualityFactorsDomainController.getAllFactorsEvaluation("test", null, true))
                .thenReturn(Arrays.asList(factor));

        mockMvc.perform(get("/api/qualityFactors/current").param("prj", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(factor.getId())))
                .andExpect(jsonPath("$[0].name", is(factor.getName())))
                .andExpect(jsonPath("$[0].value.first",
                        is(HelperFunctions.getFloatAsDouble(factor.getValue().getFirst()))))
                .andExpect(jsonPath("$[0].strategicIndicators", is(factor.getStrategicIndicators())));

        verify(qualityFactorsDomainController).getAllFactorsEvaluation("test", null, true);
    }

    @Test
    public void getMetricsCurrentEvaluationForQualityFactorCombinesFactorAndMetrics() throws Exception {
        DTOFactorEvaluation factor = builder.buildDTOFactor();
        DTOMetricEvaluation metric = builder.buildDTOMetric();
        when(metricsDomainController.getMetricsForQualityFactorCurrentEvaluation("testingperformance", "test"))
                .thenReturn(Arrays.asList(metric));
        when(qualityFactorsDomainController.getSingleFactorEvaluation("testingperformance", "test"))
                .thenReturn(factor);

        mockMvc.perform(get("/api/qualityFactors/{id}/metrics/current", "testingperformance")
                        .param("prj", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(factor.getId())))
                .andExpect(jsonPath("$[0].metrics[0].id", is(metric.getId())))
                .andExpect(jsonPath("$[0].type", is(factor.getType())));

        verify(metricsDomainController).getMetricsForQualityFactorCurrentEvaluation("testingperformance", "test");
        verify(qualityFactorsDomainController).getSingleFactorEvaluation("testingperformance", "test");
    }

    @Test
    public void getQualityFactorsHistoricalDataParsesDateRange() throws Exception {
        DTOFactorEvaluation factor = builder.buildDTOFactor();
        LocalDate from = LocalDate.parse("2024-01-01");
        LocalDate to = LocalDate.parse("2024-01-31");
        when(qualityFactorsDomainController.getAllFactorsHistoricalEvaluation("test", null, from, to))
                .thenReturn(Arrays.asList(factor));

        mockMvc.perform(get("/api/qualityFactors/historical")
                        .param("prj", "test")
                        .param("from", "2024-01-01")
                        .param("to", "2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(factor.getId())));

        verify(qualityFactorsDomainController).getAllFactorsHistoricalEvaluation("test", null, from, to);
    }

    @Test
    public void getDetailedQualityFactorsCurrentReturnsDetailedEvaluations() throws Exception {
        DTODetailedFactorEvaluation detailed = builder.buildDTOQualityFactor();
        when(qualityFactorsDomainController.getAllFactorsWithMetricsCurrentEvaluation("test", null, true))
                .thenReturn(Arrays.asList(detailed));

        mockMvc.perform(get("/api/qualityFactors/metrics/current").param("prj", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(detailed.getId())))
                .andExpect(jsonPath("$[0].metrics[0].id", is(detailed.getMetrics().get(0).getId())));

        verify(qualityFactorsDomainController).getAllFactorsWithMetricsCurrentEvaluation("test", null, true);
    }
}
