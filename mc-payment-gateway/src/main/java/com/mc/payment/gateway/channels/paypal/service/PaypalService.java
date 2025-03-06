package com.mc.payment.gateway.channels.paypal.service;

import com.mc.payment.common.base.RetResult;
import com.mc.payment.gateway.channels.paypal.model.req.CreateOrderReq;
import com.mc.payment.gateway.channels.paypal.model.req.PayPalWithdrawalReq;
import com.mc.payment.gateway.model.rsp.GatewayDepositRsp;
import com.mc.payment.gateway.model.rsp.GatewayWithdrawalRsp;

public interface PaypalService {

    RetResult<GatewayDepositRsp> createOrder(CreateOrderReq req);

    // Capture the order
    RetResult<String> captureOrder(String orderId);

    RetResult<GatewayWithdrawalRsp> payOut(PayPalWithdrawalReq req);
}
