package org.falconframework.logging.gather;

import org.falconframework.logging.elk.ElkLogging;

public interface LoggingGather {

    static LoggingGather getInstance(String gather) {
        LoggingGatherEnum loggingGatherEnum = LoggingGatherEnum.getByValue(gather);
        if (loggingGatherEnum == null) {
            return null;
        }
        return loggingGatherEnum.getInstance();
    }

    void write(ElkLogging elkLogging);

}
