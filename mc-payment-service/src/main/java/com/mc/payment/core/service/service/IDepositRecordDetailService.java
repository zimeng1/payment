package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.DepositRecordDetailEntity;
import com.mc.payment.core.service.model.req.DepositRecordDetailReq;
import com.mc.payment.core.service.model.req.GetDepositRecordDetailReq;
import com.mc.payment.core.service.model.rsp.DepositDetailRsp;
import com.mc.payment.core.service.model.rsp.DepositRecordDetailRsp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author conor
 * @since 2024-04-22 17:48:32
 */
public interface IDepositRecordDetailService extends IService<DepositRecordDetailEntity> {

    DepositRecordDetailEntity getOne(Integer channelSubType, String txHash);

    boolean saveOrUpdateByTxHash(DepositRecordDetailEntity entity);

    List<DepositRecordDetailEntity> list(String recordId);

    List<DepositRecordDetailEntity> listByRecordIds(List<String> recordIds);

    List<DepositRecordDetailEntity> listByRecordIdAndExpireTime(String recordId, Date expireTime);

    // 统计累计金额
    BigDecimal sumAccumulatedAmount(String recordId);

    List<DepositRecordDetailRsp> detailList(GetDepositRecordDetailReq req);

    /**
     * 入金明细列表
     *
     * @param req
     * @return
     */
    BasePageRsp<DepositDetailRsp> getDetailPageList(DepositRecordDetailReq req);
}

