package org.falconframework.common.help;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
@Order(-2147483648)
public class SpringBeanHelper implements ApplicationContextAware, BeanDefinitionRegistryPostProcessor {

    private static ApplicationContext context;
    private static BeanDefinitionRegistry registry;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanHelper.context = applicationContext;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        SpringBeanHelper.registry = registry;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    public static Object getBean(String name) {
        return context.getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return context.getBean(name, clazz);
    }

    public static <T> List<T> getBeansOfType(Class<T> clazz) {
        Map<String, T> beansOfType = context.getBeansOfType(clazz);
        return new ArrayList<>(beansOfType.values());
    }

    public static void registryBean(String name, final Object object) {
        registry.registerBeanDefinition(name, BeanDefinitionBuilder.genericBeanDefinition(object.getClass(), new Supplier() {
            @Override
            public Object get() {
                return object;
            }
        }).getBeanDefinition());
    }

}