package com.mc.payment.core.service.model.req.channel;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.lang.Validator;
import com.mc.payment.core.service.model.enums.AssetTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

@Data
public class ChannelAssetConfigUpdateReq {

    @Schema(title = "id")
    @NotBlank(message = "[id]不能为空")
    private String id;

    @Schema(title = "资产类型,[0:加密货币,1:法币]", description = "不可修改,需要与原数据一致")
    @NotNull(message = "[资产类型]不能为空")
    @Range(min = 0, max = 1, message = "[资产类型]必须为[0:加密货币,1:法币]")
    private Integer assetType;

    @Schema(title = "通道资产名称")
    @NotBlank(message = "[通道资产名称]不能为空")
    @Length(max = 20, message = "[通道资产名称]长度不能超过20")
    private String channelAssetName;


    @Schema(title = "通道资产网络协议/支付类型")
    @NotBlank(message = "[通道资产网络协议/支付类型]不能为空,实在没有请填:/")
    @Length(max = 20, message = "[通道资产网络协议/支付类型]长度不能超过20")
    private String channelNetProtocol;


    @Schema(title = "最小入金金额", description = "单位:当前币种")
    @NotNull(message = "[最小入金金额]不能为空")
    @DecimalMin(value = "0.0", message = "[最小入金金额]不能小于0")
    private BigDecimal minDepositAmount;

    @Schema(title = "最小出金金额", description = "单位:当前币种")
    @NotNull(message = "[最小出金金额]不能为空")
    @DecimalMin(value = "0.0", message = "[最小出金金额]不能小于0")
    private BigDecimal minWithdrawalAmount;


    @Schema(title = "最大入金金额", description = "单位:当前币种")
    @NotNull(message = "[最大入金金额]不能为空")
    @DecimalMin(value = "0.0", message = "[最大入金金额]不能小于0")
    private BigDecimal maxDepositAmount;


    @Schema(title = "最大出金金额", description = "单位:当前币种")
    @NotNull(message = "[最大出金金额]不能为空")
    @DecimalMin(value = "0.0", message = "[最大出金金额]不能小于0")
    private BigDecimal maxWithdrawalAmount;


    @Schema(title = "合约地址")
    @Length(max = 255, message = "[合约地址]长度不能超过255")
    private String tokenAddress;


    @Schema(title = "哈希查询地址(Testnet)")
    @Length(max = 255, message = "[哈希查询地址(Testnet)]长度不能超过255")
    private String testHashUrl;


    @Schema(title = "哈希查询地址(Mainnet)")
    @Length(max = 255, message = "[哈希查询地址(Mainnet)]长度不能超过255")
    private String mainHashUrl;

    @Schema(title = "手续费币种,[如:BTC]")
    @NotBlank(message = "[手续费币种]不能为空")
    @Length(max = 20, message = "[手续费币种]长度不能超过20")
    private String feeAssetName;

    @Schema(title = "默认预估费/手续费兜底值,[单位:手续费币种]")
    private BigDecimal defaultEstimateFee;

    @Schema(title = "资产状态,[0:禁用,1:激活]")
    @NotNull(message = "[通道资产状态]不能为空")
    @Range(min = 0, max = 1, message = "[通道资产状态]必须为[0:禁用,1:激活]")
    private Integer status;

    public void validate() {
        // 最小入金金额不能大于最大入金金额 金额等于0时,不校验
        if (this.minDepositAmount.compareTo(BigDecimal.ZERO) != 0
                && this.maxDepositAmount.compareTo(BigDecimal.ZERO) != 0
                && this.minDepositAmount.compareTo(this.maxDepositAmount) > 0) {
            throw new ValidateException("[最小入金金额]不能大于[最大入金金额]");
        }
        // 最小出金金额不能大于最大出金金额 金额等于0时,不校验
        if (this.minWithdrawalAmount.compareTo(BigDecimal.ZERO) != 0
                && this.maxWithdrawalAmount.compareTo(BigDecimal.ZERO) != 0
                && this.minWithdrawalAmount.compareTo(this.maxWithdrawalAmount) > 0) {
            throw new ValidateException("[最小出金金额]不能大于[最大出金金额]");
        }
        if (this.assetType == AssetTypeEnum.CRYPTO_CURRENCY.getCode()) {
            // 加密货币校验
            Validator.validateNotEmpty(this.tokenAddress, "[合约地址]不能为空");
            Validator.validateNotEmpty(this.testHashUrl, "[哈希查询地址(Testnet)]不能为空");
            Validator.validateNotEmpty(this.mainHashUrl, "[哈希查询地址(Mainnet)]不能为空");
            Validator.validateNotNull(this.defaultEstimateFee, "[手续费兜底值]不能为空");
            // 大于等于零
            if (this.defaultEstimateFee.compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidateException("[手续费兜底值]必须大于等于0");
            }
        } else if (this.assetType == AssetTypeEnum.FIAT_CURRENCY.getCode()) {
            Validator.validateEmpty(this.tokenAddress, "[资产类型]为法币时,[合约地址]不能有值");
            Validator.validateEmpty(this.testHashUrl, "[资产类型]为法币时,[哈希查询地址(Testnet)]不能有值");
            Validator.validateEmpty(this.mainHashUrl, "[资产类型]为法币时,[哈希查询地址(Mainnet)]不能有值");
            Validator.validateNull(this.defaultEstimateFee, "[资产类型]为法币时,[手续费兜底值]不能有值");
        }
    }
}
