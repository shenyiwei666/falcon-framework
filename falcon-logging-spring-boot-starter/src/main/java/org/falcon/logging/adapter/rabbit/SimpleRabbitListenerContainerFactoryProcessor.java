package org.falcon.logging.adapter.rabbit;

import org.aopalliance.aop.Advice;
import org.falcon.common.util.ArrayUtil;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

@ConditionalOnClass({SimpleRabbitListenerContainerFactory.class})
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
