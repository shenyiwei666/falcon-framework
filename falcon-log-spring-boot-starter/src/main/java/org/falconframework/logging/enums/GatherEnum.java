/*
 * 深圳市灵智数科有限公司版权所有.
 */
package org.falconframework.logging.enums;

/**
 * 日志采集方式
 *
 * @author 申益炜
 * @version 1.0.0
 * @date 2022/1/10
 */
public enum GatherEnum {

    /**
     * kafka
     */
    KAFKA("kafka");


    GatherEnum(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }

}
