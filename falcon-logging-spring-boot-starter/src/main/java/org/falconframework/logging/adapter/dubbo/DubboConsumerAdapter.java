package org.falconframework.logging.adapter.dubbo;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.falconframework.logging.config.HeaderConstant;
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
        invocation.setAttachment(HeaderConstant.TRACE_ID, MDC.get(HeaderConstant.TRACE_ID));
        invocation.setAttachment(HeaderConstant.LOGGING_IGNORE, MDC.get(HeaderConstant.LOGGING_IGNORE));
    }

}
