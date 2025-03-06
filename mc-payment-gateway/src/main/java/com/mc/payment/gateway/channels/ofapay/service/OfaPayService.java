package com.mc.payment.gateway.channels.ofapay.service;

import com.mc.payment.gateway.channels.ofapay.model.req.*;
import com.mc.payment.gateway.channels.ofapay.model.rsp.*;

import java.util.function.Function;

/**
 * OfaPay 支付通道接口
 *
 * @author Conor
 * @since 2024-07-18 15:12:15.065
 */
public interface OfaPayService {


    /**
     * 充值
     *
     * @param req
     * @return
     */
    OfaPayDepositRsp deposit(OfaPayDepositReq req);

    /**
     * 查询充值订单
     *
     * @param req
     * @return
     */
    OfaPayQueryDepositRsp queryDeposit(OfaPayQueryDepositReq req);

    /**
     * 提现
     *
     * @param req
     * @return
     */
    OfaPayWithdrawalRsp withdrawal(OfaPayWithdrawalReq req);

    /**
     * 查询提现订单
     *
     * @param req
     * @return
     */
    OfaPayQueryWithdrawalRsp queryWithdrawal(OfaPayQueryWithdrawalReq req);

    /**
     * 查询余额
     *
     * @param req
     * @return
     */
    OfaPayQueryBalanceRsp queryBalance(OfaPayQueryBalanceReq req);

    /**
     * 充值回调
     *
     * @param req
     * @param callbackProcessor 充值回调处理器
     * @return 成功则返回success
     */
    String depositCallback(OfaPayDepositCallbackReq req, Function<OfaPayDepositCallbackReq, Boolean> callbackProcessor);

    /**
     * 提现回调
     * Note 1. After received the transaction status, beside verify the code,if the status is successful, we suggest to check in the backend also (3.4) to confirm the actual status, to avoid any fake data, and please reply a string as“success”.
     * 文档提示 接收到回调后除了验证代码外,还需要调用queryDepositOrder接口查询,确认实际状态,避免虚假数据,并且请回复字符串“success”。
     *
     * @param req
     * @param callbackProcessor 提现回调处理器
     * @return 成功则返回success
     */
    String withdrawalCallback(OfaPayWithdrawalCallbackReq req, Function<OfaPayWithdrawalCallbackReq, Boolean> callbackProcessor);
}
