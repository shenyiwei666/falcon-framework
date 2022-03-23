package org.falconframework.logging.adapter.rabbit;

import org.falconframework.logging.config.HeaderConstant;
import org.slf4j.MDC;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

import java.util.Map;

public class RabbitTemplateAdapter implements MessagePostProcessor {

    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        initLoggingMdc(message);
        return message;
    }

    private void initLoggingMdc(Message message) {
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        headers.put(HeaderConstant.TRACE_ID, MDC.get(HeaderConstant.TRACE_ID));
        headers.put(HeaderConstant.LOGGING_IGNORE, MDC.get(HeaderConstant.LOGGING_IGNORE));
    }

}
