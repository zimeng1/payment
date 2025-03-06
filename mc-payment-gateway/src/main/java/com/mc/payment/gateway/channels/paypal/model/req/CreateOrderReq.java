package com.mc.payment.gateway.channels.paypal.model.req;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class CreateOrderReq {
    private String transactionId;
    private String currency;
    private String amount;
    private String returnUrl;
    private String cancelUrl;
    // 收款账号
    private String payeeAccount;
    // private String merchantName;

    public JSONObject buildRequestBody() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("intent", "CAPTURE");
        jsonObject.set("purchase_units", this.buildPurchaseUnits());
        jsonObject.set("payment_source", this.buildPaymentSource());
        return jsonObject;
    }

    private JSONObject buildAmount() {
        JSONObject amountJsonObject = new JSONObject();
        amountJsonObject.set("currency_code", currency);
        amountJsonObject.set("value", amount);
        return amountJsonObject;
    }

    private JSONObject buildPayee() {
        JSONObject payee = new JSONObject();
        payee.set("email_address", payeeAccount);
        return payee;
    }

    private JSONArray buildPurchaseUnits() {
        JSONArray purchaseUnits = new JSONArray();
        JSONObject purchaseUnit = new JSONObject();
        purchaseUnit.set("amount", this.buildAmount());
        purchaseUnit.set("payee", this.buildPayee());
        purchaseUnits.add(purchaseUnit);
        return purchaseUnits;
    }

    private JSONObject buildPaymentSource() {
        JSONObject paymentSource = new JSONObject();
        paymentSource.set("paypal", this.buildPayPal());
        return paymentSource;
    }

    private JSONObject buildPayPal() {
        JSONObject payPal = new JSONObject();
        payPal.set("experience_context", this.buildExperienceContext());
        return payPal;
    }//sb-zbab732658182_api1.business.example.com

    private JSONObject buildExperienceContext() {
        JSONObject experienceContext = new JSONObject();
        // 商户名称
        //experienceContext.set("brand_name", merchantName);
        // 不在PayPal页面中设置收货地址信息，只付款
        experienceContext.set("shipping_preference", "NO_SHIPPING");
        experienceContext.set("landing_page", "LOGIN");
        // 立即支付
        experienceContext.set("user_action", "PAY_NOW");
        // 首选支付方式,仅接受立即付款
        experienceContext.set("payment_method_preference", "IMMEDIATE_PAYMENT_REQUIRED");
        experienceContext.set("locale_code", "en-US");
        experienceContext.set("return_url", returnUrl);
        if (StrUtil.isNotBlank(cancelUrl)) {
            experienceContext.set("cancel_url", cancelUrl);
        }
        return experienceContext;
    }
}
