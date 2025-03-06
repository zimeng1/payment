package com.mc.payment.core.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.core.service.entity.MerchantChannelRelationEntity;
import com.mc.payment.core.service.mapper.MerchantChannelRelationMapper;
import com.mc.payment.core.service.model.req.MerchantChannelSaveReq;
import com.mc.payment.core.service.service.IMerchantChannelRelationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 商户通道关系表 服务实现类
 * </p>
 *
 * @author conor
 * @since 2024-02-19 16:39:16
 */
@Service
public class MerchantChannelRelationServiceImpl extends ServiceImpl<MerchantChannelRelationMapper, MerchantChannelRelationEntity> implements IMerchantChannelRelationService {

    public MerchantChannelRelationServiceImpl(MerchantChannelRelationMapper merchantChannelRelationMapper) {
        this.baseMapper = merchantChannelRelationMapper;
    }

    @Override
    @Transactional
    public void updateRelation(String merchantId, List<MerchantChannelSaveReq> channelList) {
        this.remove(Wrappers.lambdaQuery(MerchantChannelRelationEntity.class).eq(MerchantChannelRelationEntity::getMerchantId, merchantId));
        if (CollUtil.isEmpty(channelList)) {
            return;
        }
        List<MerchantChannelRelationEntity> list = new ArrayList<>();
        for (MerchantChannelSaveReq req : channelList) {
            list.add(MerchantChannelRelationEntity.valueOf(merchantId, req));
        }
        this.saveBatch(list);
    }

    @Override
    public MerchantChannelRelationEntity getOne(String merchantId, Integer channelSubType) {
        return this.getOne(Wrappers.lambdaQuery(MerchantChannelRelationEntity.class)
                .eq(MerchantChannelRelationEntity::getMerchantId, merchantId)
                .eq(MerchantChannelRelationEntity::getChannelSubType, channelSubType));
    }


}
