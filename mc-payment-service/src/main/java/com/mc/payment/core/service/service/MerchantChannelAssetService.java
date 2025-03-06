package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.core.service.entity.MerchantChannelAssetEntity;
import com.mc.payment.core.service.model.dto.*;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;

import java.util.List;

/**
 * @author Conor
 * @description 针对表【mcp_merchant_channel_asset(商户支付通道资产配置)】的数据库操作Service
 * @createDate 2024-09-25 15:26:04
 */
public interface MerchantChannelAssetService extends IService<MerchantChannelAssetEntity> {
    List<MerchantChannelAssetDto> queryByMerchantId(String merchantId);

    boolean updateByMerchantId(List<MerchantChannelAssetDto> dto, String merchantId);

    List<MerchantAssetDto> queryAsset(String merchantId, Integer assetType);

    List<MerchantAssetDetailDto> queryAssetDetail(String merchantId, Integer assetType, Integer channelSubType,
                                                  Integer generateWalletStatus);

    List<AssetDto> queryAssetList(String merchantId, Integer assetType, boolean isDeposit);

    MerchantAssetDto getAssetConfigOne(String merchantId, Integer assetType, String assetName, String netProtocol);

    /**
     * 判断商户是否已经配置了该资产名称
     *
     * @param merchantId
     * @param assetType
     * @param assetName
     * @return
     */
    boolean exists(String merchantId, Integer assetType, String assetName);

    /**
     * 禁用资产
     * <p>
     * 参数不能全为null
     *
     * @param merchantId     商户id 传null表示不限制
     * @param channelSubType 通道子类型 传null表示不限制
     * @param assetType      资产类型 传null表示不限制
     * @param assetName      资产名称 传null表示不限制
     */
    void removeAsset(String merchantId, Integer channelSubType, Integer assetType, String assetName,
                     String netProtocol);

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
    ChannelSubTypeEnum choosePaymentChannel(String merchantId, int assetType, String assetName,
                                            String netProtocol, boolean isDeposit);


    /**
     * 查询商户资产自动生成钱包的配置
     *
     * @param assetType
     * @param channelSubType
     * @param generateWalletStatus
     * @return
     */
    List<MerchantGenerateWalletAssetDto> queryMerchantGenerateWalletAsset(Integer assetType, Integer channelSubType, Integer generateWalletStatus);
}
