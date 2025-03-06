package com.mc.payment.core.service.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.DepositRecordDetailEntity;
import com.mc.payment.core.service.mapper.DepositRecordDetailMapper;
import com.mc.payment.core.service.model.req.DepositRecordDetailReq;
import com.mc.payment.core.service.model.req.GetDepositRecordDetailReq;
import com.mc.payment.core.service.model.rsp.DepositDetailRsp;
import com.mc.payment.core.service.model.rsp.DepositRecordDetailRsp;
import com.mc.payment.core.service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author conor
 * @since 2024-04-22 17:48:32
 */
@Service
public class DepositRecordDetailServiceImpl extends ServiceImpl<DepositRecordDetailMapper, DepositRecordDetailEntity> implements IDepositRecordDetailService {

    @Autowired
    private IAssetLastQuoteService assetLastQuoteService;

    @Autowired
    private IDepositRecordService depositRecordService;

    @Autowired
    private MerchantWalletService merchantWalletService;

    @Autowired
    private IWebhookEventService webhookEventService;

    @Autowired
    private IMerchantService merchantService;

    @Override
    public DepositRecordDetailEntity getOne(Integer channelSubType, String txHash) {
        return this.getOne(Wrappers.lambdaQuery(DepositRecordDetailEntity.class)
                .eq(DepositRecordDetailEntity::getChannelSubType, channelSubType)
                .eq(DepositRecordDetailEntity::getTxHash, txHash));
    }

    @Override
    public boolean saveOrUpdateByTxHash(DepositRecordDetailEntity entity) {
        long count = this.count(Wrappers.lambdaQuery(DepositRecordDetailEntity.class)
                .eq(DepositRecordDetailEntity::getTxHash, entity.getTxHash()));
        return count > 0 ? updateById(entity) : save(entity);
    }

    @Override
    public List<DepositRecordDetailEntity> list(String recordId) {
        return this.list(Wrappers.lambdaQuery(DepositRecordDetailEntity.class).eq(DepositRecordDetailEntity::getRecordId, recordId));
    }

    @Override
    public List<DepositRecordDetailEntity> listByRecordIds(List<String> recordIds) {
        return this.list(Wrappers.lambdaQuery(DepositRecordDetailEntity.class).in(DepositRecordDetailEntity::getRecordId, recordIds));
    }

    @Override
    public List<DepositRecordDetailEntity> listByRecordIdAndExpireTime(String recordId, Date expireTime) {
        return this.list(Wrappers.lambdaQuery(DepositRecordDetailEntity.class)
                .eq(DepositRecordDetailEntity::getRecordId, recordId)
                .le(DepositRecordDetailEntity::getCreateTime, expireTime));
    }

    @Override
    public BigDecimal sumAccumulatedAmount(String recordId) {
        List<DepositRecordDetailEntity> list = this.list(recordId);
        return list.stream().map(DepositRecordDetailEntity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<DepositRecordDetailRsp> detailList(GetDepositRecordDetailReq req) {
        List<DepositRecordDetailRsp> detailList = baseMapper.getDetailListByRecordId(req);
        return detailList;
    }

    @Override
    public BasePageRsp<DepositDetailRsp> getDetailPageList(DepositRecordDetailReq req) {
        Page<DepositDetailRsp> page = new Page<>(req.getCurrent(), req.getSize());
        baseMapper.getDetailPageList(page, req);
        return BasePageRsp.valueOf(page);
    }
}
