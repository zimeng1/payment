package com.mc.payment.gateway.model.rsp;

import lombok.Data;

@Data
public class GatewayQueryBalanceRsp {
    /**
     * 余额
     */
    private String balance;
}
