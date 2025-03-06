package com.mc.payment.core.service.model.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mc.payment.core.service.model.enums.AssetTypeEnum;
import com.mc.payment.core.service.model.enums.StatusEnum;
import com.mc.payment.core.service.serializer.BigDecimalSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Marty
 * @since 2024/5/9 15:25
 */
@Data
@Schema(title = "资产配置-分页返回实体")
public class AssetConfigPageRsp implements Serializable {

    private static final long serialVersionUID = 3472992263032355385L;

    @Schema(title = "id")
    protected String id;

    @Schema(title = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date updateTime;

    @Schema(title = "资产名称,[如:BTC]")
    private String assetName;

    @Schema(title = "资产网络")
    private String assetNet;

    @Schema(title = "网络协议")
    private String netProtocol;

    @Schema(title = "合约地址")
    private String tokenAddress;

    @Schema(title = "资产状态,[0:禁用,1:激活]")
    private Integer status;

    @JsonFormat(pattern = "#.##########", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "最小入金金额")
    private BigDecimal minDepositAmount;

    @JsonFormat(pattern = "#.##########", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "最小出金金额")
    private BigDecimal minWithdrawalAmount;

    @Schema(title = "手续费资产名称,[如:BTC]")
    protected String feeAssetName;

    @JsonFormat(pattern = "#.##########", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "手续费兜底值-默认预估费(单位为本币种)")
    private BigDecimal defaultEstimateFee;

    @Schema(title = "操作账号")
    protected String updateBy;

    @Schema(title = "资产类型,[0:加密货币,1:法币]")
    private Integer assetType;

    @Schema(title = "资产状态-描述")
    public String getStatusDesc() {
        return StatusEnum.getEnumDesc(status);
    }

    @Schema(title = "资产类型-描述")
    public String getAssetTypeDesc() {
        return AssetTypeEnum.getEnumDesc(assetType);
    }

}
