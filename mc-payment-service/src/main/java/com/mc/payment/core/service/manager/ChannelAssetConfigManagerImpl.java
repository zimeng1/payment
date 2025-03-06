package com.mc.payment.core.service.manager;

import cn.hutool.core.exceptions.ValidateException;
import com.mc.payment.common.constant.ExceptionTypeEnum;
import com.mc.payment.common.exception.BusinessException;
import com.mc.payment.core.service.entity.ChannelAssetConfigEntity;
import com.mc.payment.core.service.model.enums.AssetTypeEnum;
import com.mc.payment.core.service.model.enums.BooleanStatusEnum;
import com.mc.payment.core.service.model.enums.StatusEnum;
import com.mc.payment.core.service.model.req.channel.ChannelAssetConfigSaveReq;
import com.mc.payment.core.service.model.req.channel.ChannelAssetConfigUpdateReq;
import com.mc.payment.core.service.service.ChannelAssetConfigService;
import com.mc.payment.core.service.service.MerchantChannelAssetService;
import com.mc.payment.core.service.service.PayProtocolService;
import com.mc.payment.core.service.service.PlatformAssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChannelAssetConfigManagerImpl implements ChannelAssetConfigManager {
    private final ChannelAssetConfigService channelAssetConfigService;
    private final MerchantChannelAssetService merchantChannelAssetService;
    private static final String ALREADY_EXIST_MESSAGE = "[通道资产]已存在,请检查[平台资产名称]和[加密货币网络协议/法币支付类型]是否重复";
    private final PlatformAssetService platformAssetService;
    private final PayProtocolService payProtocolService;

    public String save(ChannelAssetConfigSaveReq req) {
        req.validate();
        boolean exists = channelAssetConfigService.lambdaQuery()
                .eq(ChannelAssetConfigEntity::getChannelSubType, req.getChannelSubType())
                .eq(ChannelAssetConfigEntity::getAssetType, req.getAssetType())
                .eq(ChannelAssetConfigEntity::getAssetName, req.getAssetName())
                .eq(ChannelAssetConfigEntity::getNetProtocol, req.getNetProtocol())
                .exists();
        if (exists) {
            throw new BusinessException(ExceptionTypeEnum.ALREADY_EXIST, ALREADY_EXIST_MESSAGE);
        }
        if (req.getStatus() == BooleanStatusEnum.ITEM_1.getCode()) {
            checkActivated(req.getAssetType(), req.getAssetName(), req.getNetProtocol());
        }
        ChannelAssetConfigEntity entity = req.convert();
        try {
            channelAssetConfigService.save(entity);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(ExceptionTypeEnum.ALREADY_EXIST, ALREADY_EXIST_MESSAGE);
        }
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(ChannelAssetConfigUpdateReq req) {
        req.validate();
        ChannelAssetConfigEntity entity = channelAssetConfigService.getById(req.getId());
        if (entity == null) {
            throw new BusinessException(ExceptionTypeEnum.NOT_EXIST);
        }
        if (!Objects.equals(req.getAssetType(), entity.getAssetType())) {
            throw new ValidateException("[资产类型]不可修改");
        }
        // 是否从启用更新为了禁用
        if (entity.getStatus() == StatusEnum.ACTIVE.getCode() && req.getStatus() == StatusEnum.DISABLE.getCode()) {
            // 如果是更新为禁用,则需要将所有商户中配置的这个资产删除
            merchantChannelAssetService.removeAsset(null, entity.getChannelSubType(), entity.getAssetType(),
                    entity.getAssetName(), entity.getNetProtocol());
        } else if (entity.getStatus() == StatusEnum.DISABLE.getCode() && req.getStatus() == StatusEnum.ACTIVE.getCode()) {
            // 如果是更新为启用,则检查平台资产和支付协议是否是激活状态
            checkActivated(req.getAssetType(), entity.getAssetName(), entity.getNetProtocol());
        }
        entity.setChannelAssetName(req.getChannelAssetName());
        entity.setChannelNetProtocol(req.getChannelNetProtocol());
        entity.setMinDepositAmount(req.getMinDepositAmount());
        entity.setMaxDepositAmount(req.getMaxDepositAmount());
        entity.setMinWithdrawalAmount(req.getMinWithdrawalAmount());
        entity.setMaxWithdrawalAmount(req.getMaxWithdrawalAmount());
        entity.setFeeAssetName(req.getFeeAssetName());
        entity.setStatus(req.getStatus());
        if (entity.getAssetType() == AssetTypeEnum.CRYPTO_CURRENCY.getCode()) {
            // 加密货币才需要更新的字段
            entity.setTokenAddress(req.getTokenAddress());
            entity.setTestHashUrl(req.getTestHashUrl());
            entity.setMainHashUrl(req.getMainHashUrl());
            entity.setDefaultEstimateFee(req.getDefaultEstimateFee());
        }
        boolean b;
        try {
            b = channelAssetConfigService.updateById(entity);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(ExceptionTypeEnum.ALREADY_EXIST, ALREADY_EXIST_MESSAGE);
        }
        return b;
    }

    private void checkActivated(Integer assetType, String assetName, String netProtocol) {
        //检查平台资产是否是激活状态
        boolean activated = platformAssetService.isActivated(assetType, assetName);
        if (!activated) {
            throw new ValidateException("平台资产[" + assetName + "]不是激活状态,请先激活");
        }
        //检查支付协议是否是激活状态
        activated = payProtocolService.isActivated(assetType, netProtocol);
        if (!activated) {
            throw new ValidateException("支付协议[" + netProtocol + "]不是激活状态,请先激活");
        }
    }
}
