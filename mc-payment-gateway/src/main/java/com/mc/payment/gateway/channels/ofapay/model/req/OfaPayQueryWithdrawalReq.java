package com.mc.payment.gateway.channels.ofapay.model.req;

import com.mc.payment.gateway.model.req.GatewayQueryWithdrawalReq;
import lombok.Data;

@Data
public class OfaPayQueryWithdrawalReq extends OfaPayBaseReq {


    /**
     * 订单ID
     * 字段长度: 50
     * 必填字段
     * 描述: 订单的唯一ID
     */
    private String orderid;

    public static OfaPayQueryWithdrawalReq valueOf(GatewayQueryWithdrawalReq req) {
        OfaPayQueryWithdrawalReq ofaPayQueryWithdrawalReq = new OfaPayQueryWithdrawalReq();
        ofaPayQueryWithdrawalReq.setOrderid(req.getTransactionId());
        ofaPayQueryWithdrawalReq.setScode(req.getChannelId());
        return ofaPayQueryWithdrawalReq;

    }
}
