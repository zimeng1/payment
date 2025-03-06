package com.mc.payment.core.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mc.payment.api.model.req.QueryMerchantSnapshotReq;
import com.mc.payment.api.model.rsp.MerchantWalletSnapshotRsp;
import com.mc.payment.core.service.entity.MerchantWalletSnapshotEntity;
import org.apache.ibatis.annotations.Param;

public interface MerchantWalletSnapshotMapper extends BaseMapper<MerchantWalletSnapshotEntity> {

    IPage<MerchantWalletSnapshotRsp> getMerchatSnapshotPage(IPage<MerchantWalletSnapshotRsp> page, @Param("req") QueryMerchantSnapshotReq req);
}
