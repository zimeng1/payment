package com.mc.payment.gateway.adapter;

import com.mc.payment.common.base.RetResult;
import com.mc.payment.gateway.PaymentGateway;
import com.mc.payment.gateway.channels.paypal.model.req.CreateOrderReq;
import com.mc.payment.gateway.channels.paypal.service.PaypalService;
import com.mc.payment.gateway.model.req.*;
import com.mc.payment.gateway.model.rsp.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class PayPalPaymentGatewayAdapter implements PaymentGateway {
    private final PaypalService paypalService;

    @Override
    public RetResult<GatewayDepositRsp> deposit(GatewayDepositReq req) {
        CreateOrderReq createOrder = new CreateOrderReq();
        createOrder.setTransactionId(req.getTransactionId());
        createOrder.setCurrency(req.getCurrency());
        createOrder.setAmount(req.getAmount());
        createOrder.setReturnUrl(req.getSuccessPageUrl());
        // 取消跳转的地址 setCancelUrl
        Map<String, Object> extraMap = req.getExtraMap();
        createOrder.setPayeeAccount((String) extraMap.get("payeeAccount"));
        return paypalService.createOrder(createOrder);
    }

    @Override
    public RetResult<GatewayQueryDepositRsp> queryDeposit(GatewayQueryDepositReq req) {
        return null;
    }

    @Override
    public RetResult<GatewayWithdrawalRsp> withdrawal(GatewayWithdrawalReq req) {
        return RetResult.error("paypal暂不支持提现");
        //勿删除，暂时注释
//        BigDecimal amount = new BigDecimal(req.getAmount());
//        if (amount.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0) {
//            throw new IllegalArgumentException("金额需为整数!");
//        }
//        PayPalWithdrawalReq payPalWithdrawalReq = PayPalWithdrawalReq.valueOf(req);
//        return paypalService.payOut(payPalWithdrawalReq);
    }

    @Override
    public RetResult<GatewayQueryWithdrawalRsp> queryWithdrawal(GatewayQueryWithdrawalReq req) {
        return null;
    }

    @Override
    public RetResult<GatewayQueryBalanceRsp> queryBalance(GatewayQueryBalanceReq req) {
        return null;
    }
}
