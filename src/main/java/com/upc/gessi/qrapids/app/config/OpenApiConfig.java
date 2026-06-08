package com.upc.gessi.qrapids.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.core.SpringDocConfiguration;
import org.springdoc.core.SwaggerUiConfigProperties;
import org.springdoc.core.SwaggerUiOAuthProperties;
import org.springdoc.webmvc.core.MultipleOpenApiSupportConfiguration;
import org.springdoc.webmvc.core.SpringDocWebMvcConfiguration;
import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        SpringDocConfiguration.class,
        SpringDocConfigProperties.class,
        SpringDocWebMvcConfiguration.class,
        MultipleOpenApiSupportConfiguration.class,
        SwaggerConfig.class,
        SwaggerUiConfigProperties.class,
        SwaggerUiOAuthProperties.class
})
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi learningDashboardApi() {
        return GroupedOpenApi.builder()
                .setGroup("learning-dashboard-api")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public OpenAPI learningDashboardOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Learning Dashboard Lite API")
                        .description("OpenAPI documentation for the Learning Dashboard Lite REST API.")
                        .version("3.3"));
    }
}
