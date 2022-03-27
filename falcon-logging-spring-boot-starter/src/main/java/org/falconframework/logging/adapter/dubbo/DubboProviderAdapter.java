package org.falconframework.logging.adapter.dubbo;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.falconframework.logging.constant.LoggingConstant;
import org.slf4j.MDC;
import org.springframework.core.Ordered;

@Activate(group = {CommonConstants.PROVIDER}, order = Ordered.HIGHEST_PRECEDENCE)
public class DubboProviderAdapter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            initLoggingMdc(invocation);
            return invoker.invoke(invocation);
        } finally {
            clearLoggingMdc();
        }
    }

    private void initLoggingMdc(Invocation invocation) {
        MDC.put(LoggingConstant.TRACE_ID, invocation.getAttachment(LoggingConstant.TRACE_ID));
        MDC.put(LoggingConstant.LOGGING_IGNORE, invocation.getAttachment(LoggingConstant.LOGGING_IGNORE));
    }

    private void clearLoggingMdc() {
        MDC.remove(LoggingConstant.TRACE_ID);
        MDC.remove(LoggingConstant.LOGGING_IGNORE);
    }

}
