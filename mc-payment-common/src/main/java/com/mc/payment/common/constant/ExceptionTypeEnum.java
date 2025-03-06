package com.mc.payment.common.constant;

import lombok.Getter;

/**
 * 异常枚举信息
 * <p/>
 * 按照业务类型区分
 * 500 统一的内部异常,用于未知异常/未捕获的异常/或不方便展示给用户的异常
 * 1001-2000 通用异常
 *
 * @author Conor
 * @since 2024-11-04 15:46:45.154
 */
@Getter
public enum ExceptionTypeEnum {
    DEFAULT(500, "inner exception"),
    // 签名错误
    SIGN_ERROR(1001, "sign error"),
    // 资源不存在
    NOT_EXIST(1002, "resource not exist"),
    // 资源已存在
    ALREADY_EXIST(1003, "resource already exist"),
    // 重复操作
    REPEAT_OPERATION(1004, "repeat operation"),
    //业务异常200开头
    INSUFFICIENT_BALANCE(2001001, "Insufficient balance!"),;


    private int code;
    private String message;

    ExceptionTypeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
