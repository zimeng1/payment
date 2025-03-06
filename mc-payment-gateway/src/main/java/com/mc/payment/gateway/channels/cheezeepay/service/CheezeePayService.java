package com.mc.payment.gateway.channels.cheezeepay.service;


import com.mc.payment.common.base.RetResult;
import com.mc.payment.gateway.channels.cheezeepay.model.req.CheezeePayDepositReq;
import com.mc.payment.gateway.channels.cheezeepay.model.req.CheezeePayWithdrawalCallbackReq;
import com.mc.payment.gateway.channels.cheezeepay.model.req.CheezeePayWithdrawalReq;
import com.mc.payment.gateway.channels.cheezeepay.model.rsp.CheezeePayDepositRsp;
import com.mc.payment.gateway.channels.cheezeepay.model.rsp.CheezeePayWithdrawalRsp;

public interface CheezeePayService {
    /**
     * 入金
     */
    RetResult<CheezeePayDepositRsp> deposit(CheezeePayDepositReq req, String currency) throws Exception;

    /**
     * 出金
     */
    RetResult<CheezeePayWithdrawalRsp> withdrawal(CheezeePayWithdrawalReq req, String currency);

    /**
     * 出金结果查询
     */
    RetResult<CheezeePayWithdrawalRsp> withdrawalOrderQuery();
}
