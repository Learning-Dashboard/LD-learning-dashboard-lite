package com.upc.gessi.qrapids.app.domain.controllers;

import com.upc.gessi.qrapids.app.domain.exceptions.MetricNotFoundException;
import com.upc.gessi.qrapids.app.domain.models.Alert;
import com.upc.gessi.qrapids.app.domain.models.AlertStatus;
import com.upc.gessi.qrapids.app.domain.models.AlertType;
import com.upc.gessi.qrapids.app.domain.models.Metric;
import com.upc.gessi.qrapids.app.domain.models.Project;
import com.upc.gessi.qrapids.app.domain.repositories.Alert.AlertRepository;
import com.upc.gessi.qrapids.app.domain.repositories.Metric.MetricRepository;
import com.upc.gessi.qrapids.app.testHelpers.DomainObjectsBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AlertsControllerTest {

    private DomainObjectsBuilder builder;

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private MetricRepository metricRepository;

    @InjectMocks
    private AlertsController alertsController;

    @Before
    public void setUp() {
        builder = new DomainObjectsBuilder();
    }

    @Test
    public void createAlertPersistsCurrentAlertForExistingMetric() throws Exception {
        Project project = builder.buildProject();
        Metric metric = builder.buildMetric(project);
        when(metricRepository.findByExternalIdAndProjectId(metric.getExternalId(), project.getId()))
                .thenReturn(metric);

        alertsController.createAlert(0.4f, 0.5f, AlertType.TRESPASSED_THRESHOLD, project,
                metric.getExternalId(), "metric");

        ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepository).save(captor.capture());
        Alert saved = captor.getValue();
        assertEquals(AlertType.TRESPASSED_THRESHOLD, saved.getType());
        assertEquals(AlertStatus.NEW, saved.getStatus());
        assertEquals(project, saved.getProject());
        assertEquals(metric.getExternalId(), saved.getAffectedId());
        assertEquals("metric", saved.getAffectedType());
        assertEquals(0.4f, saved.getValue(), 0f);
        assertEquals(0.5f, saved.getThreshold(), 0f);
    }

    @Test(expected = MetricNotFoundException.class)
    public void createAlertRejectsMissingMetric() throws Exception {
        Project project = builder.buildProject();

        alertsController.createAlert(0.4f, 0.5f, AlertType.TRESPASSED_THRESHOLD, project,
                "unknown", "metric");
    }

    @Test
    public void getAllProjectAlertsWithoutProfileReturnsRepositoryAlerts() {
        List<Alert> alerts = Arrays.asList(builder.buildAlert(builder.buildProject()));
        when(alertRepository.findAllByProjectId(1L)).thenReturn(alerts);

        List<Alert> result = alertsController.getAllProjectAlertsWithProfile(1L, null);

        assertSame(alerts, result);
    }

    @Test
    public void obtainMostRecentAlertPrefersNewerAlert() {
        Alert older = builder.buildAlert(builder.buildProject());
        older.setDate(new Date(1000L));
        Alert newer = builder.buildAlert(builder.buildProject());
        newer.setDate(new Date(2000L));

        Alert result = alertsController.obtainMostRecentAlert(older, newer);

        assertSame(newer, result);
    }

    @Test
    public void obtainMostRecentAlertReturnsNullWhenBothAreNull() {
        assertNull(alertsController.obtainMostRecentAlert(null, null));
    }
}
