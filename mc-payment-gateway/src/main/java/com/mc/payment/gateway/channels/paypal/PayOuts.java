package com.mc.payment.gateway.channels.paypal;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

import java.io.IOException;

class PayOuts {

    public static void main(String[] args) throws IOException {
        String clientId = "AYsGU9ihpFqUVeLVlXl0ZuM0v94YozPwKqQ3dV4eKZ5YNywQ6YBpITHuPjp4qZGr1pDeHAi9Nq6U1_Fh";
        String secret = "EIJ29ZDgIHv5sRihB65e4j9M6rN68rFWJ93IK8Gjh0ayXAomh6Ql8AOLk62ZdeLMc0zkSKCVEzj_c2OH";

        String txnId = IdUtil.fastSimpleUUID();
        JSONObject jsonObject = new JSONObject();
        JSONObject item = new JSONObject();
        JSONArray items = new JSONArray();
        item.set("recipient_type", "EMAIL");
        JSONObject amount = new JSONObject();
        amount.set("value", "100.00");
        amount.set("currency", "JPY");
        item.set("amount", amount);
        item.set("sender_item_id", txnId);
        item.set("purpose", "CASHBACK");
        item.set("receiver", "sb-swxvd32861374@personal.example.com");
        item.set("note", "Thank you.");
        items.add(item);
        jsonObject.set("items", items);

        JSONObject senderBatchHeader = new JSONObject();
        senderBatchHeader.set("sender_batch_id", txnId);
        senderBatchHeader.set("email_subject", "You have a payout!");
        senderBatchHeader.set("email_message", "You have received a payout! Thanks for using our service!");
        jsonObject.set("sender_batch_header", senderBatchHeader);

        HttpResponse response = HttpUtil.createPost("https://api-m.sandbox.paypal.com/v1/payments/payouts")
                .basicAuth(clientId, secret)
                .header("Content-Type", "application/json")
                .header("PayPal-Request-Id", txnId)
                .body(jsonObject.toString())
                .execute();
        System.out.println(response.getStatus());
        System.out.println(response.body());
    }
}
