package com.upc.gessi.qrapids.app.presentation.rest.services;

import com.upc.gessi.qrapids.app.domain.controllers.MetricsController;
import com.upc.gessi.qrapids.app.domain.controllers.StudentsController;
import com.upc.gessi.qrapids.app.domain.models.DataSource;
import com.upc.gessi.qrapids.app.presentation.rest.dto.DTOMetricEvaluation;
import com.upc.gessi.qrapids.app.presentation.rest.dto.DTOStudentIdentity;
import com.upc.gessi.qrapids.app.presentation.rest.dto.DTOStudentMetrics;
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
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StudentsTest {

    private MockMvc mockMvc;
    private DomainObjectsBuilder builder;

    @Mock
    private MetricsController metricsDomainController;

    @Mock
    private StudentsController studentsDomainController;

    @InjectMocks
    private Metrics metricsController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(metricsController).build();
        builder = new DomainObjectsBuilder();
    }

    @Test
    public void getStudentsAndMetricsReturnsCurrentStudentMetrics() throws Exception {
        DTOStudentMetrics studentMetrics = buildStudentMetrics();
        when(studentsDomainController.getStudentMetricsFromProject("test", null, null, null))
                .thenReturn(Arrays.asList(studentMetrics));

        mockMvc.perform(get("/api/metrics/students").param("prj", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(studentMetrics.getName())))
                .andExpect(jsonPath("$[0].metrics[0].id", is(studentMetrics.getMetrics().get(0).getId())))
                .andExpect(jsonPath("$[0].metrics[0].value",
                        is(HelperFunctions.getFloatAsDouble(studentMetrics.getMetrics().get(0).getValue()))));

        verify(studentsDomainController).getStudentMetricsFromProject("test", null, null, null);
    }

    @Test
    public void getStudentsAndMetricsHistoricalParsesDateRange() throws Exception {
        DTOStudentMetrics studentMetrics = buildStudentMetrics();
        LocalDate from = LocalDate.parse("2024-01-01");
        LocalDate to = LocalDate.parse("2024-01-31");
        when(studentsDomainController.getStudentMetricsFromProject("test", from, to, "profile"))
                .thenReturn(Arrays.asList(studentMetrics));

        mockMvc.perform(get("/api/metrics/students/historical")
                        .param("prj", "test")
                        .param("profile", "profile")
                        .param("from", "2024-01-01")
                        .param("to", "2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(studentMetrics.getName())));

        verify(studentsDomainController).getStudentMetricsFromProject("test", from, to, "profile");
    }

    private DTOStudentMetrics buildStudentMetrics() {
        Map<DataSource, DTOStudentIdentity> identities = new HashMap<>();
        identities.put(DataSource.GITHUB, new DTOStudentIdentity(DataSource.GITHUB, "student"));
        DTOMetricEvaluation metric = builder.buildDTOMetric();
        return new DTOStudentMetrics("Student", identities, Arrays.asList(metric), 1);
    }
}
