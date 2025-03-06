package com.mc.payment.core.service.facade;

import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.entity.WithdrawalRecordEntity;
import com.mc.payment.core.service.model.req.DepositConfirmReq;
import com.mc.payment.core.service.model.req.DepositStatusReq;
import com.mc.payment.core.service.model.req.WithdrawalConfirmReq;
import com.mc.payment.core.service.model.rsp.ConfirmRsp;
import com.mc.payment.core.service.model.rsp.DepositInfoRsp;
import com.mc.payment.core.service.model.rsp.WithDrawalInfoRsp;

/**
 * 收银台服务门面
 *
 * @author Conor
 * @since 2024-07-25 10:53:16.631
 */
public interface IPaymentPageServiceFacade {

    RetResult<DepositInfoRsp> depositInfo(String encryptId);

    RetResult<ConfirmRsp> depositConfirmOld(DepositConfirmReq req);

    RetResult<ConfirmRsp> depositConfirm(DepositConfirmReq req);

    RetResult<WithDrawalInfoRsp> withdrawInfo(String encryptId);

    RetResult<ConfirmRsp> withdrawConfirm(WithdrawalConfirmReq req);

    /**
     * 出金
     *
     * @param withdrawalRecord
     * @return
     */

    String fundWithDrawal(WithdrawalRecordEntity withdrawalRecord);

    String fundWithDrawalHandle(WithdrawalRecordEntity withdrawalRecord);

    /**
     * 法币出金webhook
     *
     * @param withdrawalRecord
     */
    void legalDrawalWebHook(WithdrawalRecordEntity withdrawalRecord);

    /**
     * 入金状态查询
     *
     * @param req
     * @return
     */
    Integer depositStatus(DepositStatusReq req);

}
