package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.api.model.req.QueryMerchantSnapshotReq;
import com.mc.payment.api.model.rsp.MerchantWalletSnapshotRsp;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.MerchantWalletSnapshotEntity;

public interface IMerchantWalletSnapshotService extends IService<MerchantWalletSnapshotEntity> {

    BasePageRsp<MerchantWalletSnapshotRsp> getMerchatSnapshotPage(QueryMerchantSnapshotReq req);
}
