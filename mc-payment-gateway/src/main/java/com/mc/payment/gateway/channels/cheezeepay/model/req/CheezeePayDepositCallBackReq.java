package com.mc.payment.gateway.channels.cheezeepay.model.req;

import java.util.Map;

public class CheezeePayDepositCallBackReq {

    /**
     * 商户编号，必须
     */
    private String merchantId;

    /**
     * 商户订单号，必须
     */
    private String mchOrderNo;

    /**
     * 平台订单号，必须
     */
    private String platOrderNo;

    /**
     * 订单状态，必须（1-成功 2-退款 3-部分付款）
     */
    private Integer orderStatus;

    /**
     * 订单实际付款金额，必须
     */
    private String payAmount;

    /**
     * 订单金额货币，必须
     */
    private String amountCurrency;

    /**
     * 手续费，必须
     */
    private String fee;

    /**
     * 手续费币种，必须
     */
    private String feeCurrency;

    /**
     * 完成时间（时间戳：毫秒），必须
     */
    private Long gmtEnd;

    /**
     * 签名，必须
     */
    private String sign;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMchOrderNo() {
        return mchOrderNo;
    }

    public void setMchOrderNo(String mchOrderNo) {
        this.mchOrderNo = mchOrderNo;
    }

    public String getPlatOrderNo() {
        return platOrderNo;
    }

    public void setPlatOrderNo(String platOrderNo) {
        this.platOrderNo = platOrderNo;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }

    public String getAmountCurrency() {
        return amountCurrency;
    }

    public void setAmountCurrency(String amountCurrency) {
        this.amountCurrency = amountCurrency;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getFeeCurrency() {
        return feeCurrency;
    }

    public void setFeeCurrency(String feeCurrency) {
        this.feeCurrency = feeCurrency;
    }

    public Long getGmtEnd() {
        return gmtEnd;
    }

    public void setGmtEnd(Long gmtEnd) {
        this.gmtEnd = gmtEnd;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

}
