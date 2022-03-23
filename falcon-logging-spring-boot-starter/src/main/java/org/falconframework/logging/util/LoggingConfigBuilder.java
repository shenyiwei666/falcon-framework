package org.falconframework.logging.util;

import org.apache.commons.lang3.StringUtils;
import org.falconframework.common.enums.BooleanEnum;
import org.falconframework.logging.config.ConfigConstant;
import org.falconframework.logging.config.LoggingConfig;
import org.falconframework.logging.gather.LoggingGatherEnum;
import org.springframework.core.env.Environment;

public class LoggingConfigBuilder {

    public static LoggingConfig build(Environment environment) {
        String app = getString(environment, ConfigConstant.APP, true, null);
        String searchIndex = getString(environment, ConfigConstant.SEARCH_INDEX, false, null);
        String env = getString(environment, ConfigConstant.ENV, true, null);
        String gather = getString(environment, ConfigConstant.GATHER, true, "kafka");
        Boolean console = getBoolean(environment, ConfigConstant.CONSOLE, true, "false");
        Boolean debug = getBoolean(environment, ConfigConstant.DEBUG, true, "false");

        LoggingConfig.KafkaConfig kafka = loadKafkaConfig(environment);
        LoggingConfig loggingConfig = new LoggingConfig();
        loggingConfig.setApp(app);
        loggingConfig.setSearchIndex(searchIndex);
        loggingConfig.setEnv(env);
        loggingConfig.setGather(gather);
        loggingConfig.setConsole(console);
        loggingConfig.setDebug(debug);
        loggingConfig.setKafka(kafka);
        return loggingConfig;
    }

    private static LoggingConfig.KafkaConfig loadKafkaConfig(Environment environment) {
        String gather = environment.getProperty(ConfigConstant.GATHER);
        String servers = getString(environment, ConfigConstant.KAFKA_SERVERS, true, null);
        String topic = getString(environment, ConfigConstant.KAFKA_TOPIC, true, null);
        String acks = getString(environment, ConfigConstant.KAFKA_ACKS, false, "0");
        Integer retries = getInteger(environment, ConfigConstant.KAFKA_RETRIES, false, "1");
        String compressionType = getString(environment, ConfigConstant.KAFKA_RCOMPRESSION_TYPE, false, "gzip");
        Integer bufferMemory = getInteger(environment, ConfigConstant.KAFKA_BUFFER_MEMORY, false, "33554432");
        Integer batchSize = getInteger(environment, ConfigConstant.KAFKA_BATCH_SIZE, false, "512000");
        Integer lingerMs = getInteger(environment, ConfigConstant.KAFKA_LINGER_MS, false, "0");
        Integer maxRequestSize = getInteger(environment, ConfigConstant.KAFKA_MAX_REQUEST_SIZE, false, "1048576");
        Integer requestTimeoutMs = getInteger(environment, ConfigConstant.KAFKA_REQUEST_TIMEOUT_MS, false, "30000");

        if (!LoggingGatherEnum.KAFKA.getValue().equals(gather)) {
            return null;
        }
        LoggingConfig.KafkaConfig kafka = new LoggingConfig.KafkaConfig();
        kafka.setServers(servers);
        kafka.setTopic(topic);
        kafka.setAcks(acks);
        kafka.setRetries(retries);
        kafka.setCompressionType(compressionType);
        kafka.setBufferMemory(bufferMemory);
        kafka.setBatchSize(batchSize);
        kafka.setLingerMs(lingerMs);
        kafka.setMaxRequestSize(maxRequestSize);
        kafka.setRequestTimeoutMs(requestTimeoutMs);
        return kafka;
    }

    private static String getString(Environment environment, String key, boolean required, String defaultValue) {
        String value = environment.getProperty(key);
        if (required && StringUtils.isBlank(value) && StringUtils.isBlank(defaultValue)) {
            throw new IllegalArgumentException(key + "配置不能为空");
        }
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    private static Integer getInteger(Environment environment, String key, boolean required, String defaultValue) {
        String value = getString(environment, key, required, defaultValue);
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            throw new IllegalArgumentException(key + "只能配置为int类型");
        }
    }

    private static Boolean getBoolean(Environment environment, String key, boolean required, String defaultValue) {
        String value = getString(environment, key, required, defaultValue);
        try {
            return BooleanEnum.getByValue(value).getBooleanValue();
        } catch (Exception e) {
            throw new IllegalArgumentException(key + "只能配置为boolean类型");
        }
    }

}
