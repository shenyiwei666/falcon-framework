package org.falcon.logging.config;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;
import java.util.Map;

public class ApolloConfigListener implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${apollo.bootstrap.namespaces:application}")
    private String namespaces;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        String[] namespaceArray = namespaces.split(",");
        for (String namespace : namespaceArray) {
            namespace = namespaces.trim();
            Config config = ConfigService.getConfig(namespace);
            if (config != null) {
                config.addChangeListener(configChangeEvent -> {
                    Map<String, String> changeKeyValueMap = getChangeKeyValueMap(configChangeEvent);
                    ConfigChangeHandle.change(changeKeyValueMap);
                });
            }
        }
    }

    private Map<String, String> getChangeKeyValueMap(ConfigChangeEvent configChangeEvent) {
        Map<String, String> changeKeyValueMap = new HashMap<>();
        for (String changeKey : configChangeEvent.changedKeys()) {
            ConfigChange configChange = configChangeEvent.getChange(changeKey);
            String newValue = configChange.getNewValue();
            changeKeyValueMap.put(changeKey, newValue);
        }
        return changeKeyValueMap;
    }

}
