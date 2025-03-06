package com.mc.payment.core.service.model.req;

import com.baomidou.mybatisplus.annotation.TableField;
import com.mc.payment.common.base.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

@Data
@Schema(title = "资产配置保存参数实体")
public class AssetConfigSaveReq extends BaseReq {
    private static final long serialVersionUID = -6200304811032124199L;

    @Schema(title = "资产名称,[如:BTC]")
    @NotBlank(message = "[资产名称]不能为空")
    @Length(max = 20, message = "[资产名称]长度不能超过20")
    protected String assetName;

    @Schema(title = "手续费资产名称,[如:BTC]")
    @NotBlank(message = "[手续费资产名称]不能为空")
    @Length(max = 20, message = "[手续费资产名称]长度不能超过20")
    protected String feeAssetName;

    @Schema(title = "资产网络")
    @NotBlank(message = "[资产网络]不能为空")
    @Length(max = 50, message = "[资产网络]长度不能超过50")
    protected String assetNet;

    @Schema(title = "网络协议")
    @TableField("net_protocol")
    @NotBlank(message = "[网络协议]不能为空")
    @Length(max = 20, message = "[网络协议]长度不能超过20")
    private String netProtocol;

    @Schema(title = "合约地址")
//    @NotBlank(message = "[合约地址]不能为空")
    @Length(max = 255, message = "[合约地址]长度不能超过255")
    private String tokenAddress;

    @Schema(title = "资产状态,[0:禁用,1:激活]")
    @NotNull(message = "[资产状态]必须为0或1,0:禁用,1:激活")
    @Range(min = 0, max = 1, message = "[资产状态]必须为0或1,0:禁用,1:激活")
    protected Integer status;

    @Schema(title = "最小入金金额")
    @NotNull(message = "[最小入金金额]不能为空")
    @DecimalMin(value = "0.0", message = "[最小入金金额]不能小于0")
    private BigDecimal minDepositAmount;

    @Schema(title = "最小出金金额")
    @NotNull(message = "[最小出金金额]不能为空")
    @DecimalMin(value = "0.0", message = "[最小出金金额]不能小于0")
    private BigDecimal minWithdrawalAmount;

    @Schema(title = "手续费兜底值-默认预估费(单位为本币种)")
    @NotNull(message = "[手续费兜底值]不能为空")
    @DecimalMin(value = "0.0", message = "[手续费兜底值]不能小于0")
    private BigDecimal defaultEstimateFee;

    @Schema(title = "资产类型,[0:加密货币,1:法币]")
    @NotNull(message = "[资产类型]必须为0或1,0:加密货币,1:法币")
    @Range(min = 0, max = 1, message = "[资产类型]必须为0或1,0:加密货币,1:法币")
    private Integer assetType;

    public AssetConfigSaveReq() {
    }

    public AssetConfigSaveReq(String assetName, String assetNet, Integer status, String tokenAddress, String netProtocol) {
        this.assetName = assetName;
        this.assetNet = assetNet;
        this.status = status;
        this.netProtocol = netProtocol;
    }
}
