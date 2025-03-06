package com.mc.payment.gateway.channels.cheezeepay.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.CommonConstant;
import com.mc.payment.common.constant.NationAndCurrencyCodeEnum;
import com.mc.payment.gateway.channels.cheezeepay.config.CheezeePayConfig;
import com.mc.payment.gateway.channels.cheezeepay.model.req.CheezeePayDepositReq;
import com.mc.payment.gateway.channels.cheezeepay.model.req.CheezeePayWithdrawalReq;
import com.mc.payment.gateway.channels.cheezeepay.model.rsp.CheezeePayDepositRsp;
import com.mc.payment.gateway.channels.cheezeepay.model.rsp.CheezeePayWithdrawalRsp;
import com.mc.payment.gateway.channels.cheezeepay.utils.CheeseTradeRSAUtil;
import com.mc.payment.gateway.channels.ofapay.config.OfaPayConfig;
import com.mc.payment.gateway.channels.ofapay.util.OfaPayUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class CheezeePayServiceImpl implements CheezeePayService {

    private final CheezeePayConfig cheezeePayConfig;

    private static final String needReplaceStrFirst = "{method}";
    private static final String needReplaceStrSecond = "{nation}";


    @Override
    public RetResult<CheezeePayDepositRsp> deposit(CheezeePayDepositReq req, String currency) {

        RetResult<CheezeePayDepositRsp> retResult = null;

        String privateKey = cheezeePayConfig.getPrivateKey();
        Map<String, Object> requestParams = BeanUtil.beanToMap(req);

        String resultStr = null;
        try {
            String platSign = CheeseTradeRSAUtil.getSign(requestParams, privateKey);
            requestParams.put("sign", platSign);
            req.setSign(platSign);

            String requestBody = JSONUtil.toJsonStr(requestParams);
            log.info("cheezeePay deposit request params: {}", requestBody);

            resultStr = HttpUtil.createPost(getRequestUrl(currency, CommonConstant.PAYMENT))
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36")
                    .body(requestBody)
                    .execute()
                    .body();
            JSONObject jsonObject = JSONUtil.parseObj(resultStr);
            String code = jsonObject.getStr("code");
            if ("000000".equals(code)) {
                log.info("cheezeePay deposit request success: {}" , resultStr);
                CheezeePayDepositRsp resp = JSONUtil.toBean(jsonObject, CheezeePayDepositRsp.class);
                retResult = RetResult.data(resp);
            } else {
                log.warn("cheezeePay deposit request failed, resultStr: {} ", resultStr);
            }
        } catch (Exception e) {
            log.error("cheezeePay deposit request exception: {}", e.getMessage());
            retResult = RetResult.error("cheezeePay deposit request exception");
        }
        return retResult;
    }

    @Override
    public RetResult<CheezeePayWithdrawalRsp> withdrawal(CheezeePayWithdrawalReq req, String currency) {
        RetResult<CheezeePayWithdrawalRsp> retResult = null;
        String privateKey = cheezeePayConfig.getPrivateKey();
        Map<String, Object> requestParams = BeanUtil.beanToMap(req);

        String resultStr = null;
        try {
            String platSign = CheeseTradeRSAUtil.getSign(requestParams, privateKey);
            requestParams.put("sign", platSign);
            req.setSign(platSign);

            String requestBody = JSONUtil.toJsonStr(requestParams);
            String requestUrl = getRequestUrl(currency, CommonConstant.PAYOUT);
            log.info("cheezeePay withdrawal request params: {} url:{}", requestBody, requestUrl);
            resultStr = HttpUtil.createPost(requestUrl)
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36")
                    .body(requestBody)
                    .execute()
                    .body();
            JSONObject jsonObject = JSONUtil.parseObj(resultStr);
            String code = jsonObject.getStr("code");
            if ("000000".equals(code)) {
                log.info("cheezeePay withdrawal request success: {}" , resultStr);
                CheezeePayWithdrawalRsp resp = JSONUtil.toBean(jsonObject, CheezeePayWithdrawalRsp.class);
                retResult = RetResult.data(resp);
            } else {
                log.warn("cheezeePay withdrawal request failed, resultStr: {} ", resultStr);
            }
        } catch (Exception e) {
            log.error("cheezeePay withdrawal request exception: {}", e.getMessage());
            retResult = RetResult.error("cheezeePay depwithdrawalosit request exception");
        }
        return retResult;
    }

    @Override
    public RetResult<CheezeePayWithdrawalRsp> withdrawalOrderQuery() {
        return null;
    }

    public String  getRequestUrl(String currency, String replaceStrFirst){
        String realUrl = "";
        String replaceStrSecond = "";
        String cheezeepayAppBaseUrl = cheezeePayConfig.getCheezeepayAppBaseUrl();
        if(NationAndCurrencyCodeEnum.IDR.name().equals(currency)){
            replaceStrSecond = NationAndCurrencyCodeEnum.IDR.getNationEnName();
        }

        if(NationAndCurrencyCodeEnum.INR.name().equals(currency)){
            replaceStrSecond = NationAndCurrencyCodeEnum.INR.getNationEnName();
        }

        if(NationAndCurrencyCodeEnum.THB.name().equals(currency)){
            replaceStrSecond = NationAndCurrencyCodeEnum.THB.getNationEnName();
        }

        if(NationAndCurrencyCodeEnum.BRL.name().equals(currency)){
            replaceStrSecond = NationAndCurrencyCodeEnum.BRL.getNationEnName();
        }

        realUrl = cheezeepayAppBaseUrl.replace(needReplaceStrFirst, replaceStrFirst)
                .replace(needReplaceStrSecond, replaceStrSecond);
        return realUrl;
    }
}
