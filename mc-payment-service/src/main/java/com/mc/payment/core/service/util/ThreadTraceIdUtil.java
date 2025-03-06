package com.mc.payment.core.service.util;

import cn.hutool.core.thread.ThreadUtil;
import org.slf4j.MDC;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 可以继承父线程Trace ID的线程工具
 *
 * @author Conor
 * @since 2024/4/27 下午3:17
 */
public class ThreadTraceIdUtil {
    /**
     * 直接在公共线程池中执行线程
     *
     * @param runnable 可运行对象
     */
    public static void execute(Runnable runnable) {
        String traceId = MDC.get("traceId");
        ThreadUtil.execute(() -> {
            runAndInheritTraceId(runnable, traceId);
        });
    }

    private static void runAndInheritTraceId(Runnable runnable, String traceId) {
        try {
            if (traceId == null || traceId.isEmpty()) {
                MDC.put("traceId", traceId);
            } else {
                MDC.put("traceId", UUID.randomUUID().toString());
            }
            runnable.run();
        } finally {
            MDC.remove("traceId");
        }
    }

    /**
     * 执行异步方法
     *
     * @param runnable 需要执行的方法体
     * @param isDaemon 是否守护线程。守护线程会在主线程结束后自动结束
     * @return 执行的方法体
     */
    public static Runnable execAsync(Runnable runnable, boolean isDaemon) {
        String traceId = MDC.get("traceId");
        return ThreadUtil.execAsync(() -> {
            runAndInheritTraceId(runnable, traceId);
        }, isDaemon);
    }

    /**
     * 执行有返回值的异步方法<br>
     * Future代表一个异步执行的操作，通过get()方法可以获得操作的结果，如果异步操作还没有完成，则，get()会使当前线程阻塞
     *
     * @param <T>  回调对象类型
     * @param task {@link Callable}
     * @return Future
     */
    public static <T> Future<T> execAsync(Callable<T> task) {
        String traceId = MDC.get("traceId");
        return ThreadUtil.execAsync(() -> {
            try {
                if (traceId == null || traceId.isEmpty()) {
                    MDC.put("traceId", traceId);
                } else {
                    MDC.put("traceId", UUID.randomUUID().toString());
                }
                return task.call();
            } finally {
                MDC.remove("traceId");
            }
        });
    }
}
