package org.falconframework.logging.adapter.rest;

import org.falconframework.logging.constant.LoggingConstant;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * 功能说明
 *
 * @author 申益炜
 * @version 1.0.0
 * @date 2022/3/4
 */
public class RestTemplateAdapter implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        initLoggingMdc(request);
        return execution.execute(request, body);
    }

    private void initLoggingMdc(HttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        headers.add(LoggingConstant.TRACE_ID, MDC.get(LoggingConstant.TRACE_ID));
    }

}
