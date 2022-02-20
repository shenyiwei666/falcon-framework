/*
 * 深圳市灵智数科有限公司版权所有.
 */
package org.falconframework.logging;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import org.apache.commons.lang3.StringUtils;
import org.falconframework.logging.constant.LoggingConstant;
import org.falconframework.logging.enums.GatherEnum;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * FalconLogAppender
 *
 * @author 申益炜
 * @date 2021/12/6
 * @since 1.0.0
 */
public class LoggingAppender<E> extends RollingFileAppender<E> {

    @Override
    protected void append(E event) {
        if (event instanceof LoggingEvent) {
            processLoggingEvent(event);
        }
    }

    private void processLoggingEvent(E event) {
        LoggingEvent loggingEvent = (LoggingEvent) event;
        String gather = LoggingContext.getLoggingConfig().getGather();
        if (GatherEnum.KAFKA.getValue().equals(gather)) {
            String loggingBody = getLoggingBody(loggingEvent);
            KafkaGather.getInstance().write(loggingBody);
        }
    }

    private String getLoggingBody(LoggingEvent event) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getTime());
        buffer.append("|" + event.getLevel().toString());
        buffer.append("|" + event.getThreadName());
        buffer.append("|" + event.getLoggerName() + ":" + getLineNumber(event));
        buffer.append("|" + getTraceId(event));
        buffer.append("|" + event.getFormattedMessage());
        buffer.append(getThrowableBody(event));
        return buffer.toString();
    }

    private String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(new Date());
    }

    private String getThrowableBody(LoggingEvent event) {
        ThrowableProxyConverter converter = new ThrowableProxyConverter();
        converter.start();
        String throwableBody = converter.convert(event);
        if (StringUtils.isBlank(throwableBody)) {
            return "";
        }
        return "\n" + throwableBody;
    }

    private String getLineNumber(LoggingEvent event) {
        StackTraceElement element = getStackTraceElement(event);
        if (element == null) {
            return "";
        }
        return element.getLineNumber() + "";
    }

    private StackTraceElement getStackTraceElement(LoggingEvent event) {
        StackTraceElement[] stackTraceElements = event.getCallerData();
        if (stackTraceElements == null) {
            return null;
        }
        for (int i = 0; i < stackTraceElements.length; ++i) {
            StackTraceElement element = stackTraceElements[i];
            if (element.getClassName().equals(event.getLoggerName())) {
                return element;
            }
        }
        return null;
    }

    private String getTraceId(LoggingEvent event) {
        Map<String, String> propertyMap = event.getMDCPropertyMap();
        if (propertyMap == null || propertyMap.isEmpty()) {
            return "";
        }
        String traceId = propertyMap.get(LoggingConstant.TRACE_ID);
        return traceId == null ? "" : traceId;
    }

}
