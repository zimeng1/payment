package com.mc.payment.gateway.channels.cheezeepay.model.rsp;

public class CheezeePayWithdrawalRsp {

    // 响应码，必填（例如：200表示成功，其他值表示失败）
    private String code;

    // 响应消息，必填（对响应码的详细解释）
    private String msg;

    // 平台订单号，必填（支付平台生成的唯一订单号）
    private String tradeNo;

    // 签名，必填（用于验证响应的有效性）
    private String sign;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
