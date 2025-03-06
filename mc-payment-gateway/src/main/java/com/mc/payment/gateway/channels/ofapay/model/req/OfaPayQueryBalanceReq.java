package com.mc.payment.gateway.channels.ofapay.model.req;

import lombok.Data;

@Data
public class OfaPayQueryBalanceReq extends OfaPayBaseReq {

    public OfaPayQueryBalanceReq() {
    }

    public OfaPayQueryBalanceReq(String scode) {
        super(scode);
    }

}
