package org.falcon.common.spring;

import org.falcon.common.redis.RedisTemplatePlus;
import org.falcon.common.help.SpringBeanHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class CommonAutoConfiguration {

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(30000);
        return factory;
    }

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    @Bean
    @ConditionalOnClass(RedisTemplate.class)
    public RedisTemplatePlus redisTemplatePlus() {
        return new RedisTemplatePlus();
    }

    @Bean
    public SpringBeanHelper springBeanHelper() {
        return new SpringBeanHelper();
    }

}
