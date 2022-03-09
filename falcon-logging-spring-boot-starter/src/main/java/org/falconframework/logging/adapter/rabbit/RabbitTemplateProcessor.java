package org.falconframework.logging.adapter.rabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 功能说明
 *
 * @author 申益炜
 * @version 1.0.0
 * @date 2022/3/9
 */
public class RabbitTemplateProcessor implements BeanPostProcessor {

    @Autowired
    private RabbitTemplateAdapter rabbitTemplateAdapter;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RabbitTemplate) {
            RabbitTemplate rabbitTemplate = (RabbitTemplate) bean;
            processRabbitTemplate(rabbitTemplate);
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    private void processRabbitTemplate(RabbitTemplate rabbitTemplate) {
        rabbitTemplate.addBeforePublishPostProcessors(rabbitTemplateAdapter);
    }

}
