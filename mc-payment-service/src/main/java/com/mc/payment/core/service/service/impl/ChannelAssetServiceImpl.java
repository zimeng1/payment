package com.mc.payment.core.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.ChannelAssetEntity;
import com.mc.payment.core.service.mapper.ChannelAssetMapper;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.req.ChannelAssetPageReq;
import com.mc.payment.core.service.model.req.ChannelAssetSaveReq;
import com.mc.payment.core.service.service.IChannelAssetService;
import com.mc.payment.fireblocksapi.FireBlocksAPI;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.AssetTypeVo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author conor
 * @since 2024-04-24 14:43:44
 */
@Deprecated
@RequiredArgsConstructor
@Service
public class ChannelAssetServiceImpl extends ServiceImpl<ChannelAssetMapper, ChannelAssetEntity> implements IChannelAssetService {
//    private final IWalletService walletService;

    private final FireBlocksAPI fireBlocksAPI;


    @Override
    public BasePageRsp<ChannelAssetEntity> page(ChannelAssetPageReq req) {
        Page<ChannelAssetEntity> page = new Page<>(req.getCurrent(), req.getSize());
        LambdaQueryWrapper<ChannelAssetEntity> query = Wrappers.lambdaQuery(ChannelAssetEntity.class)
                .orderByDesc(ChannelAssetEntity::getCreateTime);
        if (StrUtil.isNotBlank(req.getId())) {
            query.likeRight(BaseNoLogicalDeleteEntity::getId, req.getId());
        }
        if (req.getChannelSubType() != null) {
            query.eq(ChannelAssetEntity::getChannelSubType, req.getChannelSubType());
        }
        if (StrUtil.isNotBlank(req.getChannelAssetName())) {
            query.eq(ChannelAssetEntity::getChannelAssetName, req.getChannelAssetName());
        }
        if (StrUtil.isNotBlank(req.getAssetNet())) {
            query.eq(ChannelAssetEntity::getAssetNet, req.getAssetNet());
        }
        if (CollUtil.isNotEmpty(req.getNetProtocols())) {
            query.in(ChannelAssetEntity::getNetProtocol, req.getNetProtocols());
        }
        baseMapper.selectPage(page, query);
        return BasePageRsp.valueOf(page);
    }


    @Override
    public ChannelAssetEntity getOne(ChannelSubTypeEnum channelSubTypeEnum, String assetName, String netProtocol) {
        return this.getOne(channelSubTypeEnum.getCode(), assetName, netProtocol);
    }

    @Override
    public ChannelAssetEntity getOne(Integer channelSubType, String assetName, String netProtocol) {
        return this.getOne(Wrappers.lambdaQuery(ChannelAssetEntity.class)
                .eq(ChannelAssetEntity::getChannelSubType, channelSubType)
                .eq(ChannelAssetEntity::getAssetName, assetName)
                .eq(ChannelAssetEntity::getNetProtocol, netProtocol));
    }

    @Override
    public ChannelAssetEntity getOne(Integer channelSubType, String channelAssetName) {
        return this.getOne(Wrappers.lambdaQuery(ChannelAssetEntity.class)
                .eq(ChannelAssetEntity::getChannelSubType, channelSubType)
                .eq(ChannelAssetEntity::getChannelAssetName, channelAssetName));
    }


    @Override
    public List<ChannelAssetEntity> getList(Integer channelSubType) {
        return this.list(Wrappers.lambdaQuery(ChannelAssetEntity.class).eq(ChannelAssetEntity::getChannelSubType,
                channelSubType));
    }

    @Override
    public RetResult<Boolean> removeById(String id) {
        ChannelAssetEntity entity = this.getById(id);
        if (entity == null) {
            return RetResult.error("该通道资产不存在");
        }
//        long count = walletService.count(entity.getChannelSubType(), entity.getChannelAssetName()); todo 暂时去掉
//         商户钱包功能做好这里才好加
        long count = 1;
        if (count > 0) {
            return RetResult.error("该通道资产下存在钱包，不可删除");
        }
        return RetResult.data(super.removeById(id));
    }

