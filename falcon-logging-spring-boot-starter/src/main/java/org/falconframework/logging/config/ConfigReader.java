package org.falconframework.logging.config;

import org.falconframework.logging.util.LoggingConfigBuilder;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

public class ConfigReader implements PropertySourceLocator {

    private static LoggingConfig loggingConfig;

    public static LoggingConfig getLoggingConfig() {
        return loggingConfig;
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
