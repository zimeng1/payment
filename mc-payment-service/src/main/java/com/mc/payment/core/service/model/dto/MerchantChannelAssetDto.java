package com.mc.payment.core.service.model.dto;

import com.mc.payment.core.service.entity.MerchantChannelAssetEntity;
import com.mc.payment.core.service.model.enums.AssetTypeEnum;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

@Data
public class MerchantChannelAssetDto {

    @Schema(title = "通道资产配置id")
    @NotBlank(message = "通道资产配置id不能为空")
    private String channelAssetId;

    @Schema(title = "资产类型,[0:加密货币,1:法币]")
    @NotNull(message = "资产类型不能为空")
    private Integer assetType;

    /**
     * 通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]
     */
    @Schema(title = "通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    @NotNull(message = "通道子类型不能为空")
    private Integer channelSubType;

    /**
     * 资产名称,[如:BTC]
     */
    @Schema(title = "资产名称,[如:BTC]")
    @NotBlank(message = "资产名称不能为空")
    private String assetName;

    /**
     * 网络协议
     */
    @Schema(title = "网络协议")
    @NotBlank(message = "网络协议不能为空")
    private String netProtocol;

    /**
     * 是否启用告警,[0:否,1:是]
     */
    @Schema(title = "是否启用告警,[0:否,1:是]")
    @NotNull(message = "是否启用告警不能为空")
    private Integer alarmStatus;

    /**
     * 备付金告警值
     */
    @Schema(title = "备付金告警值")
    @NotNull(message = "备付金告警值不能为空")
    private BigDecimal reserveAlarmValue;

    /**
     * 是否入金可用,[0:否,1:是]
     */
    @Schema(title = "是否入金可用,[0:否,1:是]")
    @NotNull(message = "是否入金可用不能为空")
    private Integer depositStatus;


    /**
     * 是否出金可用,[0:否,1:是]
     */
    @Schema(title = "是否出金可用,[0:否,1:是]")
    @NotNull(message = "是否出金可用不能为空")
    private Integer withdrawalStatus;

    /**
     * 是否自动生成钱包,[0:否,1:是]
     */
    @Schema(title = "是否自动生成钱包,[0:否,1:是]")
    @NotNull(message = "是否自动生成钱包不能为空")
    private Integer generateWalletStatus;

    /**
     * 生成钱包小于等于阈值
     */
    @Schema(title = "生成钱包小于等于阈值")
    @Range(min = 0, max = 20, message = "生成钱包小于等于阈值范围[0-20]")
    @NotNull(message = "生成钱包小于等于阈值不能为空")
    private Integer generateWalletLeQuantity;
    /**
     * 生成钱包数量
     */
    @Schema(title = "生成钱包数量")
    @Range(min = 0, max = 20, message = "生成钱包数量范围[0-20]")
    @NotNull(message = "生成钱包数量不能为空")
    private Integer generateWalletQuantity;

    public static MerchantChannelAssetDto convert(MerchantChannelAssetEntity entity) {
        MerchantChannelAssetDto merchantChannelAssetDto = new MerchantChannelAssetDto();
        merchantChannelAssetDto.setChannelAssetId(entity.getChannelAssetId());
        merchantChannelAssetDto.setAssetType(entity.getAssetType());
        merchantChannelAssetDto.setChannelSubType(entity.getChannelSubType());
        merchantChannelAssetDto.setAssetName(entity.getAssetName());
        merchantChannelAssetDto.setNetProtocol(entity.getNetProtocol());
        merchantChannelAssetDto.setAlarmStatus(entity.getAlarmStatus());
        merchantChannelAssetDto.setReserveAlarmValue(entity.getReserveAlarmValue());
        merchantChannelAssetDto.setDepositStatus(entity.getDepositStatus());
        merchantChannelAssetDto.setWithdrawalStatus(entity.getWithdrawalStatus());
        merchantChannelAssetDto.setGenerateWalletStatus(entity.getGenerateWalletStatus());
        merchantChannelAssetDto.setGenerateWalletLeQuantity(entity.getGenerateWalletLeQuantity());
        merchantChannelAssetDto.setGenerateWalletQuantity(entity.getGenerateWalletQuantity());
        return merchantChannelAssetDto;
    }

    @Schema(title = "是否启用告警-描述")
    public String getAlarmStatusDesc() {
        return StatusEnum.getEnumDesc(alarmStatus);
    }

    @Schema(title = "是否入金可用-描述")
    public String getDepositStatusDesc() {
        return StatusEnum.getEnumDesc(depositStatus);
    }

    @Schema(title = "是否自动生成钱包-描述")
    public String getGenerateWalletStatusDesc() {
        return StatusEnum.getEnumDesc(generateWalletStatus);
    }

    @Schema(title = "是否出金可用-描述")
    public String getWithdrawalStatusDesc() {
        return StatusEnum.getEnumDesc(withdrawalStatus);
    }

    @Schema(title = "资产类型-描述")
    public String getAssetTypeDesc() {
        return AssetTypeEnum.getEnumDesc(assetType);
    }

    @Schema(title = "通道子类型-描述")
    public String getChannelSubTypeDesc() {
        return ChannelSubTypeEnum.getEnumDesc(channelSubType);
    }
}
