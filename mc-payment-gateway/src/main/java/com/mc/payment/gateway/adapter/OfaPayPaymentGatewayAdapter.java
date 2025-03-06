package com.mc.payment.gateway.adapter;

import cn.hutool.core.date.DateUtil;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.gateway.PaymentGateway;
import com.mc.payment.gateway.channels.ofapay.model.req.*;
import com.mc.payment.gateway.channels.ofapay.model.rsp.*;
import com.mc.payment.gateway.channels.ofapay.service.OfaPayService;
import com.mc.payment.gateway.model.req.*;
import com.mc.payment.gateway.model.rsp.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Component
public class OfaPayPaymentGatewayAdapter implements PaymentGateway {

    private final OfaPayService ofaPayService;

    @Override
    public RetResult<GatewayDepositRsp> deposit(GatewayDepositReq req) {
        //todo 参数校验
        BigDecimal amount = new BigDecimal(req.getAmount());
        if (amount.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException("金额需为整数!");
        }
        RetResult<GatewayDepositRsp> retResult = new RetResult<>();
        OfaPayDepositRsp ofaPayRsp = null;
        try {
            OfaPayDepositReq ofaPayReq = OfaPayDepositReq.valueOf(req);
            ofaPayRsp = ofaPayService.deposit(ofaPayReq);
            if ("1".equals(ofaPayRsp.getStatus()) && "00".equals(ofaPayRsp.getRespcode())) {
                GatewayDepositRsp rsp = new GatewayDepositRsp();
                rsp.setTransactionId(req.getTransactionId());
                rsp.equals(ofaPayRsp.getOrderno());
                rsp.setRedirectUrl(ofaPayRsp.getUrl());
                retResult = RetResult.data(rsp);
            } else {
                retResult = RetResult.error(ofaPayRsp.getRespmsg());
            }
        } catch (Exception e) {
            retResult = RetResult.error("The API call failed");
            log.error("deposit API call failed", e);
        } finally {
            retResult.addExtraField("originalResponse", ofaPayRsp);
            log.info("deposit API call req:{},result: {}", req, retResult);
        }
        return retResult;
    }

    @Override
    public RetResult<GatewayQueryDepositRsp> queryDeposit(GatewayQueryDepositReq req) {
        //todo 参数校验
        RetResult<GatewayQueryDepositRsp> retResult = new RetResult<>();
        OfaPayQueryDepositRsp ofaPayRsp = null;
        try {
            //todo 建立状态码映射关系
            OfaPayQueryDepositReq ofaPayReq = OfaPayQueryDepositReq.valueOf(req);
            ofaPayRsp = ofaPayService.queryDeposit(ofaPayReq);
            if ("00".equals(ofaPayRsp.getRespcode()) || "10".equals(ofaPayRsp.getRespcode())) {
                GatewayQueryDepositRsp rsp = new GatewayQueryDepositRsp();
                rsp.setStatus(ofaPayRsp.getStatus());
                rsp.setCompleteTime(DateUtil.parse(ofaPayRsp.getResptime()));
                retResult = RetResult.data(rsp);
            } else if ("01".equals(ofaPayRsp.getRespcode())) {
                // 未找到交易
                retResult = RetResult.error("No such transaction");
            } else {
                retResult = RetResult.error("channel error:" + ofaPayRsp.getRespcode());
            }
        } catch (Exception e) {
            retResult = RetResult.error("The API call failed");
            log.error("queryDeposit API call failed", e);
        } finally {
            retResult.addExtraField("originalResponse", ofaPayRsp);
            log.info("queryDeposit API call req:{},result: {}", req, retResult);
        }
        return retResult;
    }

