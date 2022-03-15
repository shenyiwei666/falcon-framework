package org.falconframework.logging.printer;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.springframework.core.Ordered;

import java.util.Map;

@Slf4j
// 在DubboProviderAdapter之后执行
@Activate(group = {CommonConstants.PROVIDER}, order = Ordered.HIGHEST_PRECEDENCE + 1)
public class DubboRequestPrinter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        printRequestParam(invoker, invocation);
        Result result = invoker.invoke(invocation);
        printResponseParam(invoker, invocation, result);
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

}
