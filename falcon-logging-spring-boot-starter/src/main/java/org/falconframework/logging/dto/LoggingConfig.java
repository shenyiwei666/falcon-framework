package org.falconframework.logging.dto;

import lombok.Data;
import org.falconframework.logging.enums.GatherEnum;

/**
 * 日志配置
 *
 * @author 申益炜
 * @date 2021/12/6
 * @since 1.0.0
 */
@Data
public class LoggingConfig {

    /**
     * 应用程序名称
     */
    private String app;

    /**
     * 当前环境
     */
    private String env;

    /**
     * 日志采集方式
     * @see GatherEnum
     */
    private String gather;

    /**
     * kafka配置
     */
    private KafkaConfig kafka;

    @Data
    public static class KafkaConfig {

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
         *  producer缓冲区大小，单位是字节，默认为33554432字节（32MB）
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


}
