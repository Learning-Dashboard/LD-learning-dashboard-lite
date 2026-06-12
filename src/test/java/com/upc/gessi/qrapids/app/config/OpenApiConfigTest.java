package com.upc.gessi.qrapids.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.Test;
import org.springdoc.core.GroupedOpenApi;

import static org.junit.Assert.assertEquals;

public class OpenApiConfigTest {

    private final OpenApiConfig openApiConfig = new OpenApiConfig();

    @Test
    public void createsLearningDashboardApiGroup() {
        GroupedOpenApi groupedOpenApi = openApiConfig.learningDashboardApi();

        assertEquals("learning-dashboard-api", groupedOpenApi.getGroup());
        assertEquals("/api/**", groupedOpenApi.getPathsToMatch().get(0));
    }

    @Test
    public void configuresOpenApiMetadata() {
        OpenAPI openAPI = openApiConfig.learningDashboardOpenApi();

        assertEquals("Learning Dashboard Lite API", openAPI.getInfo().getTitle());
        assertEquals("3.3", openAPI.getInfo().getVersion());
    }
}
