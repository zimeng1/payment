package com.mc.payment.gateway.adapter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.CommonConstant;
import com.mc.payment.common.constant.NationAndCurrencyCodeEnum;
import com.mc.payment.gateway.PaymentGateway;
import com.mc.payment.gateway.channels.cheezeepay.config.CheezeePayConfig;
import com.mc.payment.gateway.channels.cheezeepay.model.req.CheezeePayDepositReq;
import com.mc.payment.gateway.channels.cheezeepay.model.req.CheezeePayWithdrawalReq;
import com.mc.payment.gateway.channels.cheezeepay.model.rsp.CheezeePayDepositRsp;
import com.mc.payment.gateway.channels.cheezeepay.model.rsp.CheezeePayWithdrawalRsp;
import com.mc.payment.gateway.channels.cheezeepay.service.CheezeePayService;
import com.mc.payment.gateway.model.req.*;
import com.mc.payment.gateway.model.rsp.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class CheezeePayPaymentGatewayAdapter implements PaymentGateway {

    private final CheezeePayService cheezeePayService;

    private final CheezeePayConfig cheezeePayConfig;

    @Override
    public RetResult<GatewayDepositRsp> deposit(GatewayDepositReq req) {
        //参数校验
        BigDecimal amount = new BigDecimal(req.getAmount());
        String currency = req.getCurrency();
        if (amount.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException("金额需为整数!");
        }

        RetResult<GatewayDepositRsp> retResult = new RetResult<>();
        RetResult<CheezeePayDepositRsp> result = null;
        CheezeePayDepositRsp resp = null;
        try {
            CheezeePayDepositReq cheezeePayDepositReq = new CheezeePayDepositReq();
            //币种和参数校验
            depositParamAssemble(currency, cheezeePayDepositReq, req);
            result = cheezeePayService.deposit(cheezeePayDepositReq, currency);

            resp = result.getData();
            if(result.isSuccess()){
                GatewayDepositRsp rsp = new GatewayDepositRsp();
                rsp.setTransactionId(req.getTransactionId());
                rsp.setChannelTransactionId(resp.getTradeNo());
                rsp.setRedirectUrl(resp.getCashierLink());
                retResult = RetResult.data(rsp);
            } else {
                retResult = RetResult.error(resp.getMessage());
            }
        } catch (Exception e) {
            retResult = RetResult.error("The API call failed");
            log.error("deposit API call failed", e);
        } finally {
            retResult.addExtraField("originalResponse", resp);
            log.info("deposit API call req:{},result: {}", req, retResult);
        }
        return retResult;
    }

    @Override
    public RetResult<GatewayQueryDepositRsp> queryDeposit(GatewayQueryDepositReq req) {
        RetResult<GatewayQueryDepositRsp> retResult = new RetResult<>();
        return retResult;
    }

    @Override
    public RetResult<GatewayWithdrawalRsp> withdrawal(GatewayWithdrawalReq req) {
        BigDecimal amount = new BigDecimal(req.getAmount());
        String currency = req.getAssetName();
        //参数校验
        checkPaymentParams(req);
        //金额校验
        checkAmount(amount, currency, CommonConstant.PAYOUT);
        RetResult<GatewayWithdrawalRsp> respResult = new RetResult<>();
        RetResult<CheezeePayWithdrawalRsp> result = null;
        try {
            CheezeePayWithdrawalReq cheezeePayWithdrawalReq = new CheezeePayWithdrawalReq();
            withdrawlParamAssemble(currency, cheezeePayWithdrawalReq, req);
            result = cheezeePayService.withdrawal(cheezeePayWithdrawalReq, req.getAssetName());

            CheezeePayWithdrawalRsp resp = result.getData();
            if(result.isSuccess()){
                GatewayWithdrawalRsp rsp = new GatewayWithdrawalRsp();
                rsp.setTransactionId(req.getTransactionId());
                rsp.setChannelTransactionId(resp.getTradeNo());
                rsp.setStatus(1);
                respResult = RetResult.data(rsp);
            } else {
                respResult = RetResult.error(resp.getMsg());
            }
        } catch (Exception e) {
            respResult = RetResult.error("The API call failed");
            log.error("withdrawal API call failed", e);
        }
        return respResult;
    }

    @Override
    public RetResult<GatewayQueryWithdrawalRsp> queryWithdrawal(GatewayQueryWithdrawalReq req) {
        return null;
    }

    @Override
    public RetResult<GatewayQueryBalanceRsp> queryBalance(GatewayQueryBalanceReq req) {
        return null;
    }

    private void depositParamAssemble(String currency, CheezeePayDepositReq cheezeePayDepositReq, GatewayDepositReq req){
        Map<String, Object> extraMap = req.getExtraMap();
        String paymentMethod = (String) extraMap.get("paymentMethod");
        cheezeePayDepositReq.setAppId(cheezeePayConfig.getCheezeepayAppId());
        cheezeePayDepositReq.setMerchantId(cheezeePayConfig.getCheezeepayMchId());
        cheezeePayDepositReq.setMchOrderNo(req.getTransactionId());
        cheezeePayDepositReq.setAmount(req.getAmount());
        cheezeePayDepositReq.setNotifyUrl(req.getCallbackUrl());
        cheezeePayDepositReq.setTimestamp(Timestamp.valueOf(LocalDateTime.now()).getTime());

        if(NationAndCurrencyCodeEnum.IDR.name().equals(currency)){
            cheezeePayDepositReq.setEmail("scorpio648654167@gmail.com");
            cheezeePayDepositReq.setLanguage("en");
            cheezeePayDepositReq.setName("Magiccompass");
            cheezeePayDepositReq.setProductDetail("mc-payment trading");
            cheezeePayDepositReq.setPaymentMode(paymentMethod);
            cheezeePayDepositReq.setRedirectUrl(req.getSuccessPageUrl());
            return;
        }

        if(NationAndCurrencyCodeEnum.INR.name().equals(currency)){
            cheezeePayDepositReq.setEmail("scorpio648654167@gmail.com");
            cheezeePayDepositReq.setLanguage("en");
            cheezeePayDepositReq.setName("Magiccompass");
            cheezeePayDepositReq.setPaymentMode(paymentMethod);
            cheezeePayDepositReq.setReturnUrl(req.getSuccessPageUrl());
            return;
        }

        if(NationAndCurrencyCodeEnum.THB.name().equals(currency)){
            cheezeePayDepositReq.setPaymentMethod("ALL");
            return;
        }

        if(NationAndCurrencyCodeEnum.BRL.name().equals(currency)){
            cheezeePayDepositReq.setPaymentMode(paymentMethod);
            cheezeePayDepositReq.setLanguage("pt_br");
        }
    }

    private void withdrawlParamAssemble(String currency, CheezeePayWithdrawalReq cheezeePayWithdrawalReq, GatewayWithdrawalReq req){

        Map<String, Object> extraMap = req.getExtraMap();

        cheezeePayWithdrawalReq.setAppId(cheezeePayConfig.getCheezeepayAppId());
        cheezeePayWithdrawalReq.setMerchantId(cheezeePayConfig.getCheezeepayMchId());
        cheezeePayWithdrawalReq.setMchOrderNo(req.getTransactionId());

        cheezeePayWithdrawalReq.setNotifyUrl(req.getCallbackUrl());
        cheezeePayWithdrawalReq.setTimestamp(Timestamp.valueOf(LocalDateTime.now()).getTime());
        cheezeePayWithdrawalReq.setEmail("scorpio648654167@gmail.com");
        cheezeePayWithdrawalReq.setName("Magiccompass");
        cheezeePayWithdrawalReq.setPaymentMethod(req.getNetProtocol());

        CheezeePayWithdrawalReq.PayeeAccountInfos payeeAccountInfos = new CheezeePayWithdrawalReq.PayeeAccountInfos();

        //金额处理
        BigDecimal bigDecimal = new BigDecimal(req.getAmount());
        cheezeePayWithdrawalReq.setAmount(bigDecimal.setScale(2, BigDecimal.ROUND_DOWN).toString());
        if(NationAndCurrencyCodeEnum.IDR.name().equals(currency)){
            cheezeePayWithdrawalReq.setAmount(bigDecimal.setScale(0, BigDecimal.ROUND_DOWN).toString());
            cheezeePayWithdrawalReq.setPaymentMethod("BANK_ID");
            payeeAccountInfos.setBankCardNumber(req.getAddress());
            payeeAccountInfos.setBankCode(req.getBankCode());
            cheezeePayWithdrawalReq.setPayeeAccountInfos(JSONUtil.toJsonStr(payeeAccountInfos));
            return;
        }

        if(NationAndCurrencyCodeEnum.INR.name().equals(currency)){
            //String name = (String) extraMap.get("name");
            //String branchName = (String) extraMap.get("branchName");

            cheezeePayWithdrawalReq.setPaymentMethod("BANK_IN");
            //已有
            payeeAccountInfos.setAccountNumber(req.getAddress());
            payeeAccountInfos.setBankCode(req.getBankCode());
            payeeAccountInfos.setName("NA");
            payeeAccountInfos.setIfscCode(req.getBankNum());
            payeeAccountInfos.setAccountType("Bank");
            payeeAccountInfos.setBankName("NA");
            payeeAccountInfos.setBranchName("NA");
            cheezeePayWithdrawalReq.setPayeeAccountInfos(JSONUtil.toJsonStr(payeeAccountInfos));
            cheezeePayWithdrawalReq.setLanguage("en");
            return;
        }

        if(NationAndCurrencyCodeEnum.THB.name().equals(currency)){
            cheezeePayWithdrawalReq.setPaymentMethod("BANK_TH");
            payeeAccountInfos.setAccountNumber(req.getAddress());
            payeeAccountInfos.setBankCode(req.getBankCode());
            cheezeePayWithdrawalReq.setPayeeAccountInfos(JSONUtil.toJsonStr(payeeAccountInfos));
            return;
        }

        if(NationAndCurrencyCodeEnum.BRL.name().equals(currency)){
            String pixType = (String) extraMap.get("pixType");
            String pixAccount = (String) extraMap.get("pixAccount");
            String taxNumber = (String) extraMap.get("taxNumber");
            cheezeePayWithdrawalReq.setPaymentMethod("PIX");
            cheezeePayWithdrawalReq.setEmail(null);
            payeeAccountInfos.setPixType(pixType);
            payeeAccountInfos.setPixAccount(pixAccount);
            payeeAccountInfos.setTaxNumber(taxNumber);
            cheezeePayWithdrawalReq.setPayeeAccountInfos(JSONUtil.toJsonStr(payeeAccountInfos));
        }
    }

    private void checkPaymentParams(GatewayWithdrawalReq req) {
        Map<String, Object> params = req.getExtraMap();
        String assetName = req.getAssetName();

        if(NationAndCurrencyCodeEnum.BRL.name().equals(assetName)){
            // 校验每个必要参数
            if (CollUtil.isEmpty(params)) {
                throw new IllegalArgumentException("cheezeepay brazil needs params cannot be empty");
            }

            // 校验 pixType
            String pixType = (String) params.getOrDefault("pixType", "");
            if (StrUtil.isBlank(pixType)) {
                throw new IllegalArgumentException("cheezeepay brazil payeeAccountInfos: pixType cannot be empty");
            }

            // 校验 pixAccount
            String pixAccount = (String) params.getOrDefault("pixAccount", "");
            if (StrUtil.isBlank(pixAccount)) {
                throw new IllegalArgumentException("cheezeepay brazil payeeAccountInfos: pixAccount cannot be empty");
            }

            // 校验 taxNumber number
            String taxNumber = (String) params.getOrDefault("taxNumber", "");
            if (StrUtil.isBlank(taxNumber)) {
                throw new IllegalArgumentException("cheezeepay brazil payeeAccountInfos: taxNumber cannot be empty");
            }
        }

    }


    //金额校验
    private void checkAmount(BigDecimal amount, String currency, String type) {
        if (NationAndCurrencyCodeEnum.IDR.name().equals(currency)) {
            // 校验整数
            if (amount.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0) {
                throw new IllegalArgumentException("金额需为整数!");
            }

            // 校验印尼金额范围
            if (CommonConstant.PAYMENT.equalsIgnoreCase(type)) {
                if (amount.compareTo(new BigDecimal("10000")) < 0 || amount.compareTo(new BigDecimal("50000000")) > 0) {
                    throw new IllegalArgumentException("Indonesia: Payment amount must be between 10000 and 50,000,000 IDR.");
                }
            } else {
                if (amount.compareTo(new BigDecimal("10000")) < 0 || amount.compareTo(new BigDecimal("50000000")) > 0) {
                    throw new IllegalArgumentException("Indonesia: Payout amount must be between 10000 and 50,000,000 IDR.");
                }
            }
            return;
        }

        if (NationAndCurrencyCodeEnum.INR.name().equals(currency)) {
            // 校验印度金额范围
            if (CommonConstant.PAYMENT.equalsIgnoreCase(type)) {
                if (amount.compareTo(new BigDecimal("100")) < 0 || amount.compareTo(new BigDecimal("50000")) > 0) {
                    throw new IllegalArgumentException("India: Payment amount must be between 100 and 50,000 INR.");
                }
            } else {
                if (amount.compareTo(new BigDecimal("100")) < 0 || amount.compareTo(new BigDecimal("100000")) > 0) {
                    throw new IllegalArgumentException("India: Payout amount must be between 100 and 100,000 INR.");
                }
            }
            return;
        }

        if (NationAndCurrencyCodeEnum.THB.name().equals(currency)) {
            // 校验泰国金额范围
            if (CommonConstant.PAYMENT.equalsIgnoreCase(type)) {
                if (amount.compareTo(new BigDecimal("500")) < 0 || amount.compareTo(new BigDecimal("150000")) > 0) {
                    throw new IllegalArgumentException("Thailand: Payment amount must be between 500 and 150,000 Thai Baht.");
                }
            } else {
                if (amount.compareTo(new BigDecimal("20")) < 0 || amount.compareTo(new BigDecimal("49999")) > 0) {
                    throw new IllegalArgumentException("Thailand: Payout amount must be between 20 and 49,999 Thai Baht.");
                }
            }
            return;
        }

        if (NationAndCurrencyCodeEnum.BRL.name().equals(currency)) {
            // 校验巴西金额范围
            if (CommonConstant.PAYMENT.equalsIgnoreCase(type)) {
                if (amount.compareTo(new BigDecimal("10")) < 0 || amount.compareTo(new BigDecimal("50000")) > 0) {
                    throw new IllegalArgumentException("Brazil: Payment amount must be between 10 and 50,000 BRL.");
                }
            } else {
                if (amount.compareTo(new BigDecimal("20")) < 0 || amount.compareTo(new BigDecimal("50000")) > 0) {
                    throw new IllegalArgumentException("Brazil: Payout amount must be between 20 and 50,000 BRL.");
                }
            }
        }
    }
}
