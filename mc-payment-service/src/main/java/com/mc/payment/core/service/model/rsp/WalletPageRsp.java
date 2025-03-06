package com.mc.payment.core.service.model.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mc.payment.core.service.serializer.BigDecimalSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author conor
 * @since 2024/2/2 15:33:01
 */
@Data
@Schema(title = "钱包-分页返回实体")
public class WalletPageRsp implements Serializable {

    private static final long serialVersionUID = 5866636858663481290L;
    @Schema(title = "id")
    protected String id;

    @Schema(title = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date createTime;

    @Schema(title = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date updateTime;

    @Schema(title = "账号名称")
    private String accountName;

    @Schema(title = "通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    private Integer channelSubType;

    @Schema(title = "账户签约的商户的ID")
    private String merchantId;

    @Schema(title = "账号id")
    private String accountId;

    @Schema(title = "资产名称,[如:BTC]")
    private String assetName;

    @Schema(title = "网络协议")
    private String netProtocol;

    @Schema(title = "通道资产名称")
    private String channelAssetName;

    @Schema(title = "账户地址")
    private String walletAddress;

    @JsonFormat(pattern = "#.####", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "资产协议金额(原钱包余额)")
    private BigDecimal balance;

    @JsonFormat(pattern = "#.####", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "资产总额：展示对应资产钱包下，所有协议余额的总额")
    private BigDecimal sumBalance = BigDecimal.ZERO;

    @JsonFormat(pattern = "#.####", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "冻结金额")
    private BigDecimal freezeAmount;

    @Schema(title = "私钥")
    private String privateKey;

    @Schema(title = "备注")
    private String remark;

    @Schema(title = "外部系统钱包id", description = "比如fireblocks创建去钱包返回的钱包id就存这儿")
    private String externalId;

    @Schema(title = "状态,[0:可用,1:锁定,2:冻结]")
    protected Integer status;

    @JsonFormat(pattern = "#.####", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "余额(U)")
    private BigDecimal balanceeToU;

    @Schema(title = "更新者")
    protected String updateBy;

}
