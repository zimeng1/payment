package com.mc.payment.gateway.model.rsp;

import lombok.Data;

/**
 * 入金响应
 */
@Data
public class GatewayDepositRsp {
    /**
     * 入金交易操作的唯一标识
     */
    private String transactionId;

    /**
     * 入金申请方需要重定向的支付页面地址
     */
    private String redirectUrl;


    /**
     * 交易渠道的交易ID
     */
    private String channelTransactionId;
}
