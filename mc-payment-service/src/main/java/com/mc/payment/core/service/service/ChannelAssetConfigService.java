package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.ChannelAssetConfigEntity;
import com.mc.payment.core.service.model.req.channel.ChannelAssetConfigListReq;
import com.mc.payment.core.service.model.req.channel.ChannelAssetConfigPageReq;

import java.util.List;

/**
 * @author Conor
 * @description 针对表【mcp_channel_asset_config(通道资产配置(1.9.0基于mcp_channel_asset和mcp_asset_config迁移))】的数据库操作Service
 * @createDate 2024-11-05 11:28:56
 */
public interface ChannelAssetConfigService extends IService<ChannelAssetConfigEntity> {

    BasePageRsp<ChannelAssetConfigEntity> selectPage(ChannelAssetConfigPageReq req);

    List<ChannelAssetConfigEntity> list(ChannelAssetConfigListReq req);

    List<ChannelAssetConfigEntity> queryAccountNotExistWallet(String accountId, Integer channelSubType);

    void disableAsset(Integer assetType, String assetName, String netProtocol);
}
