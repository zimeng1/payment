package com.mc.payment.core.service.model.rsp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Conor
 * @since 2024/4/13 上午11:33
 */
@Data
public class WithdrawalAuditRsp {

    @Schema(title = "跟踪id,申请方提供唯一跟踪ID以查询处理结果，长度限制为50个字符")
    private String trackingId;

    @Schema(title = "重定向的支付页面地址",description = "如果有，请跳转到该地址进行出金/提款")
    private String redirectPageUrl;
}
