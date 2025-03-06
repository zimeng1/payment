package com.mc.payment.common.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class DecimalScaleValidator implements ConstraintValidator<MaxDecimalScale, BigDecimal> {
    private int maxScale;

    @Override
    public void initialize(MaxDecimalScale constraintAnnotation) {
        this.maxScale = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // 可以选择是否允许 null
        }
        // 检查小数位数
        String[] parts = value.toString().split("\\.");
        return parts.length < 2 || parts[1].length() <= maxScale; // 小数位数不能超过指定值
    }
}
