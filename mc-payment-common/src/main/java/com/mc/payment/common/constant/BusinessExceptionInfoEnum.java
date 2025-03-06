package com.mc.payment.common.constant;

public enum BusinessExceptionInfoEnum {

    /**
     * 异常枚举信息
     * 按照业务类型区分
     * 1001-2000 通用异常
     * 2001-3000 支付异常
     * 3001-4000 交易异常
     * 4001-5000 用户异常
     * 5001-6000 业务异常
     */

    // 通用异常（示例）
    Sign_Exception(1001, "sign error!"),
    Inner_Exception(1002, "inner exception!"),
    Server_Business_Exception(1003, "server business exception!"),
    // 支付异常（示例）
    Pay_Exception(2001, "pay exception!"),
    // 交易异常（示例）
    Trade_Exception(3001, "trade exception!"),
    // 用户异常（示例）
    User_Exception(4001, "user exception!"),
    // 业务异常（示例）
    Business_Exception(5001, "business exception!");

    private int code;
    private String message;

    BusinessExceptionInfoEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
