package com.mc.payment.core.service.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Conor
 * @since 2024/4/22 下午6:23
 */
@Data
@Schema(title = "入金事件明细实体")
public class CryptoDepositEventDetailVo {
    @Schema(title = "交易标识")
    private String txHash;

    @Schema(title = "资产名称,[如:BTC]")
    private String assetName;

    @Schema(title = "网络协议")
    private String netProtocol;

    @Schema(title = "来源地址")
    private String sourceAddress;

    @Schema(title = "目标地址")
    private String destinationAddress;

    @Schema(title = "金额")
    private BigDecimal amount;
//
//    @Schema(title = "网络费")
//    private BigDecimal networkFee;
//
//    @Schema(title = "服务费")
//    private BigDecimal serviceFee;

    @Schema(title = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date createTime;

}
