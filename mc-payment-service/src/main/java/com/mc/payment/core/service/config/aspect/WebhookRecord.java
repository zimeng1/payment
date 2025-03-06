package com.mc.payment.core.service.config.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于记录接收到的webhook
 *
 * @author Conor
 * @since 2024-08-20 22:00:51.299
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface WebhookRecord {
    /**
     * webhook的类型
     *
     * @return
     */
    String value() default "";
}
