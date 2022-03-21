package org.falconframework.logging.util;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.apache.commons.lang3.StringUtils;
import org.falconframework.common.util.NetworkUtil;
import org.falconframework.logging.config.LoggingConfig;
import org.falconframework.logging.constant.LoggingConstant;
import org.falconframework.logging.elk.ElkLogging;
import org.slf4j.MDC;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ElkLoggingBuilder {

    public static ElkLogging build(LoggingEvent event, LoggingConfig config) {
        String traceId = getTraceId(event);
        String body = getLoggingBody(event, traceId);
        String searchIndex = StringUtils.isBlank(config.getSearchIndex()) ? config.getApp() : config.getSearchIndex();

        ElkLogging elkLogging = new ElkLogging();
        elkLogging.setBody(body);
        elkLogging.setLevel(event.getLevel().toString());
        elkLogging.setTraceId(traceId);
        elkLogging.setApp(config.getApp());
        elkLogging.setSearchIndex(searchIndex);
        elkLogging.setEnv(config.getEnv());
        elkLogging.setIp(NetworkUtil.getLocalIP());
        return elkLogging;
    }

    private static String getTraceId(LoggingEvent event) {
        String traceId = null;

        Map<String, String> propertyMap = event.getMDCPropertyMap();
        if (propertyMap != null) {
            traceId = propertyMap.get(LoggingConstant.TRACE_ID);
        }

        if (traceId == null) {
            traceId = MDC.get(LoggingConstant.TRACE_ID);
        }

        if (traceId == null) {
            traceId = TraceIdGenerator.generate();
            MDC.put(LoggingConstant.TRACE_ID, traceId);
        }
        return traceId;
    }

    private static String getLoggingBody(LoggingEvent event, String traceId) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getTime(event));
        buffer.append(" | " + event.getLevel().toString());
        buffer.append(" | " + getThreadName(event));
        buffer.append(" | " + getLocation(event));
        buffer.append(" | " + traceId);
        buffer.append(" | " + event.getFormattedMessage());
        buffer.append(getThrowableBody(event));
        return buffer.toString();
    }

    private static String getLocation(LoggingEvent event) {
        String shortClassName = getShortClassName(event.getLoggerName());
        String lineNumber = getLineNumber(event);
        return shortClassName + ":" + lineNumber;
    }

    private static String getShortClassName(String className) {
        int threshold = 36;
        if (className.length() <= threshold) {
            return className;
        }
        String[] blocks = className.split("\\.");
        int residue = className.length() - threshold;
        for (int i = 0; i < blocks.length - 1 && residue > 0; i++) {
            String block = blocks[i];
            if (StringUtils.isNotBlank(block)) {
                blocks[i] = block.substring(0, 1);
                residue -= block.length() - 1;
            }
        }
        return String.join(".", blocks);
    }

    private static String getThreadName(LoggingEvent event) {
        // 会出现这种：org.springframework.amqp.rabbit.RabbitListenerEndpointContainer#0-1
        String eventThreadName = getShortClassName(event.getThreadName());
        String currentThreadName = Thread.currentThread().getName();
        return eventThreadName.equals(currentThreadName) ? eventThreadName : eventThreadName + "[" + currentThreadName + "]";
    }

    private static String getThrowableBody(LoggingEvent event) {
        ThrowableProxyConverter converter = new ThrowableProxyConverter();
        converter.start();
        String throwableBody = converter.convert(event);
        if (StringUtils.isBlank(throwableBody)) {
            return "";
        }
        return "\n" + throwableBody;
    }

    private static String getLineNumber(LoggingEvent event) {
        StackTraceElement element = getStackTraceElement(event);
        if (element == null) {
            return "";
        }
        return element.getLineNumber() + "";
    }

    private static StackTraceElement getStackTraceElement(LoggingEvent event) {
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

    private static String getTime(LoggingEvent event) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(new Date(event.getTimeStamp()));
    }

}
