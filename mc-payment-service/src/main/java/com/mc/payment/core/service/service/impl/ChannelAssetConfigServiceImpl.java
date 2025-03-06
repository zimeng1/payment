package com.mc.payment.core.service.service.impl;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.ChannelAssetConfigEntity;
import com.mc.payment.core.service.mapper.ChannelAssetConfigMapper;
import com.mc.payment.core.service.model.enums.StatusEnum;
import com.mc.payment.core.service.model.req.channel.ChannelAssetConfigListReq;
import com.mc.payment.core.service.model.req.channel.ChannelAssetConfigPageReq;
import com.mc.payment.core.service.service.ChannelAssetConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Conor
 * @description 针对表【mcp_channel_asset_config(通道资产配置(1.9.0基于mcp_channel_asset和mcp_asset_config迁移))】的数据库操作Service实现
 * @createDate 2024-11-05 11:28:56
 */
@Service
public class ChannelAssetConfigServiceImpl extends ServiceImpl<ChannelAssetConfigMapper, ChannelAssetConfigEntity>
        implements ChannelAssetConfigService {

    @Override
    public BasePageRsp<ChannelAssetConfigEntity> selectPage(ChannelAssetConfigPageReq req) {
        Page<ChannelAssetConfigEntity> page = new Page<>(req.getCurrent(), req.getSize());
        this.lambdaQuery()
                .likeRight(StrUtil.isNotBlank(req.getId()), ChannelAssetConfigEntity::getId, req.getId())
                .eq(ChannelAssetConfigEntity::getAssetType, req.getAssetType())
                .eq(req.getChannelSubType() != null, ChannelAssetConfigEntity::getChannelSubType, req.getChannelSubType())
                .like(StrUtil.isNotBlank(req.getChannelAssetName()), ChannelAssetConfigEntity::getChannelAssetName, req.getChannelAssetName())
                .like(StrUtil.isNotBlank(req.getChannelNetProtocol()),
                        ChannelAssetConfigEntity::getChannelNetProtocol, req.getChannelNetProtocol())
                .eq(req.getStatus() != null, ChannelAssetConfigEntity::getStatus, req.getStatus())
                .in(req.getAssetNames() != null && !req.getAssetNames().isEmpty(),
                        ChannelAssetConfigEntity::getAssetName, req.getAssetNames())
                .in(req.getNetProtocols() != null && !req.getNetProtocols().isEmpty(),
                        ChannelAssetConfigEntity::getNetProtocol, req.getNetProtocols())
                .orderByDesc(BaseNoLogicalDeleteEntity::getUpdateTime, BaseNoLogicalDeleteEntity::getId)
                .page(page);
        return BasePageRsp.valueOf(page);
    }


    @Override
    public List<ChannelAssetConfigEntity> list(ChannelAssetConfigListReq req) {
        return this.lambdaQuery()
                .eq(req.getChannelSubType() != null, ChannelAssetConfigEntity::getChannelSubType, req.getChannelSubType())
                .eq(req.getAssetType() != null, ChannelAssetConfigEntity::getAssetType, req.getAssetType())
                .eq(req.getStatus() != null, ChannelAssetConfigEntity::getStatus, req.getStatus())
                .orderByAsc(ChannelAssetConfigEntity::getChannelSubType, BaseNoLogicalDeleteEntity::getCreateTime)
                .list();
    }

    @Override
    public List<ChannelAssetConfigEntity> queryAccountNotExistWallet(String accountId, Integer channelSubType) {
        return baseMapper.queryAccountNotExistWallet(accountId, channelSubType);
    }

    @Override
    public void disableAsset(Integer assetType, String assetName, String netProtocol) {
        // 资产名称和协议不能同时为空
        if (StrUtil.isBlank(assetName) && StrUtil.isBlank(netProtocol)) {
            throw new ValidateException("资产名称和协议不能同时为空");
        }
        this.lambdaUpdate()
                .eq(assetName != null, ChannelAssetConfigEntity::getAssetName, assetName)
                .eq(netProtocol != null, ChannelAssetConfigEntity::getNetProtocol, netProtocol)
                .set(ChannelAssetConfigEntity::getStatus, StatusEnum.DISABLE.getCode())
                .update();
    }
}




