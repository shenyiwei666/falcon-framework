package org.falconframework.logging.adapter.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.falconframework.logging.constant.LoggingConstant;
import org.slf4j.MDC;

public class FeignAdapter implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        initLoggingMdc(requestTemplate);
    }

    private void initLoggingMdc(RequestTemplate requestTemplate) {
        requestTemplate.header(LoggingConstant.TRACE_ID, MDC.get(LoggingConstant.TRACE_ID));
    }

}
