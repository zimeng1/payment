package com.mc.payment.core.service.manager.deposit;

import com.mc.payment.api.model.req.DepositReq;
import com.mc.payment.api.model.rsp.DepositRsp;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.model.req.DepositConfirmReq;
import com.mc.payment.core.service.model.req.DepositMockReq;
import com.mc.payment.core.service.model.rsp.ConfirmRsp;

public interface DepositManager {

    /**
     * 入金申请流程
     * <p>
     * 1. 校验
     * 2. 保存记录
     * 3. 执行入金
     * 4. webhook
     *
     * @param merchantId
     * @param merchantName
     * @param req
     * @return
     */
    RetResult<DepositRsp> requestProcess(String merchantId, String merchantName, DepositReq req);


    RetResult<ConfirmRsp> executeProcess(DepositConfirmReq req);

    /**
     * 模拟入金
     *
     * @param req
     */
    void mock(DepositMockReq req);
}
