package org.falconframework.logging.elk;

import lombok.Data;

import java.io.Serializable;

@Data
public class ElkLogging implements Serializable {

    /**
     * 日志内容
     */
    private String body;

    /**
     * 日志级别
     */
    private String level;

    /**
     * 日志id
     */
    private String traceId;

    /**
     * 应用名称
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
     * 机器ip
     */
    private String ip;

}
