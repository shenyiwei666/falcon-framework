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
     * 应用程序名称
     */
    private String app;

    /**
     * 当前环境
     */
    private String env;

    /**
     * 机器ip
     */
    private String ip;

}
