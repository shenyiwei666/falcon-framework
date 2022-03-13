package org.falconframework.logging;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import org.falconframework.logging.dto.ElkLogging;
import org.falconframework.logging.dto.LoggingConfig;
import org.falconframework.logging.gather.LoggingGather;
import org.falconframework.logging.util.ElkLoggingBuilder;

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
        LoggingConfig config = LoggingContext.getLoggingConfig();
        LoggingGather loggingGather = LoggingGather.getInstance(config.getGather());
        if (loggingGather == null) {
            return;
        }
        LoggingEvent loggingEvent = (LoggingEvent) event;
        ElkLogging elkLogging = ElkLoggingBuilder.build(loggingEvent, config);
        loggingGather.write(elkLogging);
    }

}
