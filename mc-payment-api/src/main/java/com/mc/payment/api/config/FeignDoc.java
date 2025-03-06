package com.mc.payment.api.config;

import java.lang.annotation.*;

/**
 * 用于Feign接口文档标记
 *
 * @author Conor
 * @since 2024-07-29 13:51:00.512
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE_USE)
public @interface FeignDoc {
    /**
     * 接口名称
     */
    String name() default "";

    /**
     * 接口描述
     */
    String description() default "";
}
