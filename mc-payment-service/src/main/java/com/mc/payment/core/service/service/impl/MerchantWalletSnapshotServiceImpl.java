package com.mc.payment.core.service.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.api.model.req.QueryMerchantSnapshotReq;
import com.mc.payment.api.model.rsp.MerchantWalletSnapshotRsp;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.MerchantWalletSnapshotEntity;
import com.mc.payment.core.service.mapper.MerchantWalletSnapshotMapper;
import com.mc.payment.core.service.service.IMerchantWalletSnapshotService;
import org.springframework.stereotype.Service;

@Service
public class MerchantWalletSnapshotServiceImpl extends ServiceImpl<MerchantWalletSnapshotMapper, MerchantWalletSnapshotEntity> implements IMerchantWalletSnapshotService {
    @Override
    public BasePageRsp<MerchantWalletSnapshotRsp> getMerchatSnapshotPage(QueryMerchantSnapshotReq req) {
        if (req.getSize() > 500) {
            req.setSize(500);
        }
        Page<MerchantWalletSnapshotRsp> page = new Page<>(req.getCurrent(), req.getSize());
        baseMapper.getMerchatSnapshotPage(page, req);
        return BasePageRsp.valueOf(page);
    }
}
