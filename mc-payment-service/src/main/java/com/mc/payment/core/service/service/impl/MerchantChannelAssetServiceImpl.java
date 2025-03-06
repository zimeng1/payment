package com.mc.payment.core.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.common.constant.ExceptionTypeEnum;
import com.mc.payment.common.exception.BusinessException;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.entity.AssetBankEntity;
import com.mc.payment.core.service.entity.ChannelAssetConfigEntity;
import com.mc.payment.core.service.entity.MerchantChannelAssetEntity;
import com.mc.payment.core.service.mapper.MerchantChannelAssetMapper;
import com.mc.payment.core.service.model.dto.*;
import com.mc.payment.core.service.model.enums.AssetTypeEnum;
import com.mc.payment.core.service.model.enums.BooleanStatusEnum;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.enums.StatusEnum;
import com.mc.payment.core.service.model.req.channel.ChannelAssetConfigListReq;
import com.mc.payment.core.service.model.rsp.AssetBankDto;
import com.mc.payment.core.service.service.AssetBankService;
import com.mc.payment.core.service.service.ChannelAssetConfigService;
import com.mc.payment.core.service.service.MerchantChannelAssetService;
import com.mc.payment.core.service.service.PlatformAssetService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Conor
 * @description 针对表【mcp_merchant_channel_asset(商户支付通道资产配置)】的数据库操作Service实现
 * @createDate 2024-09-25 15:26:04
 */
