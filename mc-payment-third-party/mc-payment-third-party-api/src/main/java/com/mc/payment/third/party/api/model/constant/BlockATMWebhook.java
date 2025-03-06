package com.mc.payment.third.party.api.model.constant;

public class BlockATMWebhook {
    /**
     * webhook回调请求头签名
     */
    public static final String HEADER_EVENT= "BlockATM-Event";

    /**
     * webhook 充值事件
     */
    public static final String WEBHOOK_EVENT_PAYMENT = "Payment";


    /**
     * webhook 代付事件
     */
    public static final String WEBHOOK_EVENT_PAYOUT = "Payout";
}
