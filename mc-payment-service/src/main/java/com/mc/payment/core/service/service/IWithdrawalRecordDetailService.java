package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.WithdrawalRecordDetailEntity;
import com.mc.payment.core.service.model.req.WithdrawalRecordDetailReq;
import com.mc.payment.core.service.model.rsp.WithdrawalDetailRsp;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author conor
 * @since 2024-04-22 17:48:32
 */
public interface IWithdrawalRecordDetailService extends IService<WithdrawalRecordDetailEntity> {

    List<WithdrawalRecordDetailEntity> list(String recordId);

    /**
     * 出金记录明细列表
     * @param req
     * @return
     */
    BasePageRsp<WithdrawalDetailRsp> getDetailPageList(WithdrawalRecordDetailReq req);

    /**
     * 出金明细保存
     * @param entity
     * @return
     */
    boolean saveOrUpdateByTxHash(WithdrawalRecordDetailEntity entity);
}

