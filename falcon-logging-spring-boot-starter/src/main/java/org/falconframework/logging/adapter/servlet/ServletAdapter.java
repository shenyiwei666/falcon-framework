package org.falconframework.logging.adapter.servlet;

import org.falconframework.common.util.NetworkUtil;
import org.falconframework.logging.constant.LoggingConstant;
import org.falconframework.logging.util.TraceIdGenerator;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ServletAdapter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            initLoggingMdc(servletRequest);
            initResponseHeader(servletResponse);
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            clearLoggingMdc();
        }
    }

    private void initLoggingMdc(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        MDC.put(LoggingConstant.TRACE_ID, getTraceId(request));
        MDC.put(LoggingConstant.LOGGING_IGNORE, request.getHeader(LoggingConstant.LOGGING_IGNORE));
    }

    private void clearLoggingMdc() {
        MDC.remove(LoggingConstant.TRACE_ID);
        MDC.remove(LoggingConstant.LOGGING_IGNORE);
    }

    private String getTraceId(HttpServletRequest request) {
        String traceId = request.getHeader(LoggingConstant.TRACE_ID);
        if (traceId == null) {
            traceId = TraceIdGenerator.generate();
        }
        return traceId;
    }

    private void initResponseHeader(ServletResponse servletResponse) {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.addHeader(LoggingConstant.TRACE_ID, MDC.get(LoggingConstant.TRACE_ID));
        response.addHeader(LoggingConstant.SERVER_IP, NetworkUtil.getLocalIP());
    }

}
