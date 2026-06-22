package com.upc.gessi.qrapids.app.config.security;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class SecurityConstantsTest {

    @Test
    public void exposesSwaggerDocumentationRoutesPublicly() {
        List<String> publicMatchers = Arrays.asList(SecurityConstants.PUBLIC_MATCHERS);

        assertTrue(publicMatchers.contains("/swagger-ui"));
        assertTrue(publicMatchers.contains("/swagger-ui.html"));
        assertTrue(publicMatchers.contains("/swagger-ui/**"));
        assertTrue(publicMatchers.contains("/v3/api-docs"));
        assertTrue(publicMatchers.contains("/v3/api-docs/**"));
        assertTrue(publicMatchers.contains("/swagger-resources/**"));
        assertTrue(publicMatchers.contains("/webjars/**"));
    }
}
