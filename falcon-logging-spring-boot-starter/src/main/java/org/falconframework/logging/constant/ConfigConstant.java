package org.falconframework.logging.constant;

import org.falconframework.logging.enums.GatherEnum;

/**
 * 日志配置常量
 *
 * @author 申益炜
 * @date 2021/12/6
 * @since 1.0.0
 */
public class ConfigConstant {

    /**
     * 配置：应用名称
     */
    public final static String CONFIG_APP = "spring.application.name";

    /**
     * 配置：运行环境
     */
    public final static String CONFIG_ENV = "spring.profiles.active";

    /**
     * 配置：日志采集方式
     *
     * @see GatherEnum
     */
    public final static String CONFIG_GATHER = "logging.falcon.gather";

    /**
     * 配置：kafka服务器地址
     */
    public final static String CONFIG_KAFKA_SERVERS = "logging.falcon.kafka.servers";

    /**
     * 配置：kafka日志topic
     */
    public final static String CONFIG_KAFKA_TOPIC = "logging.falcon.kafka.topic";

    public final static String CONFIG_KAFKA_ACKS = "logging.falcon.kafka.acks";

    public final static String CONFIG_KAFKA_RETRIES = "logging.falcon.kafka.retries";

    public final static String CONFIG_KAFKA_RCOMPRESSION_TYPE = "logging.falcon.kafka.compressionType";

    public final static String CONFIG_KAFKA_BUFFER_MEMORY = "logging.falcon.kafka.bufferMemory";

    public final static String CONFIG_KAFKA_BATCH_SIZE = "logging.falcon.kafka.batchSize";

    public final static String CONFIG_KAFKA_LINGER_MS = "logging.falcon.kafka.lingerMs";

    public final static String CONFIG_KAFKA_MAX_REQUEST_SIZE = "logging.falcon.kafka.maxRequestSize";

    public final static String CONFIG_KAFKA_REQUEST_TIMEOUT_MS = "logging.falcon.kafka.requestTimeoutMs";

}
