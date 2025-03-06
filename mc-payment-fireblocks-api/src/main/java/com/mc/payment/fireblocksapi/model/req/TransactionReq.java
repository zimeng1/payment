package com.mc.payment.fireblocksapi.model.req;

import com.mc.payment.common.base.BaseReq;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
//Query Transaction")
public class TransactionReq extends BaseReq {
    private static final long serialVersionUID = 0L;
    //the hash of the transaction")
    @NotBlank(message = "[txId] is null")
    private String txId;
}
