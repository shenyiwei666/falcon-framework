package org.falconframework.logging.adapter.rabbit;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.falconframework.logging.constant.LoggingConstant;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;

import java.util.Map;

/**
 * 功能说明
 *
 * @author 申益炜
 * @version 1.0.0
 * @date 2022/3/1
 */
public class RabbitListenerAdapter implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object proceed;
        initLoggingMdc(methodInvocation);
        try {
            proceed = methodInvocation.proceed();
        } finally {
            clearLoggingMdc();
        }
        return proceed;
    }

    private void initLoggingMdc(MethodInvocation methodInvocation) {
        Message message = (Message) methodInvocation.getArguments()[1];
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        MDC.put(LoggingConstant.TRACE_ID, (String) headers.get(LoggingConstant.TRACE_ID));
    }

    private void clearLoggingMdc() {
        MDC.remove(LoggingConstant.TRACE_ID);
    }

}
