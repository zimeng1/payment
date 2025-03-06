package com.mc.payment.gateway.channels.cheezeepay.model.req;

import com.mc.payment.gateway.model.req.GatewayDepositReq;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class CheezeePayDepositReq implements Serializable {

    /**
     * 应用ID，用于标识请求来源的应用。
     */
    private String appId;

    /**
     * 商户ID，用于标识发起支付的商户。
     */
    private String merchantId;

    /**
     * 商户订单号，商户内部唯一标识该订单的字符串。
     */
    private String mchOrderNo;

    /**
     * 支付模式，例如“PC网站支付”、“手机网站支付”等。
     * (indonesia)
     */
    private String paymentMode;

    /**
     * 支付模式，例如“PC网站支付”、“手机网站支付”等。
     * (thailand)
     */
    private String paymentMethod;

    /**
     * 付款方式
     * ALL-未指定付款方式，进入收银台后选择
     * qrPay-二维码支付
     * KBANK-网上银行转账
     * "{\"accountNumber\":\"88886666\"}",
     *
     * thailand
     */
    private String payeeAccountInfos;

    /**
     * 支付金额，单位为分（1元=100分）。
     */
    private String amount;

    /**
     * 收款人姓名，用于订单详情展示。
     */
    private String name;

    /**
     * 收款人邮箱，用于订单详情展示和通知。
     */
    private String email;

    /**
     * 收款人手机号码，用于订单详情展示和通知。
     * 非必填
     */
    private String phone;

    /**
     * 产品详情.
     * 必填 (印尼特有字段)
     */
    private String productDetail;

    /**
     * 异步通知URL，支付成功后支付平台会向该URL发送通知。
     */
    private String notifyUrl;

    /**
     * 页面跳转URL，支付成功后用户会被重定向到该URL。
     * 非必填 (印尼)
     */
    private String redirectUrl;

    /**
     * 页面跳转URL，支付成功后用户会被重定向到该URL。
     * 非必填 (印度)
     */
    private String returnUrl;

    /**
     * 请求时间戳，单位为毫秒，用于生成签名。
     */
    private long timestamp;

    /**
     * 语言类型，例如“zh_CN”表示简体中文。
     * 非必填
     */
    private String language;

    /**
     * 描述
     * 非必填
     */
    private String description;

    /**
     * 签名，用于验证请求的真实性。
     */
    private String sign;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

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

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProductDetail() {
        return productDetail;
    }

    public void setProductDetail(String productDetail) {
        this.productDetail = productDetail;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPayeeAccountInfos() {
        return payeeAccountInfos;
    }

    public void setPayeeAccountInfos(String payeeAccountInfos) {
        this.payeeAccountInfos = payeeAccountInfos;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

}
