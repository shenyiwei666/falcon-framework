package org.falconframework.logging.adapter.servlet;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

import javax.servlet.Filter;

@ConditionalOnClass({Filter.class})
public class ServletAdapterAutoConfiguration {

    @Bean
    @ConditionalOnClass({Filter.class})
    public ServletAdapter servletAdapter() {
        return new ServletAdapter();
    }

    @Bean
    @ConditionalOnClass({Filter.class})
    public FilterRegistrationBean filterRegistrationBean(ServletAdapter servletAdapter) {
        FilterRegistrationBean filterBean = new FilterRegistrationBean();
        filterBean.setFilter(servletAdapter);
        filterBean.addUrlPatterns(new String[]{"/*"});
        filterBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterBean;
    }

}
