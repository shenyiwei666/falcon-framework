package org.falconframework.logging.enums;

import org.falconframework.logging.gather.KafkaGather;
import org.falconframework.logging.gather.LoggingGather;

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
    KAFKA("kafka", new KafkaGather());


    GatherEnum(String value, LoggingGather instance) {
        this.value = value;
        this.instance = instance;
    }

    private String value;

    private LoggingGather instance;

    public static GatherEnum getByValue(String value) {
        if (value == null) {
            return null;
        }
        for (GatherEnum e : GatherEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public LoggingGather getInstance() {
        return instance;
    }
}
