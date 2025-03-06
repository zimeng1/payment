package com.mc.payment.core.service.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@Schema(description = "钱包余额变更事件参数实体类")
public class WalletBalanceEventVo extends BaseWebhookEventVo{
    @Schema(title = "本次余额")
    private BigDecimal currentBalance;

    @Schema(title = "本次冻结金额")
    private BigDecimal currentFreezeAmount;

    @Schema(title = "上一次余额")
    private BigDecimal previousBalance;

    @Schema(title = "上一次冻结金额")
    private BigDecimal previousFreezeAmount;

    @Schema(title = "钱包地址")
    private String walletAddress;

    @Schema(title = "资产名称")
    private String assetName;

    @Schema(title = "网络协议")
    private String netProtocol;

    @Schema(title = "钱包更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date walletUpdateTime;

   // int shardIndex = 0;
}
