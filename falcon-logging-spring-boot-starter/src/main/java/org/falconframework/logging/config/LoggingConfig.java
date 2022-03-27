package org.falconframework.logging.config;

import lombok.Data;

@Data
public class LoggingConfig {

    /**
     * 应用程序名称
     */
    private String app;

    /**
     * es索引，如果为空则按app创建索引
     */
    private String searchIndex;

    /**
     * 当前环境
     */
    private String env;

    /**
     * 是否需要在控制台输出日志，values[true, false]
     */
    private Boolean console;

    /**
     * 是否开启调试模式志，values[true, false]
     */
    private Boolean debug;

    /**
     * kafka配置
     */
    private Kafka kafka;

    /**
     * 告警邮件配置
     */
    private Mail mail;

    @Data
    public static class Kafka {

        /**
         * broker连接地址，ip:端口,ip:端口
         */
        private String servers;

        /**
         * 接收日志的topic
         */
        private String topic;

        /**
         * 消息ack方式，默认为1：0，1，all或-1
         */
        private String acks;

        /**
         * 发送失败重试次数，默认为0
         */
        private Integer retries;

        /**
         * 消息压缩算法，默认为none不压缩： LZ4，Snappy，GZIP，none
         */
        private String compressionType;

        /**
         * producer缓冲区大小，单位是字节，默认为33554432字节（32MB）
         */
        private Integer bufferMemory;

        /**
         * 消息批量发送的大小，达到这个值才发送
         */
        private Integer batchSize;

        /**
         * 消息批量发送的间隔时间，达到这个值就发送
         */
        private Integer lingerMs;

        /**
         * 能够发送的最大消息大小，默认为1048576字节（1MB）
         */
        private Integer maxRequestSize;

        /**
         * 响应超时时间，默认为30秒
         */
        private Integer requestTimeoutMs;

    }

    @Data
    public static class Mail {

        /**
         * 邮件服务器地址
         */
        private String host;

        /**
         * 邮件发送协议，默认smtp
         */
        private String protocol;

        /**
         * 邮件服务器默认端口，默认25
         */
        private String port;

        /**
         * 是否需要验证用户名密码，values[true, false]，默认true
         */
        private Boolean auth;

        /**
         * 是否启用调试模式（启用调试模式可打印客户端与服务器交互过程时一问一答的响应消息），values[true, false]，默认false
         */
        private Boolean debug;

        /**
         * 发件人邮箱账号
         */
        private String senderAccount;

        /**
         * 发件人邮箱密码
         */
        private String senderPassword;

        /**
         * 收件人邮箱账号
         */
        private String[] receiverAccounts;

        /**
         * 同一错误日志多久内不能重复发送邮件，单位秒，默认不限制
         */
        private Integer frequency;

    }

}
