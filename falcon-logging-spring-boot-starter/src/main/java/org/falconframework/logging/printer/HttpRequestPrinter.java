package org.falconframework.logging.printer;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.falconframework.common.util.NetworkUtil;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HttpRequestPrinter {

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Around("@within(org.springframework.stereotype.Controller) || @within(org.springframework.web.bind.annotation.RestController)")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = NetworkUtil.getHttpServletRequest();
        String uri = request.getRequestURI();
        printRequestParam(uri, joinPoint);
        Object returnResult = joinPoint.proceed();
        printResponseParam(uri, returnResult);
        return returnResult;
    }

    private void printRequestParam(String uri, ProceedingJoinPoint joinPoint) {
        String param = ParamConverter.formatArrayParam(joinPoint.getArgs());
        String header = getRequestHeaderParam();
        log.info("http[{}]入参: \nParam: {}\nHeader: {}", uri, param, header);
    }

    private String getRequestHeaderParam() {
        HttpServletRequest request = NetworkUtil.getHttpServletRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        Map map = new HashMap();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            map.put(headerName, request.getHeader(headerName));
        }
        return JSON.toJSONString(map, true);
    }

    private void printResponseParam(String uri, Object returnResult) {
        String param = JSON.toJSONString(returnResult, true);
        log.info("http[{}]出参: \nParam: {}", uri, param);
    }

}
