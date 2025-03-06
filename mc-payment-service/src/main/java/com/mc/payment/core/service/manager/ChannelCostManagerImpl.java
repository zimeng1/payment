package com.mc.payment.core.service.manager;

import cn.hutool.core.util.NumberUtil;
import com.mc.payment.common.constant.ExceptionTypeEnum;
import com.mc.payment.common.exception.BusinessException;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.entity.ChannelAssetConfigEntity;
import com.mc.payment.core.service.entity.ChannelCostAssetEntity;
import com.mc.payment.core.service.entity.ChannelCostEntity;
import com.mc.payment.core.service.model.dto.AssetSimpleDto;
import com.mc.payment.core.service.model.enums.CostLimitEnum;
import com.mc.payment.core.service.model.enums.CostTypeEnum;
import com.mc.payment.core.service.model.enums.RoundMethodEnum;
import com.mc.payment.core.service.model.req.ChannelCostSaveReq;
import com.mc.payment.core.service.model.req.ChannelCostUpdateReq;
import com.mc.payment.core.service.model.req.QueryCostAssetListReq;
import com.mc.payment.core.service.model.req.channel.ChannelAssetConfigListReq;
import com.mc.payment.core.service.service.ChannelAssetConfigService;
import com.mc.payment.core.service.service.ChannelCostAssetService;
import com.mc.payment.core.service.service.IChannelCostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author conor
 * @since 2024/2/29 14:55:54
 */
@RequiredArgsConstructor
@Component
public class ChannelCostManagerImpl implements ChannelCostManager {

    private final IChannelCostService channelCostService;
    private final ChannelCostAssetService channelCostAssetService;
    private final ChannelAssetConfigService channelAssetConfigService;

