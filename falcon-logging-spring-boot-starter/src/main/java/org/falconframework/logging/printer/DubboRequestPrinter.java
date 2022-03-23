package org.falconframework.logging.printer;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.falconframework.logging.annotation.IgnoreLogging;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
// 在DubboProviderAdapter之后执行
@Activate(group = {CommonConstants.PROVIDER}, order = Ordered.HIGHEST_PRECEDENCE + 1)
public class DubboRequestPrinter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        IgnoreLogging ignoreLogging = getIgnoreLogAnnotation(invoker, invocation);

        if (ignoreLogging == null || !ignoreLogging.request()) {
            printRequestParam(invoker, invocation);
        }
        Result result = invoker.invoke(invocation);
        if (ignoreLogging == null || !ignoreLogging.response()) {
            printResponseParam(invoker, invocation, result);
        }
        return result;
    }

    private void printRequestParam(Invoker<?> invoker, Invocation invocation) {
        String invokeTarget = getInvokeTarget(invoker, invocation);
        String param = ParamConverter.formatArrayParam(invocation.getArguments());
        String attachments = getAttachmentsParam(invocation);
        log.info("dubbo[{}]入参: \nParam: {}\nAttachments：{}", invokeTarget, param, attachments);
    }

    private String getAttachmentsParam(Invocation invocation) {
        Map<String, String> attachments = invocation.getAttachments();
        return JSON.toJSONString(attachments, true);
    }

    private void printResponseParam(Invoker<?> invoker, Invocation invocation, Result returnResult) {
        String invokeTarget = getInvokeTarget(invoker, invocation);
        String param = JSON.toJSONString(returnResult.getValue(), true);
        log.info("dubbo[{}]出参: \nParam: {}", invokeTarget, param);
    }

    private String getInvokeTarget(Invoker<?> invoker, Invocation invocation) {
        String invokeTarget = invoker.getInterface().getSimpleName() + "." + invocation.getMethodName();
        return invokeTarget;
    }

    private IgnoreLogging getIgnoreLogAnnotation(Invoker<?> invoker, Invocation invocation) {
        Method[] interfaceMethods = invoker.getInterface().getDeclaredMethods();
        Map<String, Method> interfaceMethodMap = new HashMap<>();
        for (Method method : interfaceMethods) {
            String methodName = getUniqueMethodName(method.getName(), method.getParameterTypes());
            interfaceMethodMap.put(methodName, method);
        }
        String invokeMethodName = getUniqueMethodName(invocation.getMethodName(), invocation.getParameterTypes());
        Method invokeMethod = interfaceMethodMap.get(invokeMethodName);
        return invokeMethod.getAnnotation(IgnoreLogging.class);
    }

    private String getUniqueMethodName(String methodName, Class[] parameterTypes) {
        String paramTypeDesc = getClassTypeDesc(parameterTypes);
        return methodName + "-" + paramTypeDesc;
    }

    private String getClassTypeDesc(Class[] classArray) {
        String classTypeDesc = "";
        if (classArray != null && classArray.length > 0) {
            List<String> typeList = Arrays.stream(classArray).map(e -> e.getTypeName()).collect(Collectors.toList());
            classTypeDesc = String.join("-", typeList);
        }
        return classTypeDesc;
    }

}
