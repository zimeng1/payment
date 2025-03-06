package com.mc.payment.core.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mc.payment.core.service.entity.MerchantChannelAssetEntity;
import com.mc.payment.core.service.model.dto.MerchantAssetDetailDto;
import com.mc.payment.core.service.model.dto.MerchantAssetDto;
import com.mc.payment.core.service.model.dto.MerchantGenerateWalletAssetDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Conor
 * @description 针对表【mcp_merchant_channel_asset(商户支付通道资产配置)】的数据库操作Mapper
 * @createDate 2024-09-25 15:26:04
 * @Entity com.mc.payment.core.service.entity.MerchantChannelConfigEntity
 */
public interface MerchantChannelAssetMapper extends BaseMapper<MerchantChannelAssetEntity> {

    List<MerchantAssetDto> queryAsset(@Param("merchantId") String merchantId, @Param("assetType") Integer assetType);

    /**
     * 查询商户资产详细信息
     *
     * @param merchantId
     * @param assetType            传null表示不限制
     * @param channelSubType       传null表示不限制
     * @param generateWalletStatus 传null表示不限制
     * @return
     */
    List<MerchantAssetDetailDto> queryAssetDetail(@Param("merchantId") String merchantId,
                                                  @Param("assetType") Integer assetType,
                                                  @Param("channelSubType") Integer channelSubType,
                                                  @Param("generateWalletStatus") Integer generateWalletStatus);

    /**
     * 查询商户资产自动生成钱包的配置
     *
     * @param assetType
     * @param channelSubType
     * @param generateWalletStatus
     * @return
     */
    List<MerchantGenerateWalletAssetDto> queryMerchantGenerateWalletAsset(@Param("assetType") Integer assetType,
                                                                          @Param("channelSubType") Integer channelSubType,
                                                                          @Param("generateWalletStatus") Integer generateWalletStatus);

    MerchantAssetDto getAssetConfigOne(@Param("merchantId") String merchantId, @Param("assetType") Integer assetType,
                                       @Param("assetName") String assetName, @Param("netProtocol") String netProtocol);

    int countByName(@Param("merchantId") String merchantId, @Param("assetType") Integer assetType, @Param("assetName") String assetName);
}




