package com.mc.payment.gateway.model.req;

import lombok.Data;

@Data
public class GatewayDepositReq extends BaseGatewayReq {
    /**
     * 交易操作的唯一标识
     */
    private String transactionId;
    /**
     * 支付通道标识
     */
    private String channelId;
    /**
     * 入金业务名称, 比如商品名称/业务名称 eg: xxx报名费
     */
    private String businessName;
    /**
     * 金额
     */
    private String amount;
    /**
     * 入金币种
     */
    private String currency;
    /**
     * 支付类型
     */
    private String payType;
    /**
     * 银行代码 某些币种的支付类型需要
     */
    private String bankCode;
    /**
     * 通知回调地址 webhook
     */
    private String callbackUrl;
    /**
     * 入金成功跳转页面地址
     */
    private String successPageUrl;
    /**
     * 入金备注
     */
    private String remark;


}
