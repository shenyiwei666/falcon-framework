package org.falconframework.logging.util;

import org.apache.commons.lang3.StringUtils;
import org.falconframework.logging.constant.ConfigConstant;
import org.falconframework.logging.dto.LoggingConfig;
import org.falconframework.logging.enums.GatherEnum;
import org.springframework.core.env.Environment;

/**
 * 构建LoggingConfig
 *
 * @author 申益炜
 * @version 1.0.0
 * @date 2022/3/11
 */
public class LoggingConfigBuilder {

    public static LoggingConfig build(Environment environment) {
        LoggingConfig.KafkaConfig kafka = loadKafkaConfig(environment);
        LoggingConfig loggingConfig = new LoggingConfig();
        loggingConfig.setApp(getString(environment, ConfigConstant.CONFIG_APP));
        loggingConfig.setEnv(getString(environment, ConfigConstant.CONFIG_ENV));
        loggingConfig.setGather(getString(environment, ConfigConstant.CONFIG_GATHER));
        loggingConfig.setKafka(kafka);
        return loggingConfig;
    }

    private static LoggingConfig.KafkaConfig loadKafkaConfig(Environment environment) {
        String gather = environment.getProperty(ConfigConstant.CONFIG_GATHER);
        if (!GatherEnum.KAFKA.getValue().equals(gather)) {
            return null;
        }
        LoggingConfig.KafkaConfig kafka = new LoggingConfig.KafkaConfig();
        kafka.setServers(getString(environment, ConfigConstant.CONFIG_KAFKA_SERVERS));
        kafka.setTopic(getString(environment, ConfigConstant.CONFIG_KAFKA_TOPIC));
        kafka.setAcks(getString(environment, ConfigConstant.CONFIG_KAFKA_ACKS));
        kafka.setRetries(getInteger(environment, ConfigConstant.CONFIG_KAFKA_RETRIES));
        kafka.setCompressionType(getString(environment, ConfigConstant.CONFIG_KAFKA_RCOMPRESSION_TYPE));
        kafka.setBufferMemory(getInteger(environment, ConfigConstant.CONFIG_KAFKA_BUFFER_MEMORY));
        kafka.setBatchSize(getInteger(environment, ConfigConstant.CONFIG_KAFKA_BATCH_SIZE));
        kafka.setLingerMs(getInteger(environment, ConfigConstant.CONFIG_KAFKA_LINGER_MS));
        kafka.setMaxRequestSize(getInteger(environment, ConfigConstant.CONFIG_KAFKA_MAX_REQUEST_SIZE));
        kafka.setRequestTimeoutMs(getInteger(environment, ConfigConstant.CONFIG_KAFKA_REQUEST_TIMEOUT_MS));
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
