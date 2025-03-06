package com.mc.payment.common.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DecimalScaleValidator.class)
public @interface MaxDecimalScale {
    int value() default 20; // 默认小数位数

    String message() default "小数位数不能超过 {value}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
