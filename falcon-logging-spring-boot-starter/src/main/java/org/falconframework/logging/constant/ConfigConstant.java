package org.falconframework.logging.constant;

public class ConfigConstant {

    /**
     * 运行环境
     */
    public final static String SPRING_ENV = "spring.profiles.active";

    /**
     * 应用名称
     */
    public final static String SPRING_APP = "spring.application.name";

    /**
     * es索引，如果为空则按app创建索引
     */
    public final static String SEARCH_INDEX = "logging.falcon.searchIndex";

    /**
     * 是否需要在控制台输出日志，values[true, false]
     */
    public final static String CONSOLE = "logging.falcon.console";

    /**
     * 是否开启调试模式志，values[true, false]
     */
    public final static String DEBUG = "logging.falcon.debug";

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



    /**
     * 邮件服务器地址
     */
    public final static String MAIL_HOST = "logging.falcon.mail.host";

    /**
     * 邮件发送协议
     */
    public final static String MAIL_PROTOCOL = "logging.falcon.mail.protocol";

    /**
     * 邮件服务器默认端口
     */
    public final static String MAIL_PORT = "logging.falcon.mail.port";

    /**
     * 是否需要验证用户名密码，values[true, false]，默认true
     */
    public final static String MAIL_AUTH = "logging.falcon.mail.auth";

    /**
     * 是否启用调试模式（启用调试模式可打印客户端与服务器交互过程时一问一答的响应消息），默认false
     */
    public final static String MAIL_DEBUG = "logging.falcon.mail.debug";

    /**
     * 发件人邮箱账号
     */
    public final static String MAIL_SENDER_ACCOUNT = "logging.falcon.mail.senderAccount";

    /**
     * 发件人邮箱密码
     */
    public final static String MAIL_SENDER_PASSWORD = "logging.falcon.mail.senderPassword";

    /**
     * 收件人邮箱账号，多个使用英文逗号分隔
     */
    public final static String MAIL_RECEIVER_ACCOUNTS = "logging.falcon.mail.receiverAccounts";

    /**
     * 同一错误日志多久内不能重复发送邮件，单位秒，默认不限制
     */
    public final static String MAIL_FREQUENCY = "logging.falcon.mail.frequency";

}