@RequiredArgsConstructor
@Service
public class MerchantChannelAssetServiceImpl extends ServiceImpl<MerchantChannelAssetMapper, MerchantChannelAssetEntity>
        implements MerchantChannelAssetService {

    private final AssetBankService assetBankService;
    private final ChannelAssetConfigService channelAssetConfigService;
    private final PlatformAssetService platformAssetService;

    @Override
    public List<MerchantChannelAssetDto> queryByMerchantId(String merchantId) {
        return this.lambdaQuery().eq(MerchantChannelAssetEntity::getMerchantId, merchantId)
                .list().stream().map(MerchantChannelAssetDto::convert).toList();
    }

    @Nullable
    private static List<AssetDto> getAssetDtos(List<AssetBankEntity> bankEntityList, List<AssetDto> list) {
        if (bankEntityList.isEmpty()) {
            return list;
        }
        Map<String, List<AssetBankEntity>> map =
                bankEntityList.stream().collect(Collectors.groupingBy(assetBankEntity -> assetBankEntity.getAssetName() + assetBankEntity.getNetProtocol()));
        for (AssetDto assetDto : list) {
            List<AssetBankEntity> assetBankEntities = map.get(assetDto.getAssetName() + assetDto.getNetProtocol());
            if (CollUtil.isEmpty(assetBankEntities)) {
                continue;
            }
            List<AssetBankDto> bankDtoList = assetBankEntities.stream().map(AssetBankEntity::convertToAssetBankDto).toList();
            assetDto.setBankList(bankDtoList);
        }
        return list;
    }

    /**
     * 查询商户可用的资产配置
     * <p>
     * 用于商户的用户在出入金时选择资产,商户和收银台接了这个接口,就能控制商户可用哪些资产
     * </p>
     * 出入金申请的校验也会用到
     *
     * @param merchantId
     * @param assetType
     * @return
     */
    @Override
    public List<MerchantAssetDto> queryAsset(String merchantId, Integer assetType) {
        return baseMapper.queryAsset(merchantId, assetType);
    }

    @Override
    public List<MerchantAssetDetailDto> queryAssetDetail(String merchantId, Integer assetType, Integer channelSubType, Integer generateWalletStatus) {
        return baseMapper.queryAssetDetail(merchantId, assetType, channelSubType, generateWalletStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateByMerchantId(List<MerchantChannelAssetDto> dtoList, String merchantId) {
        if (CollUtil.isEmpty(dtoList)) {
            return true;
        }
        // 检测是否有重复的资产配置 assetName+netProtocol 不能重复
        Map<String, Long> map = dtoList.stream().collect(Collectors.groupingBy(dto ->
                dto.getAssetName() + "," + dto.getNetProtocol(), Collectors.counting()));
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            if (entry.getValue() > 1) {
                throw new ValidateException("资产配置重复:" + entry.getKey());
            }
        }
        // 删除商户的所有资产配置
        this.removeAsset(merchantId, null, null, null, null);

        ChannelAssetConfigListReq channelAssetConfigListReq = new ChannelAssetConfigListReq();
        channelAssetConfigListReq.setStatus(StatusEnum.ACTIVE.getCode());
        List<ChannelAssetConfigEntity> channelAssetConfigEntityList = channelAssetConfigService.list(channelAssetConfigListReq);
        Map<String, ChannelAssetConfigEntity> channelAssetConfigEntityMap =
                channelAssetConfigEntityList.stream().collect(Collectors.toMap(BaseNoLogicalDeleteEntity::getId, Function.identity()));

        List<MerchantChannelAssetEntity> list = new ArrayList<>();
        for (MerchantChannelAssetDto dto : dtoList) {
            ChannelAssetConfigEntity channelAssetConfigEntity = channelAssetConfigEntityMap.get(dto.getChannelAssetId());
            if (channelAssetConfigEntity == null) {
                throw new BusinessException(ExceptionTypeEnum.NOT_EXIST, "通道资产配置不存在或已禁用:" + dto.getAssetName() + "," + dto.getNetProtocol());
            }
            MerchantChannelAssetEntity entity = new MerchantChannelAssetEntity();
            entity.setChannelAssetId(dto.getChannelAssetId());
            entity.setMerchantId(merchantId);
            entity.setAssetType(channelAssetConfigEntity.getAssetType());
            entity.setChannelSubType(channelAssetConfigEntity.getChannelSubType());
            entity.setAssetName(channelAssetConfigEntity.getAssetName());
            entity.setNetProtocol(channelAssetConfigEntity.getNetProtocol());
            entity.setAlarmStatus(dto.getAlarmStatus());
            entity.setReserveAlarmValue(dto.getReserveAlarmValue());
            entity.setWithdrawalStatus(dto.getWithdrawalStatus());
            entity.setDepositStatus(dto.getDepositStatus());
            entity.setGenerateWalletStatus(dto.getGenerateWalletStatus());
            entity.setGenerateWalletLeQuantity(dto.getGenerateWalletLeQuantity());
            entity.setGenerateWalletQuantity(dto.getGenerateWalletQuantity());

            list.add(entity);
        }
        return this.saveBatch(list);
    }

    /**
     * 收银台页面查询商户可用的资产配置
     *
     * @param merchantId
     * @param assetType
     * @param isDeposit  是否是入金,true:入金,false:出金
     * @return
     */
    @Override
    public List<AssetDto> queryAssetList(String merchantId, Integer assetType, boolean isDeposit) {
        List<MerchantAssetDto> merchantAssetDtos = this.queryAsset(merchantId, assetType);
        List<AssetDto> list =
                merchantAssetDtos.stream()
                        .filter(merchantAssetDto ->
                                merchantAssetDto.getDepositStatus() == BooleanStatusEnum.ITEM_1.getCode())
                        .map(MerchantAssetDto::convertAssetDto).toList();

        if (assetType != AssetTypeEnum.FIAT_CURRENCY.getCode()) {
            return list;
        }
        List<AssetBankEntity> bankEntityList;
        if (isDeposit) {
            bankEntityList = assetBankService.getDepositBankList(null, null);
        } else {
            bankEntityList = assetBankService.getWithdrawBankList(null, null);
        }
        return getAssetDtos(bankEntityList, list);
    }

    /**
     * 查询商户可用的资产配置
     * <p>
     * 用在出入金申请的校验
     *
     * @param merchantId
     * @param assetType
     * @param assetName
     * @param netProtocol
     * @return
     */
    @Override
    public MerchantAssetDto getAssetConfigOne(String merchantId, Integer assetType, String assetName,
                                              String netProtocol) {
        return baseMapper.getAssetConfigOne(merchantId, assetType, assetName, netProtocol);
    }

    /**
     * 判断商户是否已经配置了该资产名称
     *
     * @param merchantId
     * @param assetType
     * @param assetName
     * @return
     */
    @Override
    public boolean exists(String merchantId, Integer assetType, String assetName) {
        int count = baseMapper.countByName(merchantId, assetType, assetName);
        return count > 0;
    }

    /**
     * 禁用资产
     * <p>
     * 参数不能全为null
     *
     * @param merchantId     商户id 传null表示不限制
     * @param channelSubType 通道子类型 传null表示不限制
     * @param assetType      资产类型 传null表示不限制
     * @param assetName      资产名称 传null表示不限制
     * @param netProtocol    网络协议 传null表示不限制
     */
    @Override
    public void removeAsset(String merchantId,
                            Integer channelSubType,
                            Integer assetType,
                            String assetName,
                            String netProtocol) {
        // 不可全为null
        if (merchantId == null && channelSubType == null && assetType == null && assetName == null && netProtocol == null) {
            throw new ValidateException("参数不能全为null");
        }
        this.lambdaUpdate().eq(merchantId != null, MerchantChannelAssetEntity::getMerchantId, merchantId)
                .eq(channelSubType != null, MerchantChannelAssetEntity::getChannelSubType, channelSubType)
                .eq(assetType != null, MerchantChannelAssetEntity::getAssetType, assetType)
                .eq(assetName != null, MerchantChannelAssetEntity::getAssetName, assetName)
                .eq(netProtocol != null, MerchantChannelAssetEntity::getNetProtocol, netProtocol)
                .remove();
    }

    /**
     * 支付通道选择
     *
     * @param merchantId
     * @param assetType
     * @param assetName
     * @param netProtocol
     * @param isDeposit   true:入金 false:出金 因为商户可能会配置不同的通道资产用于入金和出金
     * @return
     */
    @Override
    public ChannelSubTypeEnum choosePaymentChannel(String merchantId, int assetType, String assetName, String netProtocol, boolean isDeposit) {
        ChannelSubTypeEnum channelSubTypeEnum = ChannelSubTypeEnum.UNDECIDED;
        if (StrUtil.isBlank(netProtocol)) {
            return channelSubTypeEnum;
        }
        MerchantAssetDto merchantAssetDto = this.getAssetConfigOne(merchantId, assetType, assetName, netProtocol);
        if (merchantAssetDto == null) {
            throw new ValidateException("The assetName and netProtocol not available, please check");
        }
        if (isDeposit && merchantAssetDto.getDepositStatus() == BooleanStatusEnum.ITEM_0.getCode()) {
            throw new BusinessException(ExceptionTypeEnum.NOT_EXIST, "The assetName and netProtocol not support deposit");
        } else if (!isDeposit && merchantAssetDto.getWithdrawalStatus() == BooleanStatusEnum.ITEM_0.getCode()) {
            throw new BusinessException(ExceptionTypeEnum.NOT_EXIST, "The assetName and netProtocol not support withdrawal");
        }
        return ChannelSubTypeEnum.getEnum(merchantAssetDto.getChannelSubType());
    }

    /**
     * 查询商户资产自动生成钱包的配置
     *
     * @param assetType
     * @param channelSubType
     * @param generateWalletStatus
     * @return
     */
    @Override
    public List<MerchantGenerateWalletAssetDto> queryMerchantGenerateWalletAsset(Integer assetType, Integer channelSubType, Integer generateWalletStatus) {
        return baseMapper.queryMerchantGenerateWalletAsset(assetType, channelSubType, generateWalletStatus);
    }
}




