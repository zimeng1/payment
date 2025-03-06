package com.mc.payment.core.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.core.service.entity.ChannelCostAssetEntity;
import com.mc.payment.core.service.mapper.ChannelCostAssetMapper;
import com.mc.payment.core.service.service.ChannelCostAssetService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Conor
 * @description 针对表【mcp_channel_cost_asset(成本规则支持的资产)】的数据库操作Service实现
 * @createDate 2025-01-09 16:35:51
 */
@Service
public class ChannelCostAssetServiceImpl extends ServiceImpl<ChannelCostAssetMapper, ChannelCostAssetEntity>
        implements ChannelCostAssetService {
    @Override
    public List<ChannelCostAssetEntity> queryListByCostId(String costId) {
        return this.lambdaQuery().eq(ChannelCostAssetEntity::getCostId, costId).list();
    }
}




