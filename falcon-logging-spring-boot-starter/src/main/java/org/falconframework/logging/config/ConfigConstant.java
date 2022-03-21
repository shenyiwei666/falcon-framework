package org.falconframework.logging.config;

import org.falconframework.logging.gather.LoggingGatherEnum;

public class ConfigConstant {

    /**
     * 运行环境
     */
    public final static String ENV = "spring.profiles.active";

    /**
     * 应用名称
     */
    public final static String APP = "spring.application.name";

    /**
     * es索引，如果为空则按app创建索引
     */
    public final static String SEARCH_INDEX = "logging.falcon.searchIndex";

    /**
     * 是否需要在控制台输出日志（boolean值）
     */
    public final static String CONSOLE = "logging.falcon.console";

    /**
     * 日志采集方式
     *
     * @see LoggingGatherEnum
     */
    public final static String GATHER = "logging.falcon.gather";

    /**
     * kafka服务器地址
     */
    public final static String KAFKA_SERVERS = "logging.falcon.kafka.servers";

    /**
     * kafka日志topic
     */
    public final static String KAFKA_TOPIC = "logging.falcon.kafka.topic";

    public final static String KAFKA_ACKS = "logging.falcon.kafka.acks";

    public final static String KAFKA_RETRIES = "logging.falcon.kafka.retries";

    public final static String KAFKA_RCOMPRESSION_TYPE = "logging.falcon.kafka.compressionType";

    public final static String KAFKA_BUFFER_MEMORY = "logging.falcon.kafka.bufferMemory";

    public final static String KAFKA_BATCH_SIZE = "logging.falcon.kafka.batchSize";

    public final static String KAFKA_LINGER_MS = "logging.falcon.kafka.lingerMs";

    public final static String KAFKA_MAX_REQUEST_SIZE = "logging.falcon.kafka.maxRequestSize";

    public final static String KAFKA_REQUEST_TIMEOUT_MS = "logging.falcon.kafka.requestTimeoutMs";

}
