package com.mc.payment.gateway.channels.cheezeepay.model.req;

import lombok.Data;

import java.io.Serializable;

@Data
public class CheezeePayWithdrawalCallbackReq implements Serializable {

    // 商户ID，必填
    private String merchantId;

    // 商户订单号，必填
    private String mchOrderNo;

    // 平台订单号，必填
    private String platOrderNo;

    // 订单状态，1-成功，2-退款，4-失败，必填
    private int orderStatus;

    // 订单实际支付金额，必填
    private String payAmount;

    // 订单金额货币类型，必填
    private String amountCurrency;

    // 处理费，必填
    private String fee;

    // 处理费货币类型，必填
    private String feeCurrency;

    // 完成时间（时间戳：毫秒），必填
    private long gmtEnd;

    // 签名，用于验证消息的完整性，必填
    private String sign;
}
