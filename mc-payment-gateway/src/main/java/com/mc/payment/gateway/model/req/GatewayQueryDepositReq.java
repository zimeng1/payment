package com.mc.payment.gateway.model.req;

import lombok.Data;

@Data
public class GatewayQueryDepositReq extends BaseGatewayReq {
    /**
     * 入金交易操作的唯一标识
     */
    private String transactionId;
    /**
     * 支付通道标识
     */
    private String channelId;
}
