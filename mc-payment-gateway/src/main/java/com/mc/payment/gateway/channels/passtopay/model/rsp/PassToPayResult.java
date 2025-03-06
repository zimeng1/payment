package com.mc.payment.gateway.channels.passtopay.model.rsp;

import lombok.Data;

@Data
public class PassToPayResult<T> {
    /**
     * 成功状态码
     */
    private static final Integer SUCCESS = 0;

    private Integer code;
    private String msg;
    private String sign;
    private T data;

    public boolean isSuccess() {
        return SUCCESS.equals(code);
    }
}
