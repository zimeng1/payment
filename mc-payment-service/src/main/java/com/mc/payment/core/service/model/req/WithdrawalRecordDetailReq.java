package com.mc.payment.core.service.model.req;

import com.mc.payment.core.service.base.BasePageRsp;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * @author taoliu
 * @date 2024-04-24 16:48
 */
@Getter
@Setter
public class WithdrawalRecordDetailReq extends BasePageRsp {

    @Schema(title = "出金明细id")
    private String id;

    @Schema(title = "状态")
    private String status;

    @Schema(title = "出金资产")
    private String assetName;

    @Schema(title = "出金商户")
    private String merchantName;

    @Schema(title = "来源地址")
    private String sourceAddress;

    @Schema(title = "关联商户跟踪id")
    private String trackingId;

    @Schema(title = "目标地址")
    private String destinationAddress;

    @Schema(title = "出金时间")
    private List<String> withdrawalTimeList;
}
