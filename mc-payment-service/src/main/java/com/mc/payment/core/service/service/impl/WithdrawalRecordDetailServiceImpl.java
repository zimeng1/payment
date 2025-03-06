package com.mc.payment.core.service.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.WithdrawalRecordDetailEntity;
import com.mc.payment.core.service.mapper.WithdrawalRecordDetailMapper;
import com.mc.payment.core.service.model.req.WithdrawalRecordDetailReq;
import com.mc.payment.core.service.model.rsp.WithdrawalDetailRsp;
import com.mc.payment.core.service.service.IWithdrawalRecordDetailService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WithdrawalRecordDetailServiceImpl extends ServiceImpl<WithdrawalRecordDetailMapper, WithdrawalRecordDetailEntity> implements IWithdrawalRecordDetailService {

    @Override
    public List<WithdrawalRecordDetailEntity> list(String recordId) {
        return this.list(Wrappers.lambdaQuery(WithdrawalRecordDetailEntity.class).eq(WithdrawalRecordDetailEntity::getRecordId, recordId));
    }

    @Override
    public BasePageRsp<WithdrawalDetailRsp> getDetailPageList(WithdrawalRecordDetailReq req) {
        Page<WithdrawalDetailRsp> page = new Page<>(req.getCurrent(), req.getSize());
        baseMapper.getDetailPageList(page,req);
        return BasePageRsp.valueOf(page);
    }

    @Override
    public boolean saveOrUpdateByTxHash(WithdrawalRecordDetailEntity entity) {
        long count = this.count(Wrappers.lambdaQuery(WithdrawalRecordDetailEntity.class)
                .eq(WithdrawalRecordDetailEntity::getTxHash, entity.getTxHash()));
        return count > 0 ? updateById(entity) : save(entity);
    }
}
