package com.mc.payment.core.service.service;

import com.mc.payment.gateway.channels.ezeebill.model.req.EzeebillDepositCallBackReq;
import com.mc.payment.gateway.channels.ezeebill.model.req.EzeebillWithdrawalCallBackReq;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * webhook回调服务类
 */
public interface IWebhookService {

    /**
     * 入金支付回调
     *
     * @param payload
     * @return
     */
    String depositCallBack(String payload);

    /**
     * 出金支付回调
     *
     * @param payload
     * @return
     */
    String withdrawCallBack(String payload);

    String cheezeePayWithdrawCallBack(String payload, HttpServletResponse response);

    /**
     * PayPal入金回调
     * @param payload
     * @param request
     * @return
     */
    String paypalOrderApproved(String payload, HttpServletRequest request);

    /**
     * PayPal出金回调
     * @param payload
     * @param request
     * @return
     */
    String paypalPayoutsItemSucceeded(String payload, HttpServletRequest request);

    String passToPayDepositCallBack(String payload);

    /**
     * Ezeebill入金回调
     * @param req
     * @return
     */
    String ezeebillDepositCallBack(EzeebillDepositCallBackReq req);

    /**
     * Ezeebill出金回调
     * @param req
     * @return
     */
    String ezeebillWithdrawalCallBack(EzeebillWithdrawalCallBackReq req);

    /**
     * cheezeepay 入金回调
     *
     * @param payload
     */
    String cheezeePayDepositCallBack(String payload, HttpServletResponse response);
    
    
    String paypalPayoutsItemFailed(String payload, HttpServletRequest request);
}
