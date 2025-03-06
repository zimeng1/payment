package com.mc.payment.api.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class QueryMerchantSnapshotReq extends PageReq {

    @Schema(title = "开始时间")
    private String startTime;

    @Schema(title = "结束时间")
    private String endTime;

}
