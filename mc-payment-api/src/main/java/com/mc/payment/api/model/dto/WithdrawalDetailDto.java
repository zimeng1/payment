package com.mc.payment.api.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * WithdrawalDetailDto
 *
 * @author GZM
 * @since 2024/10/10 下午4:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalDetailDto {
    @Schema(title = "TxHash")
    private String txHash;

    @Schema(title = "资产名称")
    private String assetName;

    @Schema(title = "网络协议")
    private String netProtocol;

    @Schema(title = "来源地址")
    private String sourceAddress;

    @Schema(title = "目标地址")
    private String destinationAddress;

    @Schema(title = "商户名称")
    private String merchantName;

    @Schema(title = "金额")
    private BigDecimal amount;

    @Schema(title = "目标地址余额")
    private BigDecimal addrBalance;

    @Schema(title = "出金明细状态 1未确认,2确认中,3已确认,4已取消,5已出金,6出金失败")
    private Integer status;
}
