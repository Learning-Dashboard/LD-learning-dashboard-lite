package com.upc.gessi.qrapids.app.presentation.rest.services;

import com.google.gson.Gson;
import com.upc.gessi.qrapids.app.domain.controllers.MetricsController;
import com.upc.gessi.qrapids.app.domain.controllers.StudentsController;
import com.upc.gessi.qrapids.app.domain.models.MetricCategory;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MetricsTest {

    private MockMvc mockMvc;
    private DomainObjectsBuilder builder;

    @Mock
    private MetricsController metricsDomainController;

    @Mock
    private StudentsController studentsController;

    @InjectMocks
    private Metrics metricsController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(metricsController).build();
        builder = new DomainObjectsBuilder();
    }

    @Test
    public void getMetricsCategoriesReturnsConfiguredCategories() throws Exception {
        List<MetricCategory> categories = builder.buildMetricCategoryList();
        when(metricsDomainController.getMetricCategories(null)).thenReturn(categories);

        mockMvc.perform(get("/api/metrics/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name", is(categories.get(0).getName())))
                .andExpect(jsonPath("$[0].type", is(categories.get(0).getType())))
                .andExpect(jsonPath("$[0].upperThreshold",
                        is(HelperFunctions.getFloatAsDouble(categories.get(0).getUpperThreshold()))));

        verify(metricsDomainController).getMetricCategories(null);
    }

    @Test
    public void newMetricsCategoriesDelegatesToDomainController() throws Exception {
        List<Map<String, String>> rawCategories = builder.buildRawMetricCategoryList();

        mockMvc.perform(post("/api/metrics/categories")
                        .param("name", "Default")
                        .param("patternGroup", "Default")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(rawCategories)))
                .andExpect(status().isCreated());

        verify(metricsDomainController).newMetricCategories(rawCategories, "Default", "Default");
    }

    @Test
    public void getMetricsCurrentReturnsCurrentEvaluations() throws Exception {
        DTOMetricEvaluation metric = builder.buildDTOMetric();
        when(metricsDomainController.getAllMetricsCurrentEvaluation("test", null))
                .thenReturn(Arrays.asList(metric));

        mockMvc.perform(get("/api/metrics/current").param("prj", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(metric.getId())))
                .andExpect(jsonPath("$[0].name", is(metric.getName())))
                .andExpect(jsonPath("$[0].value", is(HelperFunctions.getFloatAsDouble(metric.getValue()))))
                .andExpect(jsonPath("$[0].qualityFactors", is(metric.getQualityFactors())));

        verify(metricsDomainController).getAllMetricsCurrentEvaluation("test", null);
    }

    @Test
    public void getSingleMetricHistoricalParsesDateRange() throws Exception {
        DTOMetricEvaluation metric = builder.buildDTOMetric();
        LocalDate from = LocalDate.parse("2024-01-01");
        LocalDate to = LocalDate.parse("2024-01-31");
        when(metricsDomainController.getSingleMetricHistoricalEvaluation("fasttests", "test", "profile", from, to))
                .thenReturn(Arrays.asList(metric));

        mockMvc.perform(get("/api/metrics/{id}/historical", "fasttests")
                        .param("prj", "test")
                        .param("profile", "profile")
                        .param("from", "2024-01-01")
                        .param("to", "2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(metric.getId())));

        verify(metricsDomainController).getSingleMetricHistoricalEvaluation("fasttests", "test", "profile", from, to);
    }

    @Test
    public void getCurrentDateReturnsDateFromFirstMetric() throws Exception {
        DTOMetricEvaluation metric = builder.buildDTOMetric();
        when(metricsDomainController.getAllMetricsCurrentEvaluation("test", null))
                .thenReturn(Arrays.asList(metric));

        mockMvc.perform(get("/api/metrics/currentDate").param("prj", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]", is(metric.getDate().getYear())))
                .andExpect(jsonPath("$[1]", is(metric.getDate().getMonthValue())))
                .andExpect(jsonPath("$[2]", is(metric.getDate().getDayOfMonth())));

        verify(metricsDomainController, times(1)).getAllMetricsCurrentEvaluation("test", null);
    }
}
