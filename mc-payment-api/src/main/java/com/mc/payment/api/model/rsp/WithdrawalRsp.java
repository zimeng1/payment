package com.mc.payment.api.model.rsp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawalRsp {

    //公共参数
    @Schema(title = "商户跟踪id")
    private String trackingId;

    //法币
    @Schema(title = "重定向的支付页面地址",description = "如果有，请跳转到该地址进行出金/提款")
    private String redirectPageUrl;

    @Schema(title = "交易备注",description = "在交易中加入一些额外的数据或标识,如订单号等,接口会原样返回")
    private String remark;

    //虚拟币
    @Schema(title = "预估Gas费--作废")
    private BigDecimal gasFee;

    @Schema(title = "通道费--作废")
    private BigDecimal channelFee;

    @Schema(title = "出金来源地址")
    private String walletAddress;

}
