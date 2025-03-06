package com.mc.payment.gateway.channels.cheezeepay.model.rsp;

import java.io.Serializable;

public class CheezeePayDepositRsp implements Serializable {

    /**
     * 响应码，用于标识支付结果的状态。
     */
    private String code;

    /**
     * 响应消息，用于描述支付结果的状态或错误信息。
     */
    private String message;

    /**
     * 支付平台订单号，支付成功后由支付平台生成。
     */
    private String tradeNo;

    /**
     * 收银台链接，用于用户跳转到收银台页面完成支付。
     */
    private String cashierLink;

    /**
     * 签名，用于验证响应的真实性。
     */
    private String sign;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getCashierLink() {
        return cashierLink;
    }

    public void setCashierLink(String cashierLink) {
        this.cashierLink = cashierLink;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
