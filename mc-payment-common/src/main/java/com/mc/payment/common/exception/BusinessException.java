package com.mc.payment.common.exception;

import com.mc.payment.common.constant.ExceptionTypeEnum;

/**
 * 业务异常
 * <p/>
 *
 * @author Conor
 * @since 2024-11-04 16:07:43.998
 */
public class BusinessException extends RuntimeException {
    private final ExceptionTypeEnum exceptionTypeEnum;

    public BusinessException(ExceptionTypeEnum exceptionTypeEnum, String message, Throwable cause) {
        super(message, cause);
        this.exceptionTypeEnum = exceptionTypeEnum;
    }

    public BusinessException(ExceptionTypeEnum exceptionTypeEnum, String message) {
        this(exceptionTypeEnum, message, null);
    }

    public BusinessException(String message) {
        this(ExceptionTypeEnum.DEFAULT, message, null);
    }

    public BusinessException(ExceptionTypeEnum exceptionTypeEnum) {
        this(exceptionTypeEnum, exceptionTypeEnum.getMessage(), null);
    }

    public BusinessException(String message, Throwable cause) {
        this(ExceptionTypeEnum.DEFAULT, message, cause);
    }

    public BusinessException(ExceptionTypeEnum exceptionTypeEnum, Throwable cause) {
        this(exceptionTypeEnum, exceptionTypeEnum.getMessage(), cause);
    }

    public ExceptionTypeEnum getExceptionTypeEnum() {
        return exceptionTypeEnum;
    }

}
