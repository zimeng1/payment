package com.mc.payment.api.model.rsp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 入金响应
 */
@Data
public class DepositRsp {

    @Schema(title = "商户跟踪id")
    private String trackingId;

    @Schema(title = "重定向的支付页面地址", description = "当请求参数中skipPage=1时,此字段的值为上游支付页面地址,否则为本系统的收银页面地址")
    private String redirectPageUrl;

    @Schema(title = "钱包地址", description = "当请求参数中skipPage=1时,此字段有意义,为提供给用户加密货币入金的钱包地址")
    private String walletAddress;

    @Schema(title = "失效时间戳,精确毫秒")
    private Long expireTimestamp;

    @Schema(title = "交易备注", description = "在交易中加入一些额外的数据或标识,如订单号等,接口会原样返回")
    private String remark;
}
