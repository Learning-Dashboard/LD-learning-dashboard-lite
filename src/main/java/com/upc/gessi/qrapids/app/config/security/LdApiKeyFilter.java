package com.upc.gessi.qrapids.app.config.security;

import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.upc.gessi.qrapids.app.config.security.SecurityConstants.COOKIE_STRING;
import static com.upc.gessi.qrapids.app.config.security.SecurityConstants.LD_API_KEY_HEADER;

public class LdApiKeyFilter extends OncePerRequestFilter {

    private static final Set<String> PUBLIC_API_PATHS = new HashSet<>(Arrays.asList(
            "/api/serverUrl",
            "/api/assessSIUrl"
    ));

    private final boolean apiEnabled;
    private final String expectedApiKey;

    public LdApiKeyFilter(boolean apiEnabled, String expectedApiKey) {
        this.apiEnabled = apiEnabled;
        this.expectedApiKey = expectedApiKey == null ? "" : expectedApiKey.trim();

        if (apiEnabled && this.expectedApiKey.isEmpty()) {
            throw new IllegalStateException("LD_API_KEY must be configured when security.api.enable is true");
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!requiresApiKey(request) || hasSessionCookie(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String providedApiKey = request.getHeader(LD_API_KEY_HEADER);
        if (!matchesExpectedApiKey(providedApiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\":\"Missing or invalid Learning Dashboard API key\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean requiresApiKey(HttpServletRequest request) {
        if (!apiEnabled || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return false;
        }

        String path = requestPath(request);
        return path.startsWith("/api/") && !PUBLIC_API_PATHS.contains(path);
    }

    private String requestPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();

        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            return path.substring(contextPath.length());
        }

        return path;
    }

    private boolean hasSessionCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return false;
        }

        for (Cookie cookie : cookies) {
            if (COOKIE_STRING.equals(cookie.getName())
                    && cookie.getValue() != null
                    && !cookie.getValue().isEmpty()) {
                return true;
            }
        }

        return false;
    }

    private boolean matchesExpectedApiKey(String providedApiKey) {
        if (providedApiKey == null || providedApiKey.isEmpty()) {
            return false;
        }

        return MessageDigest.isEqual(sha256(expectedApiKey), sha256(providedApiKey));
    }

    private byte[] sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 digest is not available", e);
        }
    }
}
