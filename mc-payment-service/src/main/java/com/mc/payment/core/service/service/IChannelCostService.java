package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.ChannelCostEntity;
import com.mc.payment.core.service.model.req.ChannelCostPageReq;
import com.mc.payment.core.service.model.rsp.BestFeeRsp;
import com.mc.payment.core.service.model.rsp.ChannelCostPageRsp;

import java.math.BigDecimal;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author conor
 * @since 2024-01-31 18:46:14
 */
public interface IChannelCostService extends IService<ChannelCostEntity> {
    BasePageRsp<ChannelCostPageRsp> page(ChannelCostPageReq req);

    RetResult<Boolean> remove(String id);

    /**
     * 最低手续费计算器
     *
     * @param merchantId 商户id
     * @param assetId    资产id
     * @param recordType 出入金记录类型
     * @param amount     金额
     * @return
     */
    @Deprecated
    BestFeeRsp minFeeCalculator(String merchantId, String assetId, Integer recordType, BigDecimal amount);

    /**
     * 获取通道费,按rate进行转换
     *
     * @param assetId
     * @param channelId
     * @param businessAction
     * @param amount
     * @param rate           需要转换的汇率, 如果1就是U
     * @return
     */
    BigDecimal getCostByParam(String assetId, String channelId, Integer businessAction, BigDecimal amount, BigDecimal rate);
}
