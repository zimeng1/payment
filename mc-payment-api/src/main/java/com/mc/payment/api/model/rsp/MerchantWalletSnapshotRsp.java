package com.mc.payment.api.model.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class MerchantWalletSnapshotRsp {

    @Schema(title = "主键id")
    private String id;

    @Schema(title = "账户签约的商户的ID")
    private String merchantId;

    @Schema(title = "资产类型,[0:加密货币,1:法币]")
    private Integer assetType;

    @Schema(title = "资产类型/币种")
    private String assetName;

    @Schema(title = "网络协议/支付网络")
    private String netProtocol;

    @Schema(title = "用途类型,[0:入金,1:出金]")
    private Integer purposeType;

    @Schema(title = "账户地址")
    private String walletAddress;

    @Schema(title = "余额")
    private BigDecimal balance;

    @Schema(title = "创建者")
    protected String createBy;

    @Schema(title = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Schema(title = "更新者")
    private String updateBy;

    @Schema(title = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
