package org.falconframework.logging.adapter.application;

import org.falconframework.logging.constant.LoggingConstant;
import org.falconframework.logging.util.TraceIdGenerator;
import org.slf4j.MDC;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.Ordered;

import java.util.ArrayList;
import java.util.List;

public class ApplicationListenerAdapter implements SmartApplicationListener {
    private static final List<Class> SUPPORTS_EVENT_TYPES = new ArrayList();

    static {
        SUPPORTS_EVENT_TYPES.add(ApplicationStartingEvent.class);
        SUPPORTS_EVENT_TYPES.add(ApplicationEnvironmentPreparedEvent.class);
        SUPPORTS_EVENT_TYPES.add(ContextClosedEvent.class);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return SUPPORTS_EVENT_TYPES.contains(eventType);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        initLoggingMdc();
    }

    private void initLoggingMdc() {
        MDC.put(LoggingConstant.TRACE_ID, TraceIdGenerator.generate());
    }

}
