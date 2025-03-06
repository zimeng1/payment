package com.mc.payment.gateway.model.rsp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GatewayWithdrawalRsp {
    /**
     * 入金交易操作的唯一标识
     */
    private String transactionId;
    /**
     * 交易状态,-1:失败,0:处理中,1:成功,2：余额不足
     */
    private int status;

    @Schema(title = "重定向的支付页面地址", description = "如果有，请跳转到该地址进行出金/提款")
    private String redirectPageUrl;

    // 交易渠道的交易ID
    private String channelTransactionId;

    // 手续费资产名称
    //private String feeAssetName;
}
