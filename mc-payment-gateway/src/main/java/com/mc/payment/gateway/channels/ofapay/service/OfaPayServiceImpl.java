package com.mc.payment.gateway.channels.ofapay.service;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.mc.payment.gateway.channels.ofapay.config.OfaPayConfig;
import com.mc.payment.gateway.channels.ofapay.model.req.*;
import com.mc.payment.gateway.channels.ofapay.model.rsp.*;
import com.mc.payment.gateway.channels.ofapay.util.OfaPayUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Component
public class OfaPayServiceImpl implements OfaPayService {
    private static final String BASE_URL = "https://www.jzc899.com";

    private final OfaPayConfig ofaPayConfig;

    private <T> T sendPostRequest(String url, OfaPayBaseReq req, Class<T> clazz) {
        String apiKey = ofaPayConfig.getKeyMap().get(req.getScode());
        if (apiKey == null) {
            throw new IllegalArgumentException("No API key found for the provided scode");
        }
        try {
            apiKey = ofaPayConfig.decrypt(apiKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        req.setSign(OfaPayUtil.generateSignature(req, apiKey));
        String reqJson = JSONUtil.toJsonStr(req);
        log.info("OfaPayService url:{},request:{}", url, reqJson);
        String postResponse = HttpUtil.post(BASE_URL + url, reqJson);
        log.info("OfaPayService response:{}", postResponse);
        return JSONUtil.toBean(postResponse, clazz);
    }

    /**
     * 充值
     *
     * @param req
     * @return
     */
    @Override
    public OfaPayDepositRsp deposit(OfaPayDepositReq req) {
        return sendPostRequest("/pay/order.aspx", req, OfaPayDepositRsp.class);
    }

    /**
     * 查询充值订单
     *
     * @param req
     * @return
     */
    @Override
    public OfaPayQueryDepositRsp queryDeposit(OfaPayQueryDepositReq req) {
        return sendPostRequest("/pay/query.aspx", req, OfaPayQueryDepositRsp.class);
    }

    /**
     * 提现
     *
     * @param req
     * @return
     */
    @Override
    public OfaPayWithdrawalRsp withdrawal(OfaPayWithdrawalReq req) {
        return sendPostRequest("/betdf/df.aspx", req, OfaPayWithdrawalRsp.class);
    }

    /**
     * 查询提现订单
     *
     * @param req
     * @return
     */
    @Override
    public OfaPayQueryWithdrawalRsp queryWithdrawal(OfaPayQueryWithdrawalReq req) {
        return sendPostRequest("/betdf/querydf.aspx", req, OfaPayQueryWithdrawalRsp.class);
    }

    /**
     * 查询余额
     *
     * @param req
     * @return
     */
    @Override
    public OfaPayQueryBalanceRsp queryBalance(OfaPayQueryBalanceReq req) {
        return sendPostRequest("/betdf/querybalance.aspx", req, OfaPayQueryBalanceRsp.class);
    }

    /**
     * 充值回调
     * Note 1. After received the transaction status, beside verify the code,if the status is successful, we suggest to check in the backend also (3.4) to confirm the actual status, to avoid any fake data, and please reply a string as“success”.
     * 文档提示 接收到回调后除了验证代码外,还需要调用queryDepositOrder接口查询,确认实际状态,避免虚假数据,并且请回复字符串“success”。
     *
     * @param req
     * @param callbackProcessor 充值回调处理器
     * @return 成功则返回success
     */
    @Override
    public String depositCallback(OfaPayDepositCallbackReq req, Function<OfaPayDepositCallbackReq, Boolean> callbackProcessor) {
        //todo
        return req.getStatus() == 1 && "00".equals(req.getRespcode()) && callbackProcessor.apply(req) ? "success" : "fail";
    }

    /**
     * 提现回调
     * Note 1. After received the transaction status, beside verify the code,if the status is successful, we suggest to check in the backend also (3.4) to confirm the actual status, to avoid any fake data, and please reply a string as“success”.
     * 文档提示 接收到回调后除了验证代码外,还需要调用queryDepositOrder接口查询,确认实际状态,避免虚假数据,并且请回复字符串“SUCCESS”。
     *
     * @param req
     * @param callbackProcessor 提现回调处理器
     * @return 成功则返回success
     */
    @Override
    public String withdrawalCallback(OfaPayWithdrawalCallbackReq req, Function<OfaPayWithdrawalCallbackReq, Boolean> callbackProcessor) {
        if (!("S".equals(req.getStatus()) && "00".equals(req.getRespcode()))) {
            return "fail";
        }
        OfaPayQueryWithdrawalReq queryWithdrawalReq = new OfaPayQueryWithdrawalReq();
        queryWithdrawalReq.setScode(req.getScode());
        queryWithdrawalReq.setOrderid(req.getOrderid());
        OfaPayQueryWithdrawalRsp ofaPayQueryWithdrawalRsp = this.queryWithdrawal(queryWithdrawalReq);
        return "1".equals(ofaPayQueryWithdrawalRsp.getPrc()) && "S".equals(ofaPayQueryWithdrawalRsp.getStatus()) && "00".equals(ofaPayQueryWithdrawalRsp.getErrcode()) && callbackProcessor.apply(req) ? "SUCCESS" : "fail";
    }

}
