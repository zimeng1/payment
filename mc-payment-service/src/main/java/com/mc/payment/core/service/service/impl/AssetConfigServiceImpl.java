package com.mc.payment.core.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.AssetConfigEntity;
import com.mc.payment.core.service.mapper.AssetConfigMapper;
import com.mc.payment.core.service.model.enums.StatusEnum;
import com.mc.payment.core.service.model.req.AssetConfigPageReq;
import com.mc.payment.core.service.model.req.AssetConfigSaveReq;
import com.mc.payment.core.service.model.req.AssetListByParamReq;
import com.mc.payment.core.service.model.rsp.AssetConfigListRsp;
import com.mc.payment.core.service.model.rsp.AssetConfigPageRsp;
import com.mc.payment.core.service.service.IAssetConfigService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 资产配置表 服务实现类
 * </p>
 *
 * @author conor
 * @since 2024-01-30 17:17:25
 */
@Deprecated
@Service
public class AssetConfigServiceImpl extends ServiceImpl<AssetConfigMapper, AssetConfigEntity> implements IAssetConfigService {

    @Autowired
    public AssetConfigServiceImpl(AssetConfigMapper assetConfigMapper) {
        this.baseMapper = assetConfigMapper;
    }


    @Override
    public BasePageRsp<AssetConfigPageRsp> page(AssetConfigPageReq req) {
        Page<AssetConfigEntity> page = new Page<>(req.getCurrent(), req.getSize());
        LambdaQueryWrapper<AssetConfigEntity> query = Wrappers.lambdaQuery(AssetConfigEntity.class);
        if (StrUtil.isNotBlank(req.getAssetName())) {
            query.likeRight(AssetConfigEntity::getAssetName, req.getAssetName());
        }
        if (StrUtil.isNotBlank(req.getAssetNet())) {
            query.likeRight(AssetConfigEntity::getAssetNet, req.getAssetNet());
        }
        if (StrUtil.isNotBlank(req.getTokenAddress())) {
            query.likeRight(AssetConfigEntity::getTokenAddress, req.getTokenAddress());
        }
        if (StrUtil.isNotBlank(req.getNetProtocol())) {
            query.likeRight(AssetConfigEntity::getNetProtocol, req.getNetProtocol());
        }
        if (req.getStatus() != null) {
            query.eq(AssetConfigEntity::getStatus, req.getStatus());
        }
        if (req.getAssetType() != null) {
            query.eq(AssetConfigEntity::getAssetType, req.getAssetType());
        }
        if (StrUtil.isNotBlank(req.getId())) {
            query.eq(AssetConfigEntity::getId, req.getId());
        }
        query.orderByDesc(AssetConfigEntity::getCreateTime);
        baseMapper.selectPage(page, query);
        List<AssetConfigEntity> records = page.getRecords();
        List<AssetConfigPageRsp> assetConfigPageRspList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(records)) {
            assetConfigPageRspList = records.stream().map(assetConfigEntity -> { // 方法的参数
                AssetConfigPageRsp temp = new AssetConfigPageRsp();
                temp.setId(assetConfigEntity.getId());
                temp.setUpdateTime(assetConfigEntity.getUpdateTime());
                temp.setAssetName(assetConfigEntity.getAssetName());
                temp.setAssetNet(assetConfigEntity.getAssetNet());
                temp.setNetProtocol(assetConfigEntity.getNetProtocol());
                temp.setTokenAddress(assetConfigEntity.getTokenAddress());
                temp.setStatus(assetConfigEntity.getStatus());
                temp.setMinDepositAmount(assetConfigEntity.getMinDepositAmount());
                temp.setMinWithdrawalAmount(assetConfigEntity.getMinWithdrawalAmount());
                temp.setFeeAssetName(assetConfigEntity.getFeeAssetName());
                temp.setDefaultEstimateFee(assetConfigEntity.getDefaultEstimateFee());
                temp.setUpdateBy(assetConfigEntity.getUpdateBy());
                temp.setAssetType(assetConfigEntity.getAssetType());
                return temp;
            }).collect(Collectors.toList());
        }
        return new BasePageRsp<>(assetConfigPageRspList, page.getTotal(), page.getSize(), page.getCurrent(), page.getPages(), page.hasPrevious(), page.hasNext());
    }

    @Override
    public RetResult<String> save(AssetConfigSaveReq req) {
        // 资产名称唯一
        if (baseMapper.selectCount(Wrappers.lambdaQuery(AssetConfigEntity.class)
                .eq(AssetConfigEntity::getAssetName, req.getAssetName())
                .eq(AssetConfigEntity::getNetProtocol, req.getNetProtocol())) > 0) {
            return RetResult.error(req.getAssetName() + ",该资产已存在");
        }
        AssetConfigEntity entity = AssetConfigEntity.valueOf(req);
        // 创建时, 这三个字段默认为0
        entity.setEstimateFee(BigDecimal.ZERO);
        entity.setUnEstimateFee(BigDecimal.ZERO);
        this.save(entity);
        return RetResult.data(entity.getId());
    }


    /**
     * assetName + netProtocol 可确定唯一
     *
     * @param assetType
     * @param assetName
     * @param netProtocol
     * @return
     */
    @Override
    public AssetConfigEntity getOne(Integer assetType, String assetName, String netProtocol) {
        return this.getOne(Wrappers.lambdaQuery(AssetConfigEntity.class)
                .eq(AssetConfigEntity::getAssetType, assetType)
                .eq(AssetConfigEntity::getAssetName, assetName)
                .eq(AssetConfigEntity::getNetProtocol, netProtocol));
    }

    @Override
    public List<AssetConfigListRsp> assetListByParam(AssetListByParamReq req) {
        // 查询资产列表, 只根据资产名称、资产网络、网络协议查询, 也只返回这三个字段
        LambdaQueryWrapper<AssetConfigEntity> query = Wrappers.lambdaQuery(AssetConfigEntity.class);
        if (StringUtils.isNotBlank(req.getAssetName())) {
            query.likeRight(AssetConfigEntity::getAssetName, req.getAssetName());
        }
        if (StringUtils.isNotBlank(req.getAssetNet())) {
            query.likeRight(AssetConfigEntity::getAssetNet, req.getAssetNet());
        }
        if (StringUtils.isNotBlank(req.getNetProtocol())) {
            query.likeRight(AssetConfigEntity::getNetProtocol, req.getNetProtocol());
        }
        query.eq(AssetConfigEntity::getStatus, StatusEnum.ACTIVE.getCode());
        //1:assetName, 2:assetNet, 3:netProtocol
        Integer distinctType = req.getDistinctType();
        if (distinctType != null) {
            //根据distinctType 的值, 选择去重的类型, 用sw
            if (distinctType == 1) {
                query.groupBy(AssetConfigEntity::getAssetName);
                query.select(AssetConfigEntity::getAssetName);
            } else if (distinctType == 2) {
                query.groupBy(AssetConfigEntity::getAssetNet);
                query.select(AssetConfigEntity::getAssetNet);
            } else {
                query.groupBy(AssetConfigEntity::getNetProtocol);
                query.select(AssetConfigEntity::getNetProtocol);
            }
        } else {
            query.select(AssetConfigEntity::getAssetName, AssetConfigEntity::getAssetNet, AssetConfigEntity::getNetProtocol);
        }
        List<AssetConfigEntity> list = this.list(query);
        return list.stream().map(AssetConfigListRsp::valueOf).toList();
    }


}
