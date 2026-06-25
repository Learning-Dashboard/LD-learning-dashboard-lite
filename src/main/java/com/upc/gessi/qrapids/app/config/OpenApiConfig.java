package com.upc.gessi.qrapids.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String API_KEY_SCHEME = "LD-API-Key";

    @Bean
    public GroupedOpenApi learningDashboardApi() {
        return GroupedOpenApi.builder()
                .group("learning-dashboard-api")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public OpenAPI learningDashboardOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Learning Dashboard Lite API")
                        .description("OpenAPI documentation for the Learning Dashboard Lite REST API.")
                        .version("3.3"))
                .schemaRequirement(API_KEY_SCHEME, new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name("X-LD-API-Key"))
                .addSecurityItem(new SecurityRequirement().addList(API_KEY_SCHEME));
    }
}
