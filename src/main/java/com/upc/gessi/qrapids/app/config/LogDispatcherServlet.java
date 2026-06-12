package com.upc.gessi.qrapids.app.config;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.upc.gessi.qrapids.app.config.libs.AuthTools;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.WebUtils;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.upc.gessi.qrapids.app.config.security.SecurityConstants.COOKIE_STRING;

public class LogDispatcherServlet extends DispatcherServlet {

    @Override
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.doDispatch(request, response);
        String logURL = createLogRequest(request);
        WebApplicationContext context = getWebApplicationContext();
        if (context == null) {
            logRequest(logURL, null);
            return;
        }
        AuthTools authTools = context.getBean(AuthTools.class);
        String cookie_token = authTools.getCookieToken( request, COOKIE_STRING );
        String username = authTools.getUser(cookie_token);
        logRequest(logURL, username);
    }

    private String createLogRequest(HttpServletRequest request) {
        String params;
        String contentType = request.getContentType();
        if (StringUtils.startsWithIgnoreCase(contentType, "multipart/")) {
            params = "multipart";
        }
        else {
            params = request.getParameterMap().entrySet().stream()
                    .map(entry -> entry.getKey() + ":" + Arrays.toString(entry.getValue()))
                    .collect(Collectors.joining(", "));
        }

        String dispatchType = (!DispatcherType.REQUEST.equals(request.getDispatcherType()) ?
                "\"" + request.getDispatcherType() + "\" dispatch for " : "");
        String message = (dispatchType + request.getMethod() + " \"" + getRequestUri(request) +
            "\", parameters={" + params + "}");

        if (logger.isTraceEnabled()) {
            List<String> values = Collections.list(request.getHeaderNames());
            String headers = values.size() > 0 ? "masked" : "";
            if (isEnableLoggingRequestDetails()) {
                headers = values.stream().map(name -> name + ":" + Collections.list(request.getHeaders(name)))
                        .collect(Collectors.joining(", "));
            }
            return message + ", headers={" + headers + "} in DispatcherServlet '" + getServletName() + "'";
        }
        else return message;
    }

    private void logRequest(String message, String username) {
        if (username != null) {
            LogFormatUtils.traceDebug(logger, traceOn ->
                    message + ", Action performed by "
                    + username);
        }
        else {
            LogFormatUtils.traceDebug(logger, traceOn ->
                    message + ", No user is logged in yet");
        }
    }

    private static String getRequestUri(HttpServletRequest request) {
        String uri = (String) request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE);
        if (uri == null) uri = request.getRequestURI();
        return uri;
    }

}
