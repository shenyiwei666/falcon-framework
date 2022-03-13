package org.falconframework.logging.adapter.servlet;

import org.falconframework.common.util.NetworkUtil;
import org.falconframework.logging.constant.LoggingConstant;
import org.falconframework.logging.util.TraceIdGenerator;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 功能说明
 *
 * @author 申益炜
 * @version 1.0.0
 * @date 2022/3/1
 */
public class ServletAdapter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            initLoggingMdc(servletRequest);
            initResponse(servletResponse);
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            clearLoggingMdc();
        }
    }

    private void initLoggingMdc(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String traceId = request.getHeader(LoggingConstant.TRACE_ID);
        if (traceId == null) {
            traceId = TraceIdGenerator.generate();
        }
        MDC.put(LoggingConstant.TRACE_ID, traceId);
    }

    private void initResponse(ServletResponse servletResponse) {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.addHeader(LoggingConstant.TRACE_ID, MDC.get(LoggingConstant.TRACE_ID));
        response.addHeader(LoggingConstant.SERVER_IP, NetworkUtil.getLocalIP());
    }

    private void clearLoggingMdc() {
        MDC.remove(LoggingConstant.TRACE_ID);
    }
}
