package com.mc.payment.core.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mc.payment.core.service.entity.ChannelAssetConfigEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Conor
 * @description 针对表【mcp_channel_asset_config(通道资产配置(1.9.0基于mcp_channel_asset和mcp_asset_config迁移))】的数据库操作Mapper
 * @createDate 2024-11-05 11:28:56
 * @Entity com.mc.payment.core.service.entity.ChannelAssetConfigEntity
 */
public interface ChannelAssetConfigMapper extends BaseMapper<ChannelAssetConfigEntity> {
    /**
     * 查询该账号下的钱包没有的通道资产
     * <p>
     * 迁移至ChannelAssetMapper
     *
     * @param accountId
     * @param channelSubType
     * @return
     */
    List<ChannelAssetConfigEntity> queryAccountNotExistWallet(@Param("accountId") String accountId, @Param("channelSubType") Integer channelSubType);
}




