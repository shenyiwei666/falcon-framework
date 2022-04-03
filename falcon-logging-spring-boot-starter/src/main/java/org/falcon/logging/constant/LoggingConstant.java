package org.falcon.logging.constant;

public class LoggingConstant {
    /**
     * 日志链路id
     */
    public final static String TRACE_ID = "X-B3-TraceId";

    /**
     * 忽略日志打印，values[true, false]
     */
    public final static String LOGGING_IGNORE = "X-Logging-Ignore";

    /**
     * 服务器ip
     */
    public final static String SERVER_IP = "X-Server-Ip";

}
