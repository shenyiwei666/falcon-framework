package org.falconframework.logging.adapter.rabbit;

import org.falconframework.logging.constant.LoggingConstant;
import org.slf4j.MDC;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

import java.util.Map;

/**
 * 功能说明
 *
 * @author 申益炜
 * @version 1.0.0
 * @date 2022/3/1
 */
public class RabbitTemplateAdapter implements MessagePostProcessor {

    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        initLoggingMdc(message);
        return message;
    }

    private void initLoggingMdc(Message message) {
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        headers.put(LoggingConstant.TRACE_ID, MDC.get(LoggingConstant.TRACE_ID));
    }

}
