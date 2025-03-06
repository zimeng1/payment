package com.mc.payment.third.party.api.model.req.fireBlocks;

import com.mc.payment.common.base.BaseReq;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Marty
 * @since 2024/6/11 15:01
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ResendWebhooksReq extends BaseReq {

    //fireblocks 交易 ID
    @NotBlank(message = "[txId] is null")
    private String txId;
}
