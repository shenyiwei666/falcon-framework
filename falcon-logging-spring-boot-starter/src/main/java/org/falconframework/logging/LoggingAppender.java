package org.falconframework.logging;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import org.falconframework.logging.config.ConfigReader;
import org.falconframework.logging.config.LoggingConfig;
import org.falconframework.logging.elk.ElkLogging;
import org.falconframework.logging.gather.LoggingGather;
import org.falconframework.logging.util.ElkLoggingBuilder;

public class LoggingAppender extends RollingFileAppender<LoggingEvent> {

    @Override
    protected void append(LoggingEvent event) {
        processLoggingEvent(event);
    }

    private void processLoggingEvent(LoggingEvent event) {
        LoggingConfig config = ConfigReader.getLoggingConfig();
        LoggingGather loggingGather = LoggingGather.getInstance(config.getGather());
        if (loggingGather == null) {
            return;
        }
        ElkLogging elkLogging = ElkLoggingBuilder.build(event, config);
        loggingGather.write(elkLogging);
    }

}
