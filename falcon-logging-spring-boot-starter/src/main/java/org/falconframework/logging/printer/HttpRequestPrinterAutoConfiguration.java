package org.falconframework.logging.printer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import javax.servlet.http.HttpServletRequest;

public class HttpRequestPrinterAutoConfiguration {

    @Bean
    @ConditionalOnClass({HttpServletRequest.class})
    public HttpRequestPrinter controllerAspect() {
        return new HttpRequestPrinter();
    }

}
