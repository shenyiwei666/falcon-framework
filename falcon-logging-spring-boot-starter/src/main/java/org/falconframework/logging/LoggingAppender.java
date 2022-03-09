package org.falconframework.logging;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import org.apache.commons.lang3.StringUtils;
import org.falconframework.common.util.NetworkUtil;
import org.falconframework.logging.constant.LoggingConstant;
import org.falconframework.logging.dto.ElkLogging;
import org.falconframework.logging.dto.LoggingConfig;
import org.falconframework.logging.gather.LoggingGather;
import org.falconframework.logging.util.LoggingUtil;
import org.slf4j.MDC;

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

    private LoggingConfig config = LoggingContext.getLoggingConfig();

    @Override
    protected void append(E event) {
        if (event instanceof LoggingEvent) {
            processLoggingEvent(event);
        }
    }

    private void processLoggingEvent(E event) {
        LoggingGather loggingGather = LoggingGather.getInstance(this.config.getGather());
        if (loggingGather == null) {
            return;
        }
        LoggingEvent loggingEvent = (LoggingEvent) event;
        ElkLogging elkLogging = buildElkLogging(loggingEvent);
        loggingGather.write(elkLogging);
    }

    private ElkLogging buildElkLogging(LoggingEvent event) {
        String traceId = getTraceId(event);
        String body = getLoggingBody(event, traceId);

        ElkLogging elkLogging = new ElkLogging();
        elkLogging.setBody(body);
        elkLogging.setLevel(event.getLevel().toString());
        elkLogging.setTraceId(traceId);
        elkLogging.setApp(this.config.getApp());
        elkLogging.setEnv(this.config.getEnv());
        elkLogging.setIp(NetworkUtil.getLocalIP());
        return elkLogging;
    }

    private String getTraceId(LoggingEvent event) {
        String traceId = null;

        Map<String, String> propertyMap = event.getMDCPropertyMap();
        if (propertyMap != null) {
            traceId = propertyMap.get(LoggingConstant.TRACE_ID);
        }

        if (traceId == null) {
            traceId = MDC.get(LoggingConstant.TRACE_ID);
        }

        if (traceId == null) {
            traceId = LoggingUtil.generateTraceId();
            MDC.put(LoggingConstant.TRACE_ID, traceId);
        }
        return traceId;
    }

    private String getLoggingBody(LoggingEvent event, String traceId) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getTime());
        buffer.append(" | " + event.getLevel().toString());
        buffer.append(" | " + getThreadName(event));
        buffer.append(" | " + event.getLoggerName() + ":" + getLineNumber(event));
        buffer.append(" | " + traceId);
        buffer.append(" | " + event.getFormattedMessage());
        buffer.append(getThrowableBody(event));
        return buffer.toString();
    }

    private String getThreadName(LoggingEvent event) {
        String eventThreadName = event.getThreadName();
        String currentThreadName = Thread.currentThread().getName();
        return eventThreadName.equals(currentThreadName) ? eventThreadName : eventThreadName + "[" + currentThreadName + "]";
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

    private String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(new Date());
    }

}
