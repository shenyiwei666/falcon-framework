package org.falconframework.logging.adapter.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.falconframework.logging.constant.LoggingConstant;
import org.slf4j.MDC;

/**
 * 功能说明
 *
 * @author 申益炜
 * @version 1.0.0
 * @date 2022/3/1
 */
public class FeignAdapter implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        initLoggingMdc(requestTemplate);
    }

    private void initLoggingMdc(RequestTemplate requestTemplate) {
        requestTemplate.header(LoggingConstant.TRACE_ID, MDC.get(LoggingConstant.TRACE_ID));
    }

}
