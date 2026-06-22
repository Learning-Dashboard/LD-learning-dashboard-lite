package com.upc.gessi.qrapids.app.config.security;

import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.IOException;

import static com.upc.gessi.qrapids.app.config.security.SecurityConstants.COOKIE_STRING;
import static com.upc.gessi.qrapids.app.config.security.SecurityConstants.LD_API_KEY_HEADER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class LdApiKeyFilterTest {

    private static final String API_KEY = "test-service-api-key";

    @Test
    public void rejectsApiRequestWithoutApiKey() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/projects");
        MockHttpServletResponse response = runFilter(request);

        assertEquals(401, response.getStatus());
        assertEquals("{\"error\":\"Missing or invalid Learning Dashboard API key\"}", response.getContentAsString());
    }

    @Test
    public void rejectsApiRequestWithWrongApiKey() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/projects");
        request.addHeader(LD_API_KEY_HEADER, "wrong-key");
        MockHttpServletResponse response = runFilter(request);

        assertEquals(401, response.getStatus());
    }

    @Test
    public void allowsApiRequestWithValidApiKey() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/projects");
        request.addHeader(LD_API_KEY_HEADER, API_KEY);
        MockFilterChain chain = new MockFilterChain();
        MockHttpServletResponse response = runFilter(request, chain);

        assertEquals(200, response.getStatus());
        assertNotNull(chain.getRequest());
    }

    @Test
    public void allowsApiRequestWithSessionCookieForExistingJwtFlow() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/projects");
        request.setCookies(new Cookie(COOKIE_STRING, "jwt-token"));
        MockFilterChain chain = new MockFilterChain();
        MockHttpServletResponse response = runFilter(request, chain);

        assertEquals(200, response.getStatus());
        assertNotNull(chain.getRequest());
    }

    @Test
    public void allowsPublicApiEndpointWithoutApiKey() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/serverUrl");
        MockFilterChain chain = new MockFilterChain();
        MockHttpServletResponse response = runFilter(request, chain);

        assertEquals(200, response.getStatus());
        assertNotNull(chain.getRequest());
    }

    @Test
    public void allowsOptionsWithoutApiKey() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/api/projects");
        MockFilterChain chain = new MockFilterChain();
        MockHttpServletResponse response = runFilter(request, chain);

        assertEquals(200, response.getStatus());
        assertNotNull(chain.getRequest());
    }

    @Test(expected = IllegalStateException.class)
    public void rejectsBlankApiKeyWhenApiSecurityIsEnabled() {
        new LdApiKeyFilter(true, " ");
    }

    @Test
    public void allowsBlankApiKeyWhenApiSecurityIsDisabled() throws Exception {
        LdApiKeyFilter filter = new LdApiKeyFilter(false, " ");
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/projects");
        MockFilterChain chain = new MockFilterChain();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain);

        assertEquals(200, response.getStatus());
        assertNotNull(chain.getRequest());
    }

    private MockHttpServletResponse runFilter(MockHttpServletRequest request)
            throws ServletException, IOException {
        return runFilter(request, new MockFilterChain());
    }

    private MockHttpServletResponse runFilter(MockHttpServletRequest request, MockFilterChain chain)
            throws ServletException, IOException {
        LdApiKeyFilter filter = new LdApiKeyFilter(true, API_KEY);
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, chain);
        return response;
    }
}
