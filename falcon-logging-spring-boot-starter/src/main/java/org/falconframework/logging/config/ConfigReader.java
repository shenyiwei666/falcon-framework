package org.falconframework.logging.config;

import com.alibaba.fastjson.JSON;
import org.falconframework.logging.util.LoggingConfigBuilder;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

public class ConfigReader implements PropertySourceLocator {

    private static LoggingConfig loggingConfig;

    public static LoggingConfig getLoggingConfig() {
        // 返回副本
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
