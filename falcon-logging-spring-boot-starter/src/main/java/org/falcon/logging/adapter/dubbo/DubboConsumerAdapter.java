package org.falcon.logging.adapter.dubbo;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.falcon.logging.constant.LoggingConstant;
import org.slf4j.MDC;
import org.springframework.core.Ordered;

@Activate(group = {CommonConstants.CONSUMER}, order = Ordered.HIGHEST_PRECEDENCE)
public class DubboConsumerAdapter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        initLoggingMdc(invocation);
        return invoker.invoke(invocation);
    }

    private void initLoggingMdc(Invocation invocation) {
        invocation.setAttachment(LoggingConstant.TRACE_ID, MDC.get(LoggingConstant.TRACE_ID));
        invocation.setAttachment(LoggingConstant.LOGGING_IGNORE, MDC.get(LoggingConstant.LOGGING_IGNORE));
    }

}
