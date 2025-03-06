package com.mc.payment.core.service.manager;

import cn.hutool.core.util.StrUtil;
import com.mc.payment.common.constant.ExceptionTypeEnum;
import com.mc.payment.common.exception.BusinessException;
import com.mc.payment.core.service.entity.PayProtocolEntity;
import com.mc.payment.core.service.model.enums.StatusEnum;
import com.mc.payment.core.service.model.req.platform.CryptoProtocolUpdateReq;
import com.mc.payment.core.service.model.req.platform.FiatPayTypeUpdateReq;
import com.mc.payment.core.service.service.ChannelAssetConfigService;
import com.mc.payment.core.service.service.MerchantChannelAssetService;
import com.mc.payment.core.service.service.PayProtocolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PayProtocolManagerImpl implements PayProtocolManager {
    private final PayProtocolService payProtocolService;
    private final ChannelAssetConfigService channelAssetConfigService;
    private final MerchantChannelAssetService merchantChannelAssetService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean fiatUpdateById(FiatPayTypeUpdateReq req) {
        return this.updateById(req.getId(), req.getStatus(), req.getIconData(), StrUtil.EMPTY);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cryptoUpdateById(CryptoProtocolUpdateReq req) {
        return this.updateById(req.getId(), req.getStatus(), req.getIconData(), req.getRegularExpression());
    }


    private boolean updateById(String id, Integer status, String iconData, String regularExpression) {
        PayProtocolEntity entity = payProtocolService.getById(id);
        if (entity == null) {
            throw new BusinessException(ExceptionTypeEnum.NOT_EXIST);
        }
        regularExpression = regularExpression == null ? "" : regularExpression.trim();
        // 没有改动则直接返回成功
        if (status.equals(entity.getStatus())
                && StrUtil.equals(iconData, entity.getIconData())
                && StrUtil.equals(regularExpression, entity.getRegularExpression())) {
            return true;
        }
        // 是否从启用更新为了禁用
        if (entity.getStatus() == StatusEnum.ACTIVE.getCode() && status == StatusEnum.DISABLE.getCode()) {
            // 如果是更新为禁用
            // 需要将通道资产禁用
            channelAssetConfigService.disableAsset(entity.getAssetType(), null, entity.getNetProtocol());
            // 需要将所有商户中配置的这个资产删除
            merchantChannelAssetService.removeAsset(null, null, entity.getAssetType(),
                    null, entity.getNetProtocol());
        }
        entity.setStatus(status);
        entity.setIconData(iconData);
        entity.setRegularExpression(regularExpression);
        return payProtocolService.updateById(entity);
    }
}
