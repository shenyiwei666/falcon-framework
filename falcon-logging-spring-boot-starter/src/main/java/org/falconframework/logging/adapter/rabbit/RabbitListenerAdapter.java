package org.falconframework.logging.adapter.rabbit;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.falconframework.logging.constant.LoggingConstant;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;

import java.util.Map;

public class RabbitListenerAdapter implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        try {
            initLoggingMdc(methodInvocation);
            return methodInvocation.proceed();
        } finally {
            clearLoggingMdc();
        }
    }

    private void initLoggingMdc(MethodInvocation methodInvocation) {
        Message message = (Message) methodInvocation.getArguments()[1];
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        MDC.put(LoggingConstant.TRACE_ID, (String) headers.get(LoggingConstant.TRACE_ID));
        MDC.put(LoggingConstant.LOGGING_IGNORE, (String) headers.get(LoggingConstant.LOGGING_IGNORE));
    }

    private void clearLoggingMdc() {
        MDC.remove(LoggingConstant.TRACE_ID);
        MDC.remove(LoggingConstant.LOGGING_IGNORE);
    }

}
