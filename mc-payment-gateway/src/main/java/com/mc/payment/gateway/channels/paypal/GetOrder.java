package com.mc.payment.gateway.channels.paypal;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;

public class GetOrder {
    public static void main(String[] args) {
        // https://api.sandbox.paypal.com/v2/checkout/orders/40621604J7721893W

        String clientId = "AYsGU9ihpFqUVeLVlXl0ZuM0v94YozPwKqQ3dV4eKZ5YNywQ6YBpITHuPjp4qZGr1pDeHAi9Nq6U1_Fh";
        String secret = "EIJ29ZDgIHv5sRihB65e4j9M6rN68rFWJ93IK8Gjh0ayXAomh6Ql8AOLk62ZdeLMc0zkSKCVEzj_c2OH";
        // 查订单 状态为VERIFIED 表示已经支付,可以捕获收款
//        String body = HttpUtil.createGet("https://api.sandbox.paypal.com/v2/checkout/orders/3TM16909CS6525808")
//                .basicAuth(clientId, secret)
//                .execute()
//                .body();
//        System.out.println(body);


        HttpResponse response = HttpUtil.createPost("https://api.sandbox.paypal.com/v2/checkout/orders/02736568VD016813U/capture")
                .header("Content-Type", "application/json")
                .basicAuth(clientId, secret)
                .execute();
        int status = response.getStatus();
        System.out.println(status);
        String body1 = response.body();
        System.out.println(body1);
    }
}
