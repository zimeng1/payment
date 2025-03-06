package com.mc.payment.core.service.util;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 重试工具类
 *
 * @author Conor
 * @since 2025-01-15 14:21:12.100
 */
@Slf4j
public class RetryUtil {
    private RetryUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 重试
     *
     * @param action        重试的操作
     * @param shouldRetry   是否需要重试
     * @param maxRetries    最大重试次数
     * @param delayInMillis 重试间隔
     * @param <T>           返回值类型
     * @return
     */
    public static <T> T retry(Supplier<T> action, Predicate<T> shouldRetry, int maxRetries, long delayInMillis) {
        int attempt = 0;
        T result = null;
        while (attempt < maxRetries) {
            try {
                result = action.get();
                if (!shouldRetry.test(result)) {
                    return result;
                }
            } catch (Exception e) {
                log.warn("Attempt {} failed: {}", attempt + 1, e.getMessage());
            }

            attempt++;
            if (attempt < maxRetries) {
                try {
                    Thread.sleep(delayInMillis);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return result;
                }
            }
        }
        return result;
    }
}
