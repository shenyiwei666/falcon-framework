package org.falconframework.logging.adapter.rabbit;

import org.aopalliance.aop.Advice;
import org.falconframework.common.util.ArrayUtil;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
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
public class SimpleRabbitListenerContainerFactoryProcessor implements BeanPostProcessor {

    @Autowired
    private RabbitListenerAdapter rabbitListenerAdapter;


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof SimpleRabbitListenerContainerFactory) {
            SimpleRabbitListenerContainerFactory factory = (SimpleRabbitListenerContainerFactory) bean;
            processSimpleRabbitListenerContainerFactory(factory);
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    private void processSimpleRabbitListenerContainerFactory(SimpleRabbitListenerContainerFactory factory) {
        Advice[] oldAdviceChain = factory.getAdviceChain();
        if (oldAdviceChain == null) {
            factory.setAdviceChain(rabbitListenerAdapter);
        } else {
            Advice[] newAdviceChain = ArrayUtil.concat(oldAdviceChain, rabbitListenerAdapter);
            factory.setAdviceChain(newAdviceChain);
        }
    }

}
