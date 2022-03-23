package org.falconframework.logging.adapter.dubbo;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.falconframework.logging.config.HeaderConstant;
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
        MDC.put(HeaderConstant.TRACE_ID, invocation.getAttachment(HeaderConstant.TRACE_ID));
        MDC.put(HeaderConstant.LOGGING_IGNORE, invocation.getAttachment(HeaderConstant.LOGGING_IGNORE));
    }

    private void clearLoggingMdc() {
        MDC.remove(HeaderConstant.TRACE_ID);
        MDC.remove(HeaderConstant.LOGGING_IGNORE);
    }

}
