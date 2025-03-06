package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mc.payment.api.model.rsp.QueryAssetRsp;
import com.mc.payment.core.service.base.BaseEntity;
import com.mc.payment.core.service.model.dto.AssetConfigExcelDto;
import com.mc.payment.core.service.model.dto.AssetDto;
import com.mc.payment.core.service.model.enums.StatusEnum;
import com.mc.payment.core.service.model.req.AssetConfigSaveReq;
import com.mc.payment.core.service.model.req.AssetConfigUpdateReq;
import com.mc.payment.core.service.serializer.BigDecimalSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <p>
 * 资产配置表
 * </p>
 *
 * @author conor
 * @since 2024-01-30 17:17:25
 */
@Getter
@Setter
@TableName("mcp_asset_config")
@Schema(title = "AssetConfigEntity对象", description = "资产配置表")
public class AssetConfigEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(title = "资产类型,[0:加密货币,1:法币]")
    @TableField("asset_type")
    private Integer assetType;

    @Schema(title = "资产名称,[如:BTC]")
    @TableField("asset_name")
    private String assetName;

    @Schema(title = "资产网络")
    @TableField("asset_net")
    private String assetNet;

    @Schema(title = "网络协议")
    @TableField("net_protocol")
    private String netProtocol;

    @Schema(title = "合约地址/Token地址/第三方支付通道的资产账号标识(ofapay中的scode)")
    @TableField("token_address")
    private String tokenAddress;

    @Schema(title = "资产状态,[0:禁用,1:激活]")
    @TableField("status")
    private Integer status;

    @JsonFormat(pattern = "#.##########", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "最小入金金额,单位U")
    @TableField("min_deposit_amount")
    private BigDecimal minDepositAmount;

    @JsonFormat(pattern = "#.##########", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "最小出金金额,单位U")
    @TableField("min_withdrawal_amount")
    private BigDecimal minWithdrawalAmount;

    @JsonFormat(pattern = "#.##########", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "最大入金金额")
    @TableField("max_deposit_amount")
    private BigDecimal maxDepositAmount;

    @JsonFormat(pattern = "#.##########", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "最大出金金额")
    @TableField("max_withdrawal_amount")
    private BigDecimal maxWithdrawalAmount;

    @Schema(title = "预估费(单位为本币种)")
    @TableField("estimate_fee")
    private BigDecimal estimateFee;

    @Schema(title = "未转换汇率预估费,单位:费率币种")
    @TableField("un_estimate_fee")
    private BigDecimal unEstimateFee;

    @Schema(title = "默认预估费(单位为本币种)")
    @TableField("default_estimate_fee")
    private BigDecimal defaultEstimateFee;


    @Schema(title = "手续费的通道资产名称,[如:USDT 出金就需用到 ETH 的手续费]")
    @TableField("fee_asset_name")
    private String feeAssetName;

    @Schema(title = "Hash跳转url")
    @TableField("hash_url")
    private String hashUrl;


    public static AssetConfigEntity valueOf(AssetConfigExcelDto assetConfigExcelDto) {
        AssetConfigEntity assetConfigEntity = new AssetConfigEntity();
        assetConfigEntity.setAssetName(assetConfigExcelDto.getAssetName());
        assetConfigEntity.setAssetNet(assetConfigExcelDto.getAssetNet());
        assetConfigEntity.setNetProtocol(assetConfigExcelDto.getNetProtocol());
        assetConfigEntity.setTokenAddress(assetConfigExcelDto.getTokenAddress());
        assetConfigEntity.setMinDepositAmount(assetConfigExcelDto.getMinDepositAmount());
        assetConfigEntity.setMinWithdrawalAmount(assetConfigExcelDto.getMinWithdrawalAmount());
        assetConfigEntity.setFeeAssetName(assetConfigExcelDto.getFeeAssetName());
        assetConfigEntity.setStatus(1);
        // todo 考虑页面维护默认预估费 预估费则定时任务定期更新
        assetConfigEntity.setEstimateFee(BigDecimal.ZERO);
        assetConfigEntity.setUnEstimateFee(BigDecimal.ZERO);
        assetConfigEntity.setDefaultEstimateFee(BigDecimal.ZERO);
        return assetConfigEntity;
    }

    //========


    @Schema(title = "资产状态-描述")
    public String getStatusDesc() {
        return StatusEnum.getEnumDesc(status);
    }

    //===================
    public static AssetConfigEntity valueOf(AssetConfigSaveReq req) {
        AssetConfigEntity entity = new AssetConfigEntity();
        entity.setAssetName(req.getAssetName());
        entity.setAssetNet(req.getAssetNet());
        entity.setTokenAddress(req.getTokenAddress());
        entity.setStatus(req.getStatus());
        entity.setNetProtocol(req.getNetProtocol());
        entity.setMinDepositAmount(req.getMinDepositAmount());
        entity.setMinWithdrawalAmount(req.getMinWithdrawalAmount());
        entity.setFeeAssetName(req.getFeeAssetName());
        entity.setDefaultEstimateFee(req.getDefaultEstimateFee());
        entity.setAssetType(req.getAssetType());
//        entity.setStatus(1);
        return entity;
    }

    public static AssetConfigEntity valueOf(AssetConfigUpdateReq req) {
        AssetConfigEntity entity = new AssetConfigEntity();
        entity.setId(req.getId());
        entity.setAssetNet(req.getAssetNet());
        entity.setAssetName(req.getAssetName());
        entity.setTokenAddress(req.getTokenAddress());
        entity.setStatus(req.getStatus());
        entity.setNetProtocol(req.getNetProtocol());
        entity.setMinDepositAmount(req.getMinDepositAmount());
        entity.setMinWithdrawalAmount(req.getMinWithdrawalAmount());
        entity.setFeeAssetName(req.getFeeAssetName());
        entity.setDefaultEstimateFee(req.getDefaultEstimateFee());
        entity.setAssetType(req.getAssetType());
//        entity.setStatus(1);
        return entity;
    }

    public QueryAssetRsp convert() {
        QueryAssetRsp rsp = new QueryAssetRsp();
        rsp.setAssetType(this.assetType);
        rsp.setAssetName(this.assetName);
        rsp.setNetProtocol(this.netProtocol);
        rsp.setAssetNet(this.assetNet);
        rsp.setMinDepositAmount(this.minDepositAmount.stripTrailingZeros().toPlainString());
        rsp.setMinWithdrawalAmount(this.minWithdrawalAmount.stripTrailingZeros().toPlainString());
        rsp.setMaxDepositAmount(this.maxDepositAmount.stripTrailingZeros().toPlainString());
        rsp.setMaxWithdrawalAmount(this.maxWithdrawalAmount.stripTrailingZeros().toPlainString());
        return rsp;
    }

    public AssetDto convertAssetDto() {
        AssetDto assetDto = new AssetDto();
        assetDto.setAssetName(this.assetName);
        assetDto.setAssetNet(this.assetNet);
        assetDto.setNetProtocol(this.netProtocol);
        return assetDto;
    }
}
