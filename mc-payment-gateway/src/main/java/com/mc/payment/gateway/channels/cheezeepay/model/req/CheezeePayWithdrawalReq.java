package com.mc.payment.gateway.channels.cheezeepay.model.req;

import com.mc.payment.gateway.model.req.GatewayWithdrawalReq;

public class CheezeePayWithdrawalReq {
    // 应用ID，必填
    private String appId;

    // 商户ID，必填
    private String merchantId;

    // 商户订单号，必填
    private String mchOrderNo;

    // 支付方式，必填（例如：银行卡支付、支付宝支付等）
    private String paymentMethod;

    // 支付金额，必填（单位：元，精确到小数点后两位）
    private String amount;

    // 收款人姓名，必填
    private String name;

    // 收款人手机号，非必填
    private String phone;

    // 收款人邮箱，非必填
    private String email;

    // 通知URL，必填（支付成功后回调的URL）
    private String notifyUrl;

    // 收款账户信息，必填（包含银行和账号信息）
    private String payeeAccountInfos;

    // 请求时间戳，必填（单位：毫秒）
    private long timestamp;

    // 请求语言，必填
    private String language;

    // 签名，必填（用于验证请求的有效性）
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getPayeeAccountInfos() {
        return payeeAccountInfos;
    }

    public void setPayeeAccountInfos(String payeeAccountInfos) {
        this.payeeAccountInfos = payeeAccountInfos;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    // 内部类，用于封装收款账户信息
    public static class PayeeAccountInfos {
        // 银行编码，必填
        private String bankCode;

        // 收款人银行账户号，必填
        private String accountNumber;
        private String bankCardNumber;

        //BRL特有参数
        private String pixType;
        private String pixAccount;
        private String taxNumber;

        //INR特有参数
        private String name;
        private String ifscCode;
        private String accountType;
        private String bankName;
        private String branchName;

        public String getBankCode() {
            return bankCode;
        }

        public void setBankCode(String bankCode) {
            this.bankCode = bankCode;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getBankCardNumber() {
            return bankCardNumber;
        }

        public void setBankCardNumber(String bankCardNumber) {
            this.bankCardNumber = bankCardNumber;
        }

        public String getPixType() {
            return pixType;
        }

        public void setPixType(String pixType) {
            this.pixType = pixType;
        }

        public String getPixAccount() {
            return pixAccount;
        }

        public void setPixAccount(String pixAccount) {
            this.pixAccount = pixAccount;
        }

        public String getTaxNumber() {
            return taxNumber;
        }

        public void setTaxNumber(String taxNumber) {
            this.taxNumber = taxNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIfscCode() {
            return ifscCode;
        }

        public void setIfscCode(String ifscCode) {
            this.ifscCode = ifscCode;
        }

        public String getAccountType() {
            return accountType;
        }

        public void setAccountType(String accountType) {
            this.accountType = accountType;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getBranchName() {
            return branchName;
        }

        public void setBranchName(String branchName) {
            this.branchName = branchName;
        }
    }

    public static CheezeePayWithdrawalReq valueOf(GatewayWithdrawalReq req){
        CheezeePayWithdrawalReq cheezeePayWithdrawalReq = new CheezeePayWithdrawalReq();
        cheezeePayWithdrawalReq.setAppId("CH10001146");
        cheezeePayWithdrawalReq.setMerchantId("CH10001146");
        cheezeePayWithdrawalReq.setMchOrderNo(req.getTransactionId());
        cheezeePayWithdrawalReq.setPaymentMethod("ALL");
        cheezeePayWithdrawalReq.setAmount(req.getAmount());
        cheezeePayWithdrawalReq.setNotifyUrl(req.getCallbackUrl());
        cheezeePayWithdrawalReq.setTimestamp(System.currentTimeMillis());
        return cheezeePayWithdrawalReq;

    }
}
