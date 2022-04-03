package org.falcon.logging.adapter.rabbit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@ConditionalOnProperty(prefix = "spring.rabbitmq", name = "host")
public class RabbitAdapterAutoConfiguration {

    @Bean
    public RabbitTemplateAdapter rabbitTemplateAdapter() {
        return new RabbitTemplateAdapter();
    }

    @Bean
    public RabbitTemplateProcessor rabbitTemplateProcessor() {
        return new RabbitTemplateProcessor();
    }

    @Bean
    public RabbitListenerAdapter rabbitListenerAdapter() {
        return new RabbitListenerAdapter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactoryProcessor simpleRabbitListenerContainerFactoryProcessor() {
        return new SimpleRabbitListenerContainerFactoryProcessor();
    }

}