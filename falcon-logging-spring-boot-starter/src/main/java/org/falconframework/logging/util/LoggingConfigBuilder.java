package org.falconframework.logging.util;

import org.apache.commons.lang3.StringUtils;
import org.falconframework.logging.config.ConfigConstant;
import org.falconframework.logging.config.LoggingConfig;
import org.falconframework.logging.gather.LoggingGatherEnum;
import org.springframework.core.env.Environment;

public class LoggingConfigBuilder {

    public static LoggingConfig build(Environment environment) {
        LoggingConfig.KafkaConfig kafka = loadKafkaConfig(environment);
        LoggingConfig loggingConfig = new LoggingConfig();
        loggingConfig.setApp(getString(environment, ConfigConstant.APP));
        loggingConfig.setEnv(getString(environment, ConfigConstant.ENV));
        loggingConfig.setGather(getString(environment, ConfigConstant.GATHER));
        loggingConfig.setKafka(kafka);
        return loggingConfig;
    }

    private static LoggingConfig.KafkaConfig loadKafkaConfig(Environment environment) {
        String gather = environment.getProperty(ConfigConstant.GATHER);
        if (!LoggingGatherEnum.KAFKA.getValue().equals(gather)) {
            return null;
        }
        LoggingConfig.KafkaConfig kafka = new LoggingConfig.KafkaConfig();
        kafka.setServers(getString(environment, ConfigConstant.KAFKA_SERVERS));
        kafka.setTopic(getString(environment, ConfigConstant.KAFKA_TOPIC));
        kafka.setAcks(getString(environment, ConfigConstant.KAFKA_ACKS));
        kafka.setRetries(getInteger(environment, ConfigConstant.KAFKA_RETRIES));
        kafka.setCompressionType(getString(environment, ConfigConstant.KAFKA_RCOMPRESSION_TYPE));
        kafka.setBufferMemory(getInteger(environment, ConfigConstant.KAFKA_BUFFER_MEMORY));
        kafka.setBatchSize(getInteger(environment, ConfigConstant.KAFKA_BATCH_SIZE));
        kafka.setLingerMs(getInteger(environment, ConfigConstant.KAFKA_LINGER_MS));
        kafka.setMaxRequestSize(getInteger(environment, ConfigConstant.KAFKA_MAX_REQUEST_SIZE));
        kafka.setRequestTimeoutMs(getInteger(environment, ConfigConstant.KAFKA_REQUEST_TIMEOUT_MS));
        return kafka;
    }

    private static String getString(Environment environment, String key) {
        String value = environment.getProperty(key);
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException(key + "配置不能为空");
        }
        return value;
    }

    private static Integer getInteger(Environment environment, String key) {
        String value = getString(environment, key);
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            throw new IllegalArgumentException(key + "只能配置为int类型");
        }
    }

}
