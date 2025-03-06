package com.mc.payment.core.service.manager;

import com.mc.payment.core.service.entity.ChannelCostEntity;
import com.mc.payment.core.service.model.dto.AssetSimpleDto;
import com.mc.payment.core.service.model.req.ChannelCostSaveReq;
import com.mc.payment.core.service.model.req.ChannelCostUpdateReq;
import com.mc.payment.core.service.model.req.QueryCostAssetListReq;

import java.math.BigDecimal;
import java.util.List;

public interface ChannelCostManager {
    /**
     * 根据id获取通道成本数据 并且查出支持的资产列表
     *
     * @param id
     * @return
     */
    ChannelCostEntity getById(String id);

    String save(ChannelCostSaveReq req);

    boolean updateById(ChannelCostUpdateReq req);

    /**
     * 获取可选的资产列表,已经配置了成本规则的资产不会再次出现
     *
     * @param req
     * @return
     */
    List<AssetSimpleDto> queryAssetList(QueryCostAssetListReq req);

    /**
     * 计算通道成本
     *
     * @param businessAction
     * @param channelSubType
     * @param assetName
     * @param netProtocol
     * @param amount
     * @param exchangeRate   汇率,amount的币种转换成U的汇率
     * @return 成本 单位:U
     */
    BigDecimal channelCostCalculator(Integer businessAction, Integer channelSubType, String assetName, String netProtocol,
                                     BigDecimal amount, BigDecimal exchangeRate);
}