    /**
     * 根据id获取通道成本数据 并且查出支持的资产列表
     *
     * @param id
     * @return
     */
    @Override
    public ChannelCostEntity getById(String id) {
        ChannelCostEntity entity = channelCostService.getById(id);
        List<AssetSimpleDto> list = channelCostAssetService.queryListByCostId(id).stream().map(ChannelCostAssetEntity::convert).toList();
        entity.setAssetList(list);
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String save(ChannelCostSaveReq req) {
        req.init();
        req.validate();
        this.checkRoleName(req.getCostRuleName(), null);
        this.checkChannelAndAsset(req.getChannelSubType(), req.getBusinessAction(), req.getAssetList(), null);
        ChannelCostEntity entity = ChannelCostEntity.valueOf(req);
        channelCostService.save(entity);
        List<ChannelCostAssetEntity> channelCostAssetEntityList = req.getAssetList()
                .stream().map(assetSimpleDto ->
                        ChannelCostAssetEntity.valueOf(assetSimpleDto, entity.getId(), entity.getChannelSubType(), entity.getBusinessAction())
                ).toList();
        channelCostAssetService.saveBatch(channelCostAssetEntityList);
        return entity.getId();
    }


    /**
     * 同一个通道子类型和业务动作下,资产不能重复
     *
     * @param channelSubType
     * @param businessAction
     * @param assetList
     */
    private void checkChannelAndAsset(Integer channelSubType, Integer businessAction, List<AssetSimpleDto> assetList,
                                      String costId) {
        for (AssetSimpleDto assetSimpleDto : assetList) {
            if (channelCostAssetService.lambdaQuery()
                    .eq(ChannelCostAssetEntity::getChannelSubType, channelSubType)
                    .eq(ChannelCostAssetEntity::getBusinessAction, businessAction)
                    .eq(ChannelCostAssetEntity::getAssetName, assetSimpleDto.getAssetName())
                    .eq(ChannelCostAssetEntity::getNetProtocol, assetSimpleDto.getNetProtocol())
                    .ne(costId != null, ChannelCostAssetEntity::getCostId, costId)
                    .exists()) {
                throw new BusinessException(ExceptionTypeEnum.NOT_EXIST,
                        assetSimpleDto.getAssetName() + "-" + assetSimpleDto.getNetProtocol() + "," +
                                "该资产已配置成本规则");
            }
        }
    }

    private void checkRoleName(String costRuleName, String costId) {
        if (channelCostService.lambdaQuery().eq(ChannelCostEntity::getCostRuleName, costRuleName)
                .ne(costId != null, BaseNoLogicalDeleteEntity::getId, costId)
                .exists()) {
            throw new BusinessException(ExceptionTypeEnum.NOT_EXIST, costRuleName + ",该成本规则名称已存在");
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(ChannelCostUpdateReq req) {
        if (!channelCostService.lambdaQuery().eq(BaseNoLogicalDeleteEntity::getId, req.getId()).exists()) {
            throw new BusinessException(ExceptionTypeEnum.NOT_EXIST, "该数据不存在");
        }
        req.init();
        req.validate();
        this.checkRoleName(req.getCostRuleName(), req.getId());

        // 校验新加的资产是否已经在别的地方配置了成本规则
        this.checkChannelAndAsset(req.getChannelSubType(), req.getBusinessAction(), req.getAssetList(), req.getId());
        if (!channelCostService.updateById(ChannelCostEntity.valueOf(req))) {
            return false;
        }
        channelCostAssetService.lambdaUpdate().eq(ChannelCostAssetEntity::getCostId, req.getId()).remove();
        List<ChannelCostAssetEntity> channelCostAssetEntityList = req.getAssetList()
                .stream().map(assetSimpleDto ->
                        ChannelCostAssetEntity.valueOf(assetSimpleDto, req.getId(), req.getChannelSubType(), req.getBusinessAction())
                ).toList();
        channelCostAssetService.saveBatch(channelCostAssetEntityList);
        return true;
    }

    /**
     * 获取可选的资产列表,已经配置了成本规则的资产不会再次出现
     *
     * @param req
     * @return
     */
    @Override
    public List<AssetSimpleDto> queryAssetList(QueryCostAssetListReq req) {
        ChannelAssetConfigListReq configListReq = new ChannelAssetConfigListReq();
        configListReq.setChannelSubType(req.getChannelSubType());
        // 该通道子类型下的所有资产
        List<AssetSimpleDto> allAssetList =
                channelAssetConfigService.list(configListReq).stream().map(ChannelAssetConfigEntity::convert).toList();
        // 已经配置了成本规则的资产
        List<ChannelCostAssetEntity> configuredList = channelCostAssetService.lambdaQuery().eq(ChannelCostAssetEntity::getChannelSubType, req.getChannelSubType())
                .eq(ChannelCostAssetEntity::getBusinessAction, req.getBusinessAction())
                .list();
        // 过滤掉已经配置了成本规则的资产
        return allAssetList.stream().filter(assetSimpleDto -> configuredList.stream().noneMatch(
                channelCostAssetEntity -> channelCostAssetEntity.getAssetName().equals(assetSimpleDto.getAssetName())
                        && channelCostAssetEntity.getNetProtocol().equals(assetSimpleDto.getNetProtocol())
        )).toList();
    }

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
    @Override
    public BigDecimal channelCostCalculator(Integer businessAction, Integer channelSubType, String assetName,
                                            String netProtocol, BigDecimal amount, BigDecimal exchangeRate) {
        ChannelCostAssetEntity channelCostAssetEntity = channelCostAssetService.lambdaQuery()
                .eq(ChannelCostAssetEntity::getBusinessAction, businessAction)
                .eq(ChannelCostAssetEntity::getChannelSubType, channelSubType)
                .eq(ChannelCostAssetEntity::getAssetName, assetName)
                .eq(ChannelCostAssetEntity::getNetProtocol, netProtocol)
                .one();
        if (channelCostAssetEntity == null) {
            return BigDecimal.ZERO;
        }
        String costId = channelCostAssetEntity.getCostId();
        ChannelCostEntity channelCostEntity = channelCostService.getById(costId);
        if (channelCostEntity == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal cost = channelCostEntity.getCost(); // 成本 例如:cost=2 代表成本为2U
        BigDecimal minCost = channelCostEntity.getMinCostLimit(); // 最小成本
        BigDecimal maxCost = channelCostEntity.getMaxCostLimit(); // 最大成本
        BigDecimal rate = channelCostEntity.getRate(); // 费率 例如:rate=2 费率为 2%
        Integer costType = channelCostEntity.getCostType(); // 成本类型,[0:按笔收费/U,1:按费率收费/%]
        Integer roundMethod = channelCostEntity.getRoundMethod(); // 取整方式,[0:向上取整,1:向下取整,2:四舍五入,9:无]
        String costLimitOption = channelCostEntity.getCostLimitOption();// 成本限额,[0:最低/U,1:最高/U]
        Integer costPrecision = channelCostEntity.getCostPrecision(); // 成本精度

        BigDecimal channelCost;
        // 计算通道费, 根据costType决定是按笔收费还是按费率收费, 然后根据roundMethod决定取整方式
        if (CostTypeEnum.ITEM_0.getCode() == costType) {
            // 按笔收费/U
            channelCost = cost;
        } else {
            // 按费率收费/%  此时成本字段是费率,单位%,例如:cost=2 费率为 2%
            // 最终计算结果保留5位小数
            RoundingMode roundingMode = RoundingMode.HALF_UP;
            if (RoundMethodEnum.ITEM_0.getCode() == roundMethod) {
                roundingMode = RoundingMode.UP;
            } else if (RoundMethodEnum.ITEM_1.getCode() == roundMethod) {
                roundingMode = RoundingMode.DOWN;
            }
            BigDecimal temp = NumberUtil.mul(amount, exchangeRate, rate);
            temp = NumberUtil.div(temp, 100, costPrecision, roundingMode);
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
            channelCost = temp;
        }
        return channelCost;
    }
}
