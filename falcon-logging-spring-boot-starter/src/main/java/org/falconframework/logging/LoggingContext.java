package org.falconframework.logging;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.falconframework.logging.dto.LoggingConfig;
import org.falconframework.logging.constant.ConfigConstant;
import org.falconframework.logging.enums.GatherEnum;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

/**
 * 日志上下文
 *
 * @author 申益炜
 * @version 1.0.0
 * @date 2021/12/6
 */
public class LoggingContext implements PropertySourceLocator {

    private static LoggingConfig loggingConfig;

    public static LoggingConfig getLoggingConfig() {
        return JSON.parseObject(JSON.toJSONString(loggingConfig), LoggingConfig.class);
    }

    @Override
    public PropertySource<?> locate(Environment environment) {
        initLoggingConfig(environment);
        return null;
    }

    private void initLoggingConfig(Environment environment) {
        LoggingConfig.KafkaConfig kafka = loadKafkaConfig(environment);
        loggingConfig = new LoggingConfig();
        loggingConfig.setApp(getString(environment, ConfigConstant.CONFIG_APP));
        loggingConfig.setEnv(getString(environment, ConfigConstant.CONFIG_ENV));
        loggingConfig.setGather(getString(environment, ConfigConstant.CONFIG_GATHER));
        loggingConfig.setKafka(kafka);
    }

    private LoggingConfig.KafkaConfig loadKafkaConfig(Environment environment) {
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

}
