package org.falcon.logging.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@ConditionalOnProperty({"apollo.bootstrap.enabled"})
public class ApolloConfigListenerAutoConfiguration {

    @Bean
    public ApolloConfigListener apolloConfigListener() {
        return new ApolloConfigListener();
    }

}
