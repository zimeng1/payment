package com.mc.payment.core.service.model.rsp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Conor
 * @since 2024/4/13 上午11:33
 */
@Getter
@Setter
public class DepositAuditRsp {

    @Schema(title = "跟踪id,申请方提供唯一跟踪ID以查询处理结果，长度限制为50个字符")
    private String trackingId;
}
