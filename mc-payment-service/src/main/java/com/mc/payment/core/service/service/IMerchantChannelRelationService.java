package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.core.service.entity.MerchantChannelRelationEntity;
import com.mc.payment.core.service.model.req.MerchantChannelSaveReq;

import java.util.List;

/**
 * <p>
 * 商户通道关系表 服务类
 * </p>
 * <p>
 * 该类已经废弃,请使用{@link com.mc.payment.core.service.service.MerchantChannelAssetService}
 *
 * @author conor
 * @since 2024-02-19 16:39:16
 */
@Deprecated
public interface IMerchantChannelRelationService extends IService<MerchantChannelRelationEntity> {
    /**
     * 更新关系
     * 新增的关系会增加,减少的关系会删除,关系不变则不做任何处理
     *
     * @param channelList
     */
    void updateRelation(String merchantId, List<MerchantChannelSaveReq> channelList);

    MerchantChannelRelationEntity getOne(String merchantId, Integer channelSubType);
}
