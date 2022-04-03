package org.falcon.logging.adapter.rest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@ConditionalOnClass({RestTemplate.class})
public class RestTemplateAdapterAutoConfiguration {

    @Bean
    public RestTemplateAdapter restTemplateAdapter() {
        return new RestTemplateAdapter();
    }

    @Bean
    public RestTemplateProcessor restTemplateProcessor() {
        return new RestTemplateProcessor();
    }

}