package org.falcon.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import org.falcon.common.enums.BooleanEnum;
import org.falcon.logging.alarm.LoggingAlarm;
import org.falcon.logging.config.ConfigReader;
import org.falcon.logging.config.LoggingConfig;
import org.falcon.logging.constant.LoggingConstant;
import org.falcon.logging.elk.ElkLogging;
import org.falcon.logging.elk.ElkLoggingBuilder;
import org.falcon.logging.elk.KafkaGather;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CloudAppender<E> extends ConsoleAppender<E> {

    private static ConcurrentLinkedQueue delayQueue = new ConcurrentLinkedQueue();

    private KafkaGather kafkaGather = new KafkaGather();

    @Override
    protected void append(E event) {
        try {
            processAppend(event);
            processDelayAppend();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processAppend(E event) {
        LoggingConfig config = ConfigReader.getLoggingConfig();
        // 配置加载器还未被执行
        if (config == null) {
            delayQueue.add(event);
            return;
        }

        String loggingIgnore = getMdcPropertyValue(event, LoggingConstant.LOGGING_IGNORE);
        // 开启了调试模式，并且请求的header参数中指定了不打印日志
        if (config.getDebug() && BooleanEnum.isTrue(loggingIgnore)) {
            return;
        }

        if (config.getConsole()) {
            super.append(event);
        }

        LoggingEvent loggingEvent = (LoggingEvent) event;
        ElkLogging elkLogging = ElkLoggingBuilder.build(loggingEvent, config);
        kafkaGather.write(elkLogging);

        if (Level.ERROR.levelStr.equals(elkLogging.getLevel())) {
            String loggingSign = ElkLoggingBuilder.getSign(loggingEvent);
            LoggingAlarm.alarm(elkLogging.getBody(), loggingSign);
        }
    }

    private String getMdcPropertyValue(E event, String mdcPropertyKey) {
        Map<String, String> mdcPropertyMap = ((LoggingEvent) event).getMDCPropertyMap();
        if (mdcPropertyMap != null) {
            return mdcPropertyMap.get(mdcPropertyKey);
        }
        return null;
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
