package com.mc.payment.gateway.channels.paypal;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import com.mc.payment.gateway.channels.paypal.model.req.CreateOrderReq;

public class PayPal {
    public static void main(String[] args) {
        //curl -v -X POST "https://api-m.sandbox.paypal.com/v1/oauth2/token"\
        // -u "CLIENT_ID:CLIENT_SECRET"\
        // -H "Content-Type: application/x-www-form-urlencoded"\
        // -d "grant_type=client_credentials"
        String clientId = "AYsGU9ihpFqUVeLVlXl0ZuM0v94YozPwKqQ3dV4eKZ5YNywQ6YBpITHuPjp4qZGr1pDeHAi9Nq6U1_Fh";
        String secret = "EIJ29ZDgIHv5sRihB65e4j9M6rN68rFWJ93IK8Gjh0ayXAomh6Ql8AOLk62ZdeLMc0zkSKCVEzj_c2OH";
//        String body = HttpUtil.createPost("https://api-m.sandbox.paypal.com/v1/oauth2/token")
//                .basicAuth(clientId, secret)
//                .header("Content-Type", "application/x-www-form-urlencoded")
//                .form("grant_type", "client_credentials")
//                .execute()
//                .body();
//        System.out.println(body);

        JSONObject jsonObject = new JSONObject();
//        jsonObject.set("intent", "CAPTURE");
//        JSONObject purchaseUnit = new JSONObject();
//        JSONObject amount = new JSONObject();
//        amount.set("currency_code", "USD");
//        amount.set("value", "100.00");
//        purchaseUnit.set("amount", amount);
//        JSONArray purchaseUnits = new JSONArray();
//        purchaseUnits.add(purchaseUnit);
//        jsonObject.set("purchase_units", purchaseUnits);

        CreateOrderReq createOrder = new CreateOrderReq();
        createOrder.setCurrency("USD");
        createOrder.setAmount("1000.00");
        createOrder.setReturnUrl("https://www.zhihu.com/");
        createOrder.setCancelUrl("https://www.google.com/");
        createOrder.setPayeeAccount("sb-muac032771589@business.example.com");
        jsonObject = createOrder.buildRequestBody();
        System.out.println(jsonObject);


        HttpResponse response = HttpUtil.createPost("https://api-m.sandbox.paypal.com/v2/checkout/orders")
                .basicAuth(clientId, secret)
                .header("Content-Type", "application/json")
                .header("PayPal-Request-Id", IdUtil.fastSimpleUUID())
                .body(jsonObject.toString())
                .execute();

        System.out.println(response.getStatus());
        System.out.println(response.body());
    }
}
