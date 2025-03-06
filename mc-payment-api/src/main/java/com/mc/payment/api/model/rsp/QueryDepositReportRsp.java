package com.mc.payment.api.model.rsp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
public class QueryDepositReportRsp {

    @Schema(title = "商户跟踪id")
    private String trackingId;

    @Schema(title = "币种")
    private String assetName;

    @Schema(title = "金额")
    private BigDecimal amount;

    @Schema(title = "实际入金金额")
    private BigDecimal accumulatedAmount;

    @Schema(title = "手续费")
    private BigDecimal gasFee;

    @Schema(title = "汇率")
    private BigDecimal rate;

    @Schema(title = "存款状态,[0:待入金,1:部分入金,2:完全入金,3:撤销入金,4:请求失败,5:待审核,6:审核不通过]")
    private Integer depositStatus;

    @Schema(title = "支付状态,[1未确认,2确认中,3已确认,4已取消,5未支付,6交易成功,7交易失败]")
    private Integer payStatus;

    @Schema(title = "支付平台订单号")
    private String orderNo;

    @Schema(title = "存款时间")
    protected String depositTime;
}
