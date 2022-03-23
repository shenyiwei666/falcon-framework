package org.falconframework.logging.adapter.servlet;

import org.falconframework.common.util.NetworkUtil;
import org.falconframework.logging.config.HeaderConstant;
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
        MDC.put(HeaderConstant.TRACE_ID, getTraceId(request));
        MDC.put(HeaderConstant.LOGGING_IGNORE, request.getHeader(HeaderConstant.LOGGING_IGNORE));
    }

    private void clearLoggingMdc() {
        MDC.remove(HeaderConstant.TRACE_ID);
        MDC.remove(HeaderConstant.LOGGING_IGNORE);
    }

    private String getTraceId(HttpServletRequest request) {
        String traceId = request.getHeader(HeaderConstant.TRACE_ID);
        if (traceId == null) {
            traceId = TraceIdGenerator.generate();
        }
        return traceId;
    }

    private void initResponseHeader(ServletResponse servletResponse) {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.addHeader(HeaderConstant.TRACE_ID, MDC.get(HeaderConstant.TRACE_ID));
        response.addHeader(HeaderConstant.SERVER_IP, NetworkUtil.getLocalIP());
    }

}
