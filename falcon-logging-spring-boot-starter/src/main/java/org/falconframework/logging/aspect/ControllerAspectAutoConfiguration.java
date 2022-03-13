package org.falconframework.logging.aspect;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import javax.servlet.http.HttpServletRequest;

/**
 * 功能说明
 *
 * @author 申益炜
 * @version 1.0.0
 * @date 2022/3/10
 */
public class ControllerAspectAutoConfiguration {

    @Bean
    @ConditionalOnClass({HttpServletRequest.class})
    public ControllerAspect controllerAspect() {
        return new ControllerAspect();
    }

}
