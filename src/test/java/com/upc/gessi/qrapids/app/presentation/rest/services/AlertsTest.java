package com.upc.gessi.qrapids.app.presentation.rest.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upc.gessi.qrapids.app.domain.controllers.AlertsController;
import com.upc.gessi.qrapids.app.domain.controllers.ProjectsController;
import com.upc.gessi.qrapids.app.domain.models.Alert;
import com.upc.gessi.qrapids.app.domain.models.AlertType;
import com.upc.gessi.qrapids.app.domain.models.Notification;
import com.upc.gessi.qrapids.app.domain.models.Project;
import com.upc.gessi.qrapids.app.testHelpers.DomainObjectsBuilder;
import com.upc.gessi.qrapids.app.testHelpers.HelperFunctions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AlertsTest {

    private MockMvc mockMvc;
    private DomainObjectsBuilder builder;

    @Mock
    private SimpMessagingTemplate simpleMessagingTemplate;

    @Mock
    private ProjectsController projectsController;

    @Mock
    private AlertsController alertsController;

    @InjectMocks
    private Alerts alertsServiceController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(alertsServiceController).build();
        builder = new DomainObjectsBuilder();
    }

    @Test
    public void getAllAlertsMapsCurrentAlertFields() throws Exception {
        Project project = builder.buildProject();
        Alert alert = builder.buildAlert(project);
        when(projectsController.findProjectByExternalId(project.getExternalId())).thenReturn(project);
        when(alertsController.getAllProjectAlertsWithProfile(project.getId(), null))
                .thenReturn(Arrays.asList(alert));

        mockMvc.perform(get("/api/alerts").param("prj", project.getExternalId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(alert.getId().intValue())))
                .andExpect(jsonPath("$[0].affectedId", is(alert.getAffectedId())))
                .andExpect(jsonPath("$[0].affectedType", is(alert.getAffectedType())))
                .andExpect(jsonPath("$[0].type", is(alert.getType().toString())))
                .andExpect(jsonPath("$[0].value", is(HelperFunctions.getFloatAsDouble(alert.getValue()))))
                .andExpect(jsonPath("$[0].threshold", is(HelperFunctions.getFloatAsDouble(alert.getThreshold()))))
                .andExpect(jsonPath("$[0].status", is(alert.getStatus().toString())));

        verify(alertsController).changeAlertStatusToViewed(alert);
    }

    @Test
    public void countNewAlertsUsesProjectId() throws Exception {
        Project project = builder.buildProject();
        when(projectsController.findProjectByExternalId(project.getExternalId())).thenReturn(project);
        when(alertsController.countNewAlertsWithProfile(project.getId(), null)).thenReturn(2);

        mockMvc.perform(get("/api/alerts/countNew").param("prj", project.getExternalId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(2)));

        verify(alertsController).countNewAlertsWithProfile(project.getId(), null);
    }

    @Test
    public void createAlertPersistsCurrentAlertAndSendsNotification() throws Exception {
        Project project = builder.buildProject();
        when(projectsController.findProjectByExternalId(project.getExternalId())).thenReturn(project);

        Map<String, Map<String, String>> body = alertBody(project.getExternalId(), "metric");

        mockMvc.perform(post("/api/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(body)))
                .andExpect(status().isCreated());

        verify(alertsController).createAlert(0.4f, 0.5f, AlertType.ALERT_NOT_TREATED, project,
                "duplication", "metric");
        verify(simpleMessagingTemplate).convertAndSend(eq("/queue/notify"),
                ArgumentMatchers.any(Notification.class));
    }

    @Test
    public void createAlertRejectsUnknownAffectedType() throws Exception {
        Map<String, Map<String, String>> body = alertBody("test", "unknown");

        mockMvc.perform(post("/api/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    private Map<String, Map<String, String>> alertBody(String projectExternalId, String affectedType) {
        Map<String, String> element = new HashMap<>();
        element.put("affectedId", "duplication");
        element.put("affectedType", affectedType);
        element.put("type", "ALERT_NOT_TREATED");
        element.put("value", "0.4");
        element.put("threshold", "0.5");
        element.put("project_id", projectExternalId);

        Map<String, Map<String, String>> body = new HashMap<>();
        body.put("element", element);
        return body;
    }
}
