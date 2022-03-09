package org.falconframework.logging.thread;

import org.slf4j.MDC;

import java.util.Map;

/**
 * 将主线程中的MDC信息传递到子线程中
 *
 * @author 申益炜
 * @version 1.0.0
 * @date 2022/3/4
 */
public abstract class MdcRunnable implements Runnable {

    private Map<String, String> parentMdc;

    public MdcRunnable() {
        loadParentMdc();
    }

    /**
     * Runnable中run方法执行的内容
     */
    public abstract void call();

    @Override
    public void run() {
        initOwnMdc();
        call();
        clearOwnMdc();
    }

    private void loadParentMdc() {
        this.parentMdc = MDC.getCopyOfContextMap();
    }

    private void initOwnMdc() {
        for (Map.Entry<String, String> entry : this.parentMdc.entrySet()) {
            MDC.put(entry.getKey(), entry.getValue());
        }
    }

    private void clearOwnMdc() {
        for (String key : this.parentMdc.keySet()) {
            MDC.remove(key);
        }
    }

}
