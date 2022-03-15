package org.falconframework.logging.config;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;

import java.util.Set;

public class ApolloConfigListener implements ApplicationContextAware, ApplicationListener<ApplicationReadyEvent> {

    private ApplicationContext applicationContext;

    @Autowired
    private LoggingSystem loggingSystem;

    @Value("${apollo.bootstrap.namespaces:application}")
    private String namespaces;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        String[] namespaceArray = namespaces.split(",");
        for (String namespace : namespaceArray) {
            namespace = namespaces.trim();
            Config config = ConfigService.getConfig(namespace);
            if (config != null) {
                config.addChangeListener(configChangeEvent -> {
                    changeEventHandle(configChangeEvent);
                });
            }
        }
    }

    private void changeEventHandle(ConfigChangeEvent configChangeEvent) {
        Set<String> changedKeys = configChangeEvent.changedKeys();
        for (String key : changedKeys) {
            if (key.startsWith("logging.level.")) {
                changeLogLevel(configChangeEvent, key);
            }
        }
    }

    private void changeLogLevel(ConfigChangeEvent configChangeEvent, String changeKey) {
        ConfigChange configChange = configChangeEvent.getChange(changeKey);
        String newValue = configChange.getNewValue();
        String level = StringUtils.isBlank(newValue) ? "INFO" : newValue;
        LogLevel logLevel = LogLevel.valueOf(level.toUpperCase());
        String loggerName = changeKey.replace("logging.level.", "");
        this.loggingSystem.setLogLevel(loggerName, logLevel);
    }

}
