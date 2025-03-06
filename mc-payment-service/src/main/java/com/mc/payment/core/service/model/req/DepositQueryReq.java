package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Conor
 * @since 2024/4/29 下午3:09
 */
@Data
public class DepositQueryReq {

    @Schema(title = "跟踪id集合", description = "申请方提供唯一跟踪ID以查询处理结果，长度限制为50个字符,20个跟踪id")
    private List<String> trackingIds;


}
