package com.mc.payment.core.service.facade;

import com.mc.payment.core.service.entity.WithdrawalRecordEntity;
import com.mc.payment.gateway.channels.cheezeepay.model.req.CheezeePayWithdrawalCallbackReq;
import jakarta.servlet.http.HttpServletResponse;

public interface ICheezeePayServiceFacade {

    String withdrawalCallback(String payload, CheezeePayWithdrawalCallbackReq callbackReq, HttpServletResponse response);

    WithdrawalRecordEntity withdrawalCallBackHandle(CheezeePayWithdrawalCallbackReq callbackReq);

}
