package org.falconframework.logging.adapter.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.falconframework.logging.config.HeaderConstant;
import org.slf4j.MDC;

public class FeignAdapter implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        initLoggingMdc(requestTemplate);
    }

    private void initLoggingMdc(RequestTemplate requestTemplate) {
        requestTemplate.header(HeaderConstant.TRACE_ID, MDC.get(HeaderConstant.TRACE_ID));
        requestTemplate.header(HeaderConstant.LOGGING_IGNORE, MDC.get(HeaderConstant.LOGGING_IGNORE));
    }

}
