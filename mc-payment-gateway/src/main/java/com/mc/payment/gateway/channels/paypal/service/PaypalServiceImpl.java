package com.mc.payment.gateway.channels.paypal.service;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.gateway.channels.ezeebill.constants.EzeebillConstants;
import com.mc.payment.gateway.channels.paypal.model.req.CreateOrderReq;
import com.mc.payment.gateway.channels.paypal.model.req.PayPalWithdrawalReq;
import com.mc.payment.gateway.model.rsp.GatewayDepositRsp;
import com.mc.payment.gateway.model.rsp.GatewayWithdrawalRsp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaypalServiceImpl implements PaypalService {
    @Value("${app.paypal-client_id}")
    private String clientId;
    @Value("${app.paypal-secret}")
    private String secret;
    @Value("${app.paypal-api-base-url}")
    private String apiBaseUrl;

    @Override
    public RetResult<GatewayDepositRsp> createOrder(CreateOrderReq req) {
        RetResult<GatewayDepositRsp> retResult = new RetResult<>();
        int status = 0;
        String result = null;
        try {
            HttpResponse response = HttpUtil.createPost(apiBaseUrl + "/v2/checkout/orders")
                    .basicAuth(clientId, secret)
                    .header("Content-Type", "application/json")
                    .header("PayPal-Request-Id", req.getTransactionId())
                    .body(req.buildRequestBody().toString())
                    .execute();
            status = response.getStatus();
            result = response.body();
            if (status / 200 == 1) {
                GatewayDepositRsp rsp = new GatewayDepositRsp();
                rsp.setTransactionId(req.getTransactionId());
                JSONObject jsonObject = JSONUtil.parseObj(result);
                rsp.setChannelTransactionId(jsonObject.getStr("id"));
                for (Object links : jsonObject.getJSONArray("links")) {
                    JSONObject link = (JSONObject) links;
                    if ("payer-action".equals(link.getStr("rel"))) {
                        rsp.setRedirectUrl(link.getStr("href"));
                    }
                }
                retResult = RetResult.data(rsp);
            } else {
                retResult = RetResult.error("The API call status failed");
            }
        } catch (Exception e) {
            log.error("createOrder call failed req:{}", req, e);
            retResult = RetResult.error("The API call failed," + e.getMessage());
        } finally {
            log.info("createOrder call req:{},status:{},result: {}", req, status, result);
        }
        return retResult;
    }

    @Override
    public RetResult<String> captureOrder(String orderId) {
        RetResult<String> retResult = new RetResult<>();
        int status = 0;
        String result = null;
        try {
            HttpResponse response = HttpUtil.createPost(apiBaseUrl + "/v2/checkout/orders/" + orderId + "/capture")
                    .header("Content-Type", "application/json")
                    .basicAuth(clientId, secret)
                    .execute();
            status = response.getStatus();
            result = response.body();
            if (status / 200 == 1) {
                retResult = RetResult.data(result);
            } else {
                retResult = RetResult.error("The API call status failed");
                retResult.setData(result);
            }
            //todo   重复捕获    status=422 {"name":"UNPROCESSABLE_ENTITY","details":[{"issue":"ORDER_ALREADY_CAPTURED","description":"Order already captured.If 'intent=CAPTURE' only one capture per order is allowed."}],"message":"The requested action could not be performed, semantically incorrect, or failed business validation.","debug_id":"f3391198e4dae","links":[{"href":"https://developer.paypal.com/docs/api/orders/v2/#error-ORDER_ALREADY_CAPTURED","rel":"information_link","method":"GET"}]}
        } catch (Exception e) {
            log.error("captureOrder call failed orderId:{}", orderId, e);
            retResult = RetResult.error("The API call failed," + e.getMessage());
        } finally {
            log.info("captureOrder call orderId:{},status:{},result: {}", orderId, status, result);
        }
        return retResult;
    }

    @Override
    public RetResult<GatewayWithdrawalRsp> payOut(PayPalWithdrawalReq req) {
        RetResult<GatewayWithdrawalRsp> retResult;
        int status = 0;
        String result = null;
        try {
            HttpResponse response = HttpUtil.createPost(apiBaseUrl + "/v1/payments/payouts")
                    .basicAuth(clientId, secret)
                    .header("Content-Type", "application/json")
                    .header("PayPal-Request-Id", req.getTransactionId())
                    .body(req.buildRequestBody().toString())
                    .execute();
            status = response.getStatus();
            result = response.body();
            JSONObject jsonObject = JSONUtil.parseObj(result);
            
            GatewayWithdrawalRsp gatewayWithdrawalRsp = new GatewayWithdrawalRsp();
           
            if (status / 200 == 1) {
                GatewayWithdrawalRsp rsp = new GatewayWithdrawalRsp();
                rsp.setTransactionId(req.getTransactionId());
                rsp.setChannelTransactionId(jsonObject.getJSONObject("batch_header").getStr("payout_batch_id"));
                retResult = RetResult.data(rsp);
            } else {
                if(EzeebillConstants.PAYPAL_PAYOUT_INSUFFICIENT_BALANCE_CODE.equals(jsonObject.getStr("name"))){
                    gatewayWithdrawalRsp.setStatus(2);
                }
                retResult = RetResult.error("The API call status failed:"+jsonObject.getStr("message"),gatewayWithdrawalRsp);
            }
        } catch (Exception e) {
            log.error("payPal payOut call failed req:{}", req, e);
            retResult = RetResult.error("The API call failed," + e.getMessage());
        } finally {
            log.info("payPal payOut call req:{},status:{},result: {}", req, status, result);
        }
        return retResult;
    }
}
