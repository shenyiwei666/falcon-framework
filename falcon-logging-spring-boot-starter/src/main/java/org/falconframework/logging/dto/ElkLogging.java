package org.falconframework.logging.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * elk日志
 *
 * @author 申益炜
 * @version 1.0.0
 * @date 2022/1/10
 */
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
