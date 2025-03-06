package com.mc.payment.core.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.ChannelCostEntity;
import com.mc.payment.core.service.mapper.ChannelCostMapper;
import com.mc.payment.core.service.model.enums.CostLimitEnum;
import com.mc.payment.core.service.model.enums.CostTypeEnum;
import com.mc.payment.core.service.model.enums.RoundMethodEnum;
import com.mc.payment.core.service.model.req.ChannelCostPageReq;
import com.mc.payment.core.service.model.rsp.BestFeeRsp;
import com.mc.payment.core.service.model.rsp.ChannelCostPageRsp;
import com.mc.payment.core.service.service.IChannelCostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author conor
 * @since 2024-01-31 18:46:14
 */
@Service
public class ChannelCostServiceImpl extends ServiceImpl<ChannelCostMapper, ChannelCostEntity> implements IChannelCostService {
    @Autowired
    public ChannelCostServiceImpl(ChannelCostMapper channelCostMapper) {
        this.baseMapper = channelCostMapper;
    }

    @Override
    public BasePageRsp<ChannelCostPageRsp> page(ChannelCostPageReq req) {
        Page<ChannelCostPageRsp> page = new Page<>(req.getCurrent(), req.getSize());
        baseMapper.selectPage(page, req);
        return BasePageRsp.valueOf(page);
    }


    @Override
    public RetResult<Boolean> remove(String id) {
        ChannelCostEntity entity = getById(id);
        if (entity == null) {
            return RetResult.error("该数据不存在");
        }
        return RetResult.data(this.removeById(id));
    }


    @Override
    public BestFeeRsp minFeeCalculator(String merchantId, String assetId, Integer recordType, BigDecimal amount) {
        List<ChannelCostEntity> list = baseMapper.list(merchantId, assetId, recordType);
        if (CollUtil.isEmpty(list)) {
            log.error("minFeeCalculator() fail not found ChannelCostEntity");
            return null;
        }
        BigDecimal minFee = null;
        String channelCostId = null;
        for (int i = 0, listSize = list.size(); i < listSize; i++) {
            ChannelCostEntity entity = list.get(i);
            BigDecimal cost = entity.getCost(); // 成本
            BigDecimal minCost = entity.getMinCostLimit(); // 最小成本
            BigDecimal maxCost = entity.getMaxCostLimit(); // 最大成本
            BigDecimal rate = entity.getRate(); // 费率
            Integer costType = entity.getCostType(); // 成本类型,[0:按笔收费/U,1:按费率收费/%]
            Integer roundMethod = entity.getRoundMethod(); // 取整方式,[0:向上取整,1:向下取整,2:四舍五入,9:无]
//            Integer costLimit = entity.getCostLimit(); // 成本限额,[0:最低/U,1:最高/U]
            String costLimitOption = entity.getCostLimitOption();

            BigDecimal temp;
            if (CostTypeEnum.ITEM_0.getCode() == costType) {
                // 按笔收费/U
                temp = cost;
            } else {
                // 按费率收费/%  此时成本字段是费率,单位%,例如:cost=2 费率为 2%
                // 最终计算结果保留5位小数
                RoundingMode roundingMode = RoundingMode.HALF_UP;
                if (RoundMethodEnum.ITEM_0.getCode() == roundMethod) {
                    roundingMode = RoundingMode.UP;
                } else if (RoundMethodEnum.ITEM_1.getCode() == roundMethod) {
                    roundingMode = RoundingMode.DOWN;
                }

                temp = NumberUtil.mul(amount, rate);
                temp = NumberUtil.div(temp, 100, 5, roundingMode);
                if (costLimitOption.contains(CostLimitEnum.ITEM_0.getCode())) {
                    // 最低/U 时 计算结果若比成本小 则取最小成本
                    if (minCost != null && temp.compareTo(minCost) < 0) {
                        temp = minCost;
                    }
                }
                if (costLimitOption.contains(CostLimitEnum.ITEM_1.getCode())) {
                    // 最高/U 时 计算结果若比成本大 则取最大成本
                    if (maxCost != null && temp.compareTo(maxCost) > 0) {
                        temp = maxCost;
                    }
                }

//                if (CostLimitEnum.ITEM_0.getCode() == costLimit) {
//                    // 最低/U 时 计算结果若比成本小 则取成本
//                    if (temp.compareTo(cost) < 0) {
//                        temp = cost;
//                    }
//                } else {
//                    // 最高/U 时 计算结果若比成本大 则取成本
//                    if (temp.compareTo(cost) > 0) {
//                        temp = cost;
//                    }
//                }
            }
            if (i == 0) {
                minFee = temp;
                channelCostId = entity.getId();
            }
            // 计算最小值
            if (minFee.compareTo(temp) > 0) {
                minFee = temp;
                channelCostId = entity.getId();
            }
        }
        return new BestFeeRsp(channelCostId, minFee);
    }


    /**
     * 根据条件查询出数据, 然后根据数据的类型计算成本
     *
     * @param assetId        @description 待废弃
     * @param channelId
     * @param businessAction
     * @param amount
     * @return
     */
    @Override
    public BigDecimal getCostByParam(String assetId, String channelId, Integer businessAction, BigDecimal amount, BigDecimal rate) {
//        // 获取通道费(ps:二期目前是以通道为规则, 之后可能以币种. 所以目前二期数据库最多只能存在4条数据)
//        ChannelCostEntity one = this.getOne(Wrappers.lambdaQuery(ChannelCostEntity.class).eq(ChannelCostEntity::getBusinessAction, businessAction).like(ChannelCostEntity::getChannelId, channelId));
//        if (one != null) {
//            // 计算通道费, 根据costType决定是按笔收费还是按费率收费, 然后根据roundMethod决定取整方式
//            BigDecimal channelCost = one.getCost();
//            Integer costType = one.getCostType();
//            Integer roundMethod = one.getRoundMethod();
//            RoundingMode roundingMode = roundMethod == 0 ? RoundingMode.UP : roundMethod == 1 ? RoundingMode.DOWN : RoundingMode.HALF_UP;
//            if (costType == CostTypeEnum.ITEM_0.getCode()) {
//                // 按笔收费
//                return channelCost.divide(rate, 20, roundingMode);
//            } else {
//                // 本次交易按费率收费
//                BigDecimal traRate = one.getRate();
//                BigDecimal temp = NumberUtil.mul(amount, traRate);
//                //取整方式,[0:向上取整,1:向下取整,2:四舍五入,9:无]
//                temp = NumberUtil.div(temp, 100, 20, roundingMode);
//                //判断是否小于最低值或者大于最高值
//                if (one.getMinCostLimit() != null && one.getMinCostLimit().compareTo(temp) > 0) {
//                    temp = one.getMinCostLimit();
//                }
//                if (one.getMaxCostLimit() != null && one.getMaxCostLimit().compareTo(temp) < 0) {
//                    temp = one.getMaxCostLimit();
//                }
//                return temp.divide(rate, 20, roundingMode);
//            }
//        }
        // todo
        return BigDecimal.ZERO;
    }
}