    @Override
    public RetResult<GatewayWithdrawalRsp> withdrawal(GatewayWithdrawalReq req) {
        BigDecimal amount = new BigDecimal(req.getAmount());
        if (amount.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException("金额需为整数!");
        }
        RetResult<GatewayWithdrawalRsp> retResult = new RetResult<>();
        OfaPayWithdrawalRsp ofaPayRsp = null;
        try {
            //todo 建立状态码映射关系
            OfaPayWithdrawalReq ofaPayReq = OfaPayWithdrawalReq.valueOf(req);
            ofaPayRsp = ofaPayService.withdrawal(ofaPayReq);
            GatewayWithdrawalRsp rsp = new GatewayWithdrawalRsp();
            rsp.setTransactionId(req.getTransactionId());
            // todo 余额不足 response:{"prc":"-1","errcode":"13","msg":"Insufficient balance"}
            if ("00".equals(ofaPayRsp.getErrcode())) {
                rsp.setStatus(1);
                rsp.setChannelTransactionId(ofaPayRsp.getOrderno());
                retResult = RetResult.data(rsp);
            } else if ("10".equals(ofaPayRsp.getErrcode())) {
                rsp.setStatus(0);
                retResult = RetResult.data(rsp);
            } else if ("11".equals(ofaPayRsp.getErrcode())) {
                rsp.setStatus(-1);
                retResult = RetResult.error(ofaPayRsp.getMsg());
            } else if ("19".equals(ofaPayRsp.getErrcode())) {
                // 未找到交易
                retResult = RetResult.error("No such transaction");
            } else {
                retResult = RetResult.error("channel error:" + ofaPayRsp.getMsg());
            }
        } catch (Exception e) {
            retResult = RetResult.error("The API call failed");
            log.error("withdrawal API call failed", e);
        } finally {
            retResult.addExtraField("originalResponse", ofaPayRsp);
            log.info("withdrawal API call req:{},result: {}", req, retResult);
        }
        return retResult;
    }

    @Override
    public RetResult<GatewayQueryWithdrawalRsp> queryWithdrawal(GatewayQueryWithdrawalReq req) {
        //todo 参数校验
        RetResult<GatewayQueryWithdrawalRsp> retResult = new RetResult<>();
        OfaPayQueryWithdrawalRsp ofaPayRsp = null;
        try {
            //todo 建立状态码映射关系
            OfaPayQueryWithdrawalReq ofaPayReq = OfaPayQueryWithdrawalReq.valueOf(req);
            ofaPayRsp = ofaPayService.queryWithdrawal(ofaPayReq);
            if ("00".equals(ofaPayRsp.getErrcode()) || "10".equals(ofaPayRsp.getErrcode())) {
                GatewayQueryWithdrawalRsp rsp = new GatewayQueryWithdrawalRsp();
                if ("F".equals(ofaPayRsp.getStatus())) {
                    rsp.setStatus(-1);
                } else if ("S".equals(ofaPayRsp.getStatus())) {
                    rsp.setStatus(1);
                } else {
                    // I
                    rsp.setStatus(0);
                }
                rsp.setCompleteTime(DateUtil.parse(ofaPayRsp.getResptime()));
                retResult = RetResult.data(rsp);
            } else if ("19".equals(ofaPayRsp.getErrcode())) {
                // 未找到交易
                retResult = RetResult.error("No such transaction");
            } else {
                retResult = RetResult.error("channel error:" + ofaPayRsp.getErrcode());
            }
        } catch (Exception e) {
            retResult = RetResult.error("The API call failed");
            log.error("queryWithdrawal API call failed", e);
        } finally {
            retResult.addExtraField("originalResponse", ofaPayRsp);
            log.info("queryWithdrawal API call req:{},result: {}", req, retResult);
        }
        return retResult;
    }


    @Override
    public RetResult<GatewayQueryBalanceRsp> queryBalance(GatewayQueryBalanceReq req) {
        RetResult<GatewayQueryBalanceRsp> retResult = new RetResult<>();
        OfaPayQueryBalanceRsp ofaPayQueryBalanceRsp = null;
        try {
            ofaPayQueryBalanceRsp = ofaPayService.queryBalance(new OfaPayQueryBalanceReq(req.getAccountId()));
            if ("1".equals(ofaPayQueryBalanceRsp.getPrc()) && "00".equals(ofaPayQueryBalanceRsp.getErrcode())) {
                GatewayQueryBalanceRsp gatewayQueryBalanceRsp = new GatewayQueryBalanceRsp();
                gatewayQueryBalanceRsp.setBalance(ofaPayQueryBalanceRsp.getBalance());
                retResult = RetResult.data(gatewayQueryBalanceRsp);
            } else {
                retResult = RetResult.error(ofaPayQueryBalanceRsp.getMsg());
            }
        } catch (Exception e) {
            retResult = RetResult.error("The API call failed");
            log.error("queryBalance API call failed", e);
        } finally {
            retResult.addExtraField("originalResponse", ofaPayQueryBalanceRsp);
            log.info("queryBalance API call req:{},result: {}", req, retResult);
        }
        return retResult;
    }

}
