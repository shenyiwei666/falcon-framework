package org.falconframework.logging.gather;

import org.falconframework.logging.dto.ElkLogging;
import org.falconframework.logging.enums.GatherEnum;

/**
 * 功能说明
 *
 * @author 申益炜
 * @version 1.0.0
 * @date 2022/3/1
 */
public interface LoggingGather {

    static LoggingGather getInstance(String gather) {
        GatherEnum gatherEnum = GatherEnum.getByValue(gather);
        if (gatherEnum == null) {
            return null;
        }
        return gatherEnum.getInstance();
    }

    void write(ElkLogging elkLogging);

}
