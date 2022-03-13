package org.falconframework.logging.aspect;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.falconframework.common.util.NetworkUtil;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

/**
 * 功能说明
 *
 * @author 申益炜
 * @version 1.0.0
 * @date 2022/3/10
 */
@Slf4j
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ControllerAspect {

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
        String param = getRequestParam(joinPoint);
        log.info("{}入参: {}", uri, param);
    }

    private void printResponseParam(String uri, Object returnResult) {
        String param = JSON.toJSONString(returnResult);
        log.info("{}出参: {}", uri, param);
    }

    private String getRequestParam(ProceedingJoinPoint joinPoint) {
        Object[] params = joinPoint.getArgs();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < params.length; i++) {
            String paramKey = "arg" + (i + 1);
            String paramValue = convertString(params[i]);
            if (paramValue == null) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(paramKey + " = " + paramValue);
        }
        return sb.toString();
    }

    private static String convertString(Object param) {
        if (param instanceof ServletRequest || param instanceof ServletResponse || param instanceof MultipartFile) {
            return null;
        }
        if (param instanceof Float || param instanceof Double) {
            param = new BigDecimal(param + "");
        }
        if (param instanceof String || param instanceof Long || param instanceof Integer || param instanceof Boolean
                || param instanceof Short || param instanceof Character || param instanceof Byte) {
            return String.valueOf(param);
        } else if (param instanceof BigDecimal) {
            return ((BigDecimal) param).setScale(8, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
        } else if(param instanceof java.util.Date) {
            return (((java.util.Date) param).getTime() + "");
        } else if(param instanceof java.sql.Date) {
            return (((java.sql.Date) param).getTime() + "");
        }  else if(param instanceof Timestamp) {
            return (((Timestamp) param).getTime() + "");
        } else {
            return JSON.toJSONString(param);
        }
    }

}
