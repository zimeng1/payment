package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class QueryCostAssetListReq {
    @Schema(title = "通道子类型")
    @NotNull(message = "[通道子类型]不能为空")
    @Range(min = 1, max = 6, message = "[通道子类型]必须为[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    private Integer channelSubType;

    @Schema(title = "业务动作,[0:入金,1:出金]")
    @NotNull(message = "[业务动作]不能为空")
    @Range(min = 0, max = 1, message = "[业务动作]必须为[0:入金,1:出金]")
    private Integer businessAction;
}
