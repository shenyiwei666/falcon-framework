package org.falconframework.logging.adapter.feign;

import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

@ConditionalOnClass({RequestInterceptor.class})
public class FeignAdapterAutoConfiguration {

    @Bean
    @ConditionalOnClass({RequestInterceptor.class})
    public FeignAdapter feignAdapter() {
        return new FeignAdapter();
    }

}