package com.mc.payment.core.service.manager.withdrawal;

import com.mc.payment.api.model.req.WithdrawalReq;
import com.mc.payment.api.model.rsp.WithdrawalRsp;
import com.mc.payment.core.service.model.req.FundWithdrawalAuditReq;
import com.mc.payment.core.service.model.rsp.WithdrawalAuditRsp;
import com.mc.payment.core.service.model.rsp.WithdrawalReExecuteRsp;

public interface WithdrawalManager {

    /**
     * 出金申请流程
     *
     * @param merchantId
     * @param merchantName
     * @param req
     * @return
     */
    WithdrawalRsp requestProcess(String merchantId, String merchantName, WithdrawalReq req);

    /**
     * 出金审核流程
     *
     * @param req
     */
    WithdrawalAuditRsp auditProcess(FundWithdrawalAuditReq req);

    WithdrawalReExecuteRsp reExecuteProcess(String id);

    /**
     * 终止出金流程
     *
     * @param id
     */
    void cancelProcess(String id);

}
