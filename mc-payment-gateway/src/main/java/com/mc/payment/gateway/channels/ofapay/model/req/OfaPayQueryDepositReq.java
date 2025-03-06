package com.mc.payment.gateway.channels.ofapay.model.req;

import com.mc.payment.gateway.model.req.GatewayQueryDepositReq;
import lombok.Data;

@Data
public class OfaPayQueryDepositReq extends OfaPayBaseReq {


    /**
     * 唯一交易ID
     * 必填字段
     */
    private String orderid;


    public static OfaPayQueryDepositReq valueOf(GatewayQueryDepositReq req) {
        OfaPayQueryDepositReq ofaPayQueryDepositReq = new OfaPayQueryDepositReq();
        ofaPayQueryDepositReq.setOrderid(req.getTransactionId());
        ofaPayQueryDepositReq.setScode(req.getChannelId());
        return ofaPayQueryDepositReq;

    }
}
