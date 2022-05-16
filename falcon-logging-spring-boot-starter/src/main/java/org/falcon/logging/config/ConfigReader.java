package org.falcon.logging.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.falcon.common.util.ConfigUtil;
import org.falcon.logging.constant.ConfigConstant;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.text.MessageFormat;

@Slf4j
public class ConfigReader implements PropertySourceLocator {

    private static LoggingConfig loggingConfig;

    public static LoggingConfig getLoggingConfig() {
        return loggingConfig;
    }

    @Override
    public PropertySource<?> locate(Environment environment) {
        try {
            loggingConfig = loadLoggingConfig(environment);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            log.error("logging配置加载异常", e);
            System.exit(0);
        }
        return null;
    }

    public static LoggingConfig loadLoggingConfig(Environment environment) {
        String app = ConfigUtil.getString(environment, ConfigConstant.SPRING_APP, true, "unknown");
        String env = ConfigUtil.getString(environment, ConfigConstant.SPRING_ENV, true, "unknown");
        String searchIndex = ConfigUtil.getString(environment, ConfigConstant.SEARCH_INDEX, false, null);
        Boolean console = ConfigUtil.getBoolean(environment, ConfigConstant.CONSOLE, true, "false");
        Boolean debug = ConfigUtil.getBoolean(environment, ConfigConstant.DEBUG, true, "false");
        LoggingConfig.Kafka kafka = loadKafkaConfig(environment);
        LoggingConfig.Mail mail = loadMailConfig(environment);

        LoggingConfig loggingConfig = new LoggingConfig();
        loggingConfig.setApp(app);
        loggingConfig.setSearchIndex(searchIndex);
        loggingConfig.setEnv(env);
        loggingConfig.setConsole(console);
        loggingConfig.setDebug(debug);
        loggingConfig.setKafka(kafka);
        loggingConfig.setMail(mail);
        return loggingConfig;
    }

    private static LoggingConfig.Kafka loadKafkaConfig(Environment environment) {
        LoggingConfig.Kafka kafkaConfig = new LoggingConfig.Kafka();
        kafkaConfig.setServers(ConfigUtil.getString(environment, ConfigConstant.KAFKA_SERVERS, true, null));
        kafkaConfig.setTopic(ConfigUtil.getString(environment, ConfigConstant.KAFKA_TOPIC, true, null));
        kafkaConfig.setAcks(ConfigUtil.getString(environment, ConfigConstant.KAFKA_ACKS, false, "0"));
        kafkaConfig.setRetries(ConfigUtil.getInteger(environment, ConfigConstant.KAFKA_RETRIES, false, "1"));
        kafkaConfig.setCompressionType(ConfigUtil.getString(environment, ConfigConstant.KAFKA_RCOMPRESSION_TYPE, false, "gzip"));
        kafkaConfig.setBufferMemory(ConfigUtil.getInteger(environment, ConfigConstant.KAFKA_BUFFER_MEMORY, false, "33554432"));
        kafkaConfig.setBatchSize(ConfigUtil.getInteger(environment, ConfigConstant.KAFKA_BATCH_SIZE, false, "512000"));
        kafkaConfig.setLingerMs(ConfigUtil.getInteger(environment, ConfigConstant.KAFKA_LINGER_MS, false, "0"));
        kafkaConfig.setMaxRequestSize(ConfigUtil.getInteger(environment, ConfigConstant.KAFKA_MAX_REQUEST_SIZE, false, "1048576"));
        kafkaConfig.setRequestTimeoutMs(ConfigUtil.getInteger(environment, ConfigConstant.KAFKA_REQUEST_TIMEOUT_MS, false, "30000"));
        return kafkaConfig;
    }

    private static LoggingConfig.Mail loadMailConfig(Environment environment) {
        String host = environment.getProperty(ConfigConstant.MAIL_HOST);
        if (StringUtils.isBlank(host)) {
            return null;
        }
        Integer frequency = ConfigUtil.getInteger(environment, ConfigConstant.MAIL_FREQUENCY, false, null);
        if (frequency != null && frequency > 0) {
            if (StringUtils.isBlank(environment.getProperty("spring.redis.host"))
                    && StringUtils.isBlank(environment.getProperty("spirng.redis.cluster.nodes"))) {
                String error = MessageFormat.format("当配置了{0}时，必须配置RedisTemplate相关配置，保证RedisTemplate可用", ConfigConstant.MAIL_FREQUENCY);
                throw new IllegalArgumentException(error);
            }
        }
        String autoProtocol = host.substring(0, host.indexOf("."));

        LoggingConfig.Mail mailConfig = new LoggingConfig.Mail();
        mailConfig.setHost(host);
        mailConfig.setProtocol(ConfigUtil.getString(environment, ConfigConstant.MAIL_PROTOCOL, true, autoProtocol));
        mailConfig.setPort(ConfigUtil.getString(environment, ConfigConstant.MAIL_PORT, true, null));
        mailConfig.setAuth(ConfigUtil.getBoolean(environment, ConfigConstant.MAIL_AUTH, true, "true"));
        mailConfig.setDebug(ConfigUtil.getBoolean(environment, ConfigConstant.MAIL_DEBUG, true, "false"));
        mailConfig.setSenderAccount(ConfigUtil.getString(environment, ConfigConstant.MAIL_SENDER_ACCOUNT, true, null));
        mailConfig.setSenderPassword(ConfigUtil.getString(environment, ConfigConstant.MAIL_SENDER_PASSWORD, true, null));
        mailConfig.setReceiverAccounts(ConfigUtil.getStringArray(environment, ConfigConstant.MAIL_RECEIVER_ACCOUNTS, true, null));
        mailConfig.setFrequency(frequency);
        return mailConfig;
    }

}
