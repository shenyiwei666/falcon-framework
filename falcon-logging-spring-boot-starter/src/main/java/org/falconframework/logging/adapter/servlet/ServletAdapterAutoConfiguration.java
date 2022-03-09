package org.falconframework.logging.adapter.servlet;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

import javax.servlet.Filter;

/**
 * 功能说明
 *
 * @author 申益炜
 * @version 1.0.0
 * @date 2022/3/7
 */
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