    @Override
    public RetResult<String> save(ChannelAssetSaveReq req) {
        ChannelAssetEntity entity = ChannelAssetEntity.valueOf(req);
        long count = this.count(Wrappers.lambdaQuery(ChannelAssetEntity.class)
                .eq(ChannelAssetEntity::getChannelSubType, req.getChannelSubType())
                .eq(ChannelAssetEntity::getChannelAssetName, req.getChannelAssetName()));
        if (count > 0) {
            return RetResult.error("保存失败,该通道下已存在名称为" + req.getChannelAssetName() + "的通道资产");
        }
        count = this.count(Wrappers.lambdaQuery(ChannelAssetEntity.class)
                .eq(ChannelAssetEntity::getChannelSubType, req.getChannelSubType())
                .eq(ChannelAssetEntity::getAssetName, req.getAssetName())
                .eq(ChannelAssetEntity::getNetProtocol, req.getNetProtocol()));
        if (count > 0) {
            return RetResult.error("保存失败,该通道下已存在名称为" + req.getAssetName() + ",协议为" + req.getNetProtocol() + "的资产");
        }
        boolean save = this.save(entity);
        if (!save) {
            return RetResult.error("保存失败");
        }
        return RetResult.data(entity.getId());
    }

    @Override
    public List<ChannelAssetEntity> getListByTypeList(List<Integer> channelSubTypeList) {
        return this.list(Wrappers.lambdaQuery(ChannelAssetEntity.class).in(ChannelAssetEntity::getChannelSubType,
                channelSubTypeList));
    }

    @Override
    public List<ChannelAssetEntity> getChannelAssetNameList(ChannelAssetPageReq req) {
        LambdaQueryWrapper<ChannelAssetEntity> query = Wrappers.lambdaQuery(ChannelAssetEntity.class);
        if (req.getChannelSubType() != null) {
            query.eq(ChannelAssetEntity::getChannelSubType, req.getChannelSubType());
        }
        if (StringUtils.isNotBlank(req.getId())) {
            query.likeRight(ChannelAssetEntity::getId, req.getId());
        }
        query.select(ChannelAssetEntity::getChannelAssetName, ChannelAssetEntity::getNetProtocol);
        query.groupBy(ChannelAssetEntity::getChannelAssetName, ChannelAssetEntity::getNetProtocol);
        return this.list(query);
    }

    @Override
    public List<ChannelAssetEntity> getFireBlocksSupportedAssetList() {
        //todo 待维护到表里, 防止fireblocks刚好挂了, 有个保险.
        RetResult<List<AssetTypeVo>> listRetResult = fireBlocksAPI.querySupportedAssets();
        if (listRetResult.isSuccess()) {
            List<AssetTypeVo> assetTypeVoList = listRetResult.getData();
            // 将assetTypeVoList 转成ChannelAssetEntity的集合
            return assetTypeVoList.stream().map(ChannelAssetEntity::valueOf).toList();
        }
        return null;
    }

    /**
     * 根据通道资产名称找资产名称
     *
     * @param channelSubType
     * @param assetName
     * @param channelAssetName
     * @param netProtocol
     * @return
     */
    @Override
    public String getAssetNameByChannelAssetName(Integer channelSubType, String assetName, String channelAssetName,
                                                 String netProtocol) {
        if (!assetName.equals(channelAssetName)) {
            LambdaQueryWrapper<ChannelAssetEntity> query = Wrappers.lambdaQuery(ChannelAssetEntity.class)
                    .eq(ChannelAssetEntity::getChannelSubType, channelSubType)
                    .eq(ChannelAssetEntity::getChannelAssetName, channelAssetName)
                    .eq(ChannelAssetEntity::getNetProtocol, netProtocol);
            ChannelAssetEntity feeChannelAsset = this.getOne(query);
            // 如果没找到, 就还是原来的手续费通道资产名称, 如果找到了, 就用找到的资产名称
            if (feeChannelAsset != null) {
                return feeChannelAsset.getAssetName();
            } else {
                return channelAssetName;
            }
        }
        return assetName;
    }

    @Override
    public List<String> getAssetNameList(List<Integer> typeList) {
        List<ChannelAssetEntity> list = lambdaQuery().in(ChannelAssetEntity::getChannelSubType, typeList).list();
        List<String> assetNameList = new ArrayList<>();
        if (CollUtil.isNotEmpty(list)) {
            assetNameList = list.stream().map(ChannelAssetEntity::getAssetName).distinct().collect(Collectors.toList());
        }
        return assetNameList;
    }

}
