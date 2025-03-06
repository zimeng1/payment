package com.mc.payment.api.model.rsp;

import com.mc.payment.api.model.dto.DepositDetailDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryDepositRsp {
    @Schema(title = "商户跟踪id")
    private String trackingId;

    @Schema(title = "入金状态,[0:待入金,1:部分入金,2:完全入金,3:撤销入金,4:请求失效]", example = "1")
    private Integer status;

    @Schema(title = "资产名称,[如:BTC]")
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

    @Schema(title = "已入金金额")
    private BigDecimal accumulatedAmount;

    @Schema(title = "备注说明")
    private String remark;

    @Schema(title = "资产类型,[0:加密货币,1:法币]")
    private Integer assetType;

    @Schema(title = "通知回调地址/webhook url")
    private String webhookUrl;

    @Schema(title = "入金成功跳转页面地址")
    private String successPageUrl;

    @Schema(title = "入金业务名称, 比如商品名称/业务名称 eg: xxx报名费")
    private String businessName;

    @Schema(title = "银行代码", description = "某些币种的支付类型需要")
    private String bankCode;

    @Schema(title = "用户id")
    private String userId;

    @Schema(title = "用户ip地址")
    private String userIp;

    @Schema(title = "停留原因")
    private String stayReason;

    @Schema(title = "入金明细")
    private List<DepositDetailDto> depositDetailDtoList;

}
