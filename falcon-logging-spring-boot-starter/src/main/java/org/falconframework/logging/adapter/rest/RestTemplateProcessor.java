package org.falconframework.logging.adapter.rest;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 功能说明
 *
 * @author 申益炜
 * @version 1.0.0
 * @date 2022/3/4
 */
public class RestTemplateProcessor implements BeanPostProcessor {

    @Autowired
    private RestTemplateAdapter restTemplateAdapter;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RestTemplate) {
            processRestTemplate((RestTemplate) bean);
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    private void processRestTemplate(RestTemplate restTemplate) {
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        interceptors.add(this.restTemplateAdapter);
    }

}
