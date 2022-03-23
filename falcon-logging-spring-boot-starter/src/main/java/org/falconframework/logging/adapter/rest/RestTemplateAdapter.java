package org.falconframework.logging.adapter.rest;

import org.falconframework.logging.config.HeaderConstant;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class RestTemplateAdapter implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        initLoggingMdc(request);
        return execution.execute(request, body);
    }

    private void initLoggingMdc(HttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        headers.add(HeaderConstant.TRACE_ID, MDC.get(HeaderConstant.TRACE_ID));
        headers.add(HeaderConstant.LOGGING_IGNORE, MDC.get(HeaderConstant.LOGGING_IGNORE));
    }

}
