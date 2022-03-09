package org.falconframework.logging.util;

import java.util.UUID;

/**
 * 功能说明
 *
 * @author 申益炜
 * @version 1.0.0
 * @date 2022/3/7
 */
public class LoggingUtil {

    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
