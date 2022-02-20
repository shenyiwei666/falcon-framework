/*
 * 深圳市灵智数科有限公司版权所有.
 */
package org.falconframework.logging;

import org.apache.commons.lang3.StringUtils;
import org.falconframework.logging.config.LoggingConfig;
import org.falconframework.logging.enums.GatherEnum;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

/**
 * 日志上下文
 *
 * @author 申益炜
 * @version 1.0.0
 * @date 2021/12/6
 */
@Order
public class LoggingContext implements PropertySourceLocator {

    private static LoggingConfig loggingConfig;

    public static LoggingConfig getLoggingConfig() {
        return loggingConfig;
    }

    @Override
    public PropertySource<?> locate(Environment environment) {
        initConfig(environment);
        initStart();
        return null;
    }

    private void initConfig(Environment environment) {
        loggingConfig = new LoggingConfig();
        loggingConfig.setApplicationName(getString(environment, "spring.application.name"));
        loggingConfig.setGather(getString(environment, "logging.falcon.gather"));

        loggingConfig.setKafka(loadKafkaConfig(environment));
    }

    private LoggingConfig.Kafka loadKafkaConfig(Environment environment) {
        String gather = environment.getProperty("logging.falcon.gather");
        if (!GatherEnum.KAFKA.getValue().equals(gather)) {
            return null;
        }
        LoggingConfig.Kafka kafka = new LoggingConfig.Kafka();
        kafka.setServers(getString(environment, "logging.falcon.kafka.servers"));
        kafka.setTopic(getString(environment, "logging.falcon.kafka.topic"));
        kafka.setAcks(getString(environment, "logging.falcon.kafka.acks"));
        kafka.setRetries(getInteger(environment, "logging.falcon.kafka.retries"));
        kafka.setCompressionType(getString(environment, "logging.falcon.kafka.compressionType"));
        kafka.setBufferMemory(getInteger(environment, "logging.falcon.kafka.bufferMemory"));
        kafka.setBatchSize(getInteger(environment, "logging.falcon.kafka.batchSize"));
        kafka.setLingerMs(getInteger(environment, "logging.falcon.kafka.lingerMs"));
        kafka.setMaxRequestSize(getInteger(environment, "logging.falcon.kafka.maxRequestSize"));
        kafka.setRequestTimeoutMs(getInteger(environment, "logging.falcon.kafka.requestTimeoutMs"));
        return kafka;
    }

    private String getString(Environment environment, String key) {
        String value = environment.getProperty(key);
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException(key + "配置不能为空");
        }
        return value;
    }

    private Integer getInteger(Environment environment, String key) {
        String value = getString(environment, key);
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            throw new IllegalArgumentException(key + "只能配置为int类型");
        }
    }

    private void initStart() {
        String gather = LoggingContext.getLoggingConfig().getGather();
        if (GatherEnum.KAFKA.getValue().equals(gather)) {
            KafkaGather.getInstance().start(loggingConfig);
        }
    }

}
