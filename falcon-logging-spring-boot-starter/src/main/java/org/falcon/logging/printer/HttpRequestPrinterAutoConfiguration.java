package org.falcon.logging.printer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import javax.servlet.http.HttpServletRequest;

@ConditionalOnClass({HttpServletRequest.class})
public class HttpRequestPrinterAutoConfiguration {

    @Bean
    public HttpRequestPrinter controllerAspect() {
        return new HttpRequestPrinter();
    }

}
