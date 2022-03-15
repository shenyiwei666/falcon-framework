package org.falconframework.logging.gather;

public enum LoggingGatherEnum {

    /**
     * kafka
     */
    KAFKA("kafka", new KafkaLoggingGather());


    LoggingGatherEnum(String value, LoggingGather instance) {
        this.value = value;
        this.instance = instance;
    }

    private String value;

    private LoggingGather instance;

    public static LoggingGatherEnum getByValue(String value) {
        if (value == null) {
            return null;
        }
        for (LoggingGatherEnum e : LoggingGatherEnum.values()) {
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
