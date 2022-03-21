package org.falconframework.logging;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import org.falconframework.logging.config.ConfigReader;
import org.falconframework.logging.config.LoggingConfig;
import org.falconframework.logging.elk.ElkLogging;
import org.falconframework.logging.gather.LoggingGather;
import org.falconframework.logging.util.ElkLoggingBuilder;

import java.util.concurrent.ConcurrentLinkedQueue;

public class CloudAppender<E> extends ConsoleAppender<E> {

    private static ConcurrentLinkedQueue delayQueue = new ConcurrentLinkedQueue();

    @Override
    protected void append(E event) {
        processAppend(event);
        processDelayAppend();
    }

    private void processAppend(E event) {
        LoggingConfig config = ConfigReader.getLoggingConfig();
        // 配置加载器还未被执行
        if (config == null) {
            delayQueue.add(event);
            return;
        }
        appendGather(event, config);
        appendConsole(event, config);
    }

    private void appendGather(E event, LoggingConfig config) {
        LoggingGather loggingGather = LoggingGather.getInstance(config.getGather());
        if (loggingGather == null) {
            return;
        }
        LoggingEvent loggingEvent = (LoggingEvent) event;
        ElkLogging elkLogging = ElkLoggingBuilder.build(loggingEvent, config);
        loggingGather.write(elkLogging);
    }

    private void appendConsole(E event, LoggingConfig config) {
        if (config.getConsole()) {
            super.append(event);
        }
    }

    private void processDelayAppend() {
        if (delayQueue.isEmpty()) {
            return;
        }
        LoggingConfig config = ConfigReader.getLoggingConfig();
        if (config == null) {
            return;
        }
        while (!delayQueue.isEmpty()) {
            processAppend((E) delayQueue.poll());
        }
    }

}