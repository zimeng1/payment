package com.mc.payment.gateway.channels.ofapay.model.req;

import lombok.Data;

@Data
public class OfaPayBaseReq {
    /**
     * 商户ID
     * 必填字段
     */
    protected String scode;


    /**
     * 参考第4节：加密规则
     * 必填字段
     */
    protected String sign;

    public OfaPayBaseReq() {
    }
    public OfaPayBaseReq(String scode) {
        this.scode = scode;
    }
}
