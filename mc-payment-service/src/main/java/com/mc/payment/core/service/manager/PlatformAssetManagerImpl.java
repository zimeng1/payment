package com.mc.payment.core.service.manager;

import cn.hutool.core.util.StrUtil;
import com.mc.payment.common.constant.ExceptionTypeEnum;
import com.mc.payment.common.exception.BusinessException;
import com.mc.payment.core.service.entity.PlatformAssetEntity;
import com.mc.payment.core.service.model.enums.StatusEnum;
import com.mc.payment.core.service.model.req.platform.PlatformAssetUpdateReq;
import com.mc.payment.core.service.service.ChannelAssetConfigService;
import com.mc.payment.core.service.service.MerchantChannelAssetService;
import com.mc.payment.core.service.service.PlatformAssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlatformAssetManagerImpl implements PlatformAssetManager {
    private final PlatformAssetService platformAssetService;
    private final ChannelAssetConfigService channelAssetConfigService;
    private final MerchantChannelAssetService merchantChannelAssetService;

    @Override
    public boolean updateById(PlatformAssetUpdateReq req) {
        PlatformAssetEntity entity = platformAssetService.getById(req.getId());
        if (entity == null) {
            throw new BusinessException(ExceptionTypeEnum.NOT_EXIST);
        }
        // 没有改动则直接返回成功
        if (req.getStatus().equals(entity.getStatus())
                && StrUtil.equals(req.getIconData(), entity.getIconData())) {
            return true;
        }
        // 是否从启用更新为了禁用
        if (entity.getStatus() == StatusEnum.ACTIVE.getCode() && req.getStatus() == StatusEnum.DISABLE.getCode()) {
            // 如果是更新为禁用
            // 需要将通道资产禁用
            channelAssetConfigService.disableAsset(entity.getAssetType(), entity.getAssetName(), null);
            // 需要将所有商户中配置的这个资产删除
            merchantChannelAssetService.removeAsset(null, null, entity.getAssetType(), entity.getAssetName(), null);
        }

        entity.setStatus(req.getStatus());
        entity.setIconData(req.getIconData());
        return platformAssetService.updateById(entity);
    }
}
