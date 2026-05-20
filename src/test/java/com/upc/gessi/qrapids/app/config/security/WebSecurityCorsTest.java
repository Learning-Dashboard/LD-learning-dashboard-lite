package com.upc.gessi.qrapids.app.config.security;

import org.junit.Test;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class WebSecurityCorsTest {

    @Test
    public void splitsCommaSeparatedValuesAndIgnoresEmptyItems() {
        assertEquals(
                Arrays.asList("http://localhost:3000", "https://dashboard.example.com"),
                WebSecurity.splitCommaSeparatedValues(" http://localhost:3000, ,https://dashboard.example.com "));
    }

    @Test
    public void emptyCorsOriginListDoesNotDefaultToWildcard() {
        CorsConfiguration configuration = WebSecurity.buildCorsConfiguration(
                "",
                "GET,POST,OPTIONS",
                "Authorization,Content-Type",
                "Authorization",
                false);

        assertEquals(Collections.emptyList(), configuration.getAllowedOrigins());
        assertFalse(configuration.getAllowedOrigins().contains("*"));
    }

    @Test
    public void buildsCorsConfigurationFromConfiguredValues() {
        CorsConfiguration configuration = WebSecurity.buildCorsConfiguration(
                "http://localhost:3000,https://frontend.example.com",
                "GET,POST,PUT,DELETE,OPTIONS",
                "Authorization,Content-Type,X-LD-API-Key",
                "Authorization,Location",
                true);

        assertEquals(
                Arrays.asList("http://localhost:3000", "https://frontend.example.com"),
                configuration.getAllowedOrigins());
        assertEquals(
                Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"),
                configuration.getAllowedMethods());
        assertEquals(
                Arrays.asList("Authorization", "Content-Type", "X-LD-API-Key"),
                configuration.getAllowedHeaders());
        assertEquals(Arrays.asList("Authorization", "Location"), configuration.getExposedHeaders());
        assertEquals(Boolean.TRUE, configuration.getAllowCredentials());
    }

    @Test(expected = IllegalStateException.class)
    public void rejectsWildcardOriginWhenCredentialsAreEnabled() {
        WebSecurity.buildCorsConfiguration(
                "*",
                "GET",
                "Authorization",
                "Authorization",
                true);
    }
}
