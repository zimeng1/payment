package com.mc.payment.gateway.adapter;

import com.mc.payment.common.base.RetResult;
import com.mc.payment.gateway.PaymentGateway;
import com.mc.payment.gateway.channels.passtopay.model.req.PassToPayCreateOrderReq;
import com.mc.payment.gateway.channels.passtopay.model.rsp.PassToPayCreateOrderRsp;
import com.mc.payment.gateway.channels.passtopay.service.PassToPayService;
import com.mc.payment.gateway.model.req.*;
import com.mc.payment.gateway.model.rsp.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class PassToPayPaymentGatewayAdapter implements PaymentGateway {
    private final PassToPayService service;

    @Override
    public RetResult<GatewayDepositRsp> deposit(GatewayDepositReq req) {
        RetResult<GatewayDepositRsp> retResult = new RetResult<>();

        GatewayDepositRsp gatewayDepositRsp = null;
        try {
            PassToPayCreateOrderReq createOrderReq = new PassToPayCreateOrderReq();
            createOrderReq.setMchOrderNo(req.getTransactionId());
            createOrderReq.setWayCode(req.getPayType());
            // 转成分
            int amount = new BigDecimal(req.getAmount()).multiply(BigDecimal.valueOf(100)).intValue();
            createOrderReq.setAmount(amount);
            createOrderReq.setCurrency(req.getCurrency());
            createOrderReq.setReqTime(System.currentTimeMillis());
            Map<String, Object> extraMap = req.getExtraMap();
            // 这里如果没有用户id给对方,则使用随机生成的
            createOrderReq.setCustNo(extraMap.getOrDefault("userId", "C" + System.currentTimeMillis()).toString());
            // 用户注册时间 填默认值 1622016572190
            createOrderReq.setRegisterTime(1622016572190L);
            createOrderReq.setVersion("1.1");
            // 非必填 不传给上游
//        req.setUserName("")
//        req.setMbrTel("")
//        req.setIdNo("")
            createOrderReq.setNotifyUrl(req.getCallbackUrl());
            createOrderReq.setReturnUrl(req.getSuccessPageUrl());
            Object expiredTime = extraMap.get("expiredTime");
            if (expiredTime != null) {
                createOrderReq.setExpiredTime(Integer.parseInt(expiredTime.toString()));
            }
            createOrderReq.setExtParam(req.getRemark());

            RetResult<PassToPayCreateOrderRsp> result = service.createOrder(createOrderReq);

            if (result.isSuccess()) {
                PassToPayCreateOrderRsp data = result.getData();
                gatewayDepositRsp = new GatewayDepositRsp();
                gatewayDepositRsp.setTransactionId(data.getMchOrderNo());
                gatewayDepositRsp.setRedirectUrl(data.getPayData());
                gatewayDepositRsp.setChannelTransactionId(data.getPayOrderId());
                retResult = RetResult.data(gatewayDepositRsp);
            } else {
                retResult = RetResult.error(result.getMsg());
            }
        } catch (Exception e) {
            log.error("deposit API call failed", e);
            retResult = RetResult.error("The API call failed");
        } finally {
            log.info("deposit API call req:{},result: {}", req, retResult);
        }
        return retResult;
    }

    @Override
    public RetResult<GatewayQueryDepositRsp> queryDeposit(GatewayQueryDepositReq req) {
        return null;
    }

    @Override
    public RetResult<GatewayWithdrawalRsp> withdrawal(GatewayWithdrawalReq req) {
        return null;
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
