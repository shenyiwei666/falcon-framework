package org.falconframework.logging;

import com.alibaba.fastjson.JSON;
import org.falconframework.logging.dto.LoggingConfig;
import org.falconframework.logging.util.LoggingConfigBuilder;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

/**
 * 日志上下文
 *
 * @author 申益炜
 * @version 1.0.0
 * @date 2021/12/6
 */
public class LoggingContext implements PropertySourceLocator {

    private static LoggingConfig loggingConfig;

    public static LoggingConfig getLoggingConfig() {
        // 防止loggingConfig被修改
        return JSON.parseObject(JSON.toJSONString(loggingConfig), LoggingConfig.class);
    }

    @Override
    public PropertySource<?> locate(Environment environment) {
        initLoggingConfig(environment);
        return null;
    }

    private void initLoggingConfig(Environment environment) {
        loggingConfig = LoggingConfigBuilder.build(environment);
    }

}
