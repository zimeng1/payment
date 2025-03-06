package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.core.service.entity.ChannelCostAssetEntity;

import java.util.List;

/**
 * @author Conor
 * @description 针对表【mcp_channel_cost_asset(成本规则支持的资产)】的数据库操作Service
 * @createDate 2025-01-09 16:35:51
 */
public interface ChannelCostAssetService extends IService<ChannelCostAssetEntity> {

    List<ChannelCostAssetEntity> queryListByCostId(String costId);
}
