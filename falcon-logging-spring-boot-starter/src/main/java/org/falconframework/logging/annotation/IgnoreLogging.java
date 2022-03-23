package org.falconframework.logging.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface IgnoreLogging {

    /**
     * 是否忽略打印入参日志
     */
    boolean request() default true;

    /**
     * 是否忽略打印出参日志
     */
    boolean response() default true;

}
