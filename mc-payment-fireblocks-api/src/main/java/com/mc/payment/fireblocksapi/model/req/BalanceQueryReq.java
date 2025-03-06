package com.mc.payment.fireblocksapi.model.req;

import com.mc.payment.common.base.BaseReq;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
//Query Balance")
public class BalanceQueryReq extends BaseReq {
    private static final long serialVersionUID = 0L;
    //the address of the ERC20 contract")
    @NotBlank(message = "[contractAddress] is null")
    private String coinContractAddress;

    //the address of the account")
    @NotBlank(message = "[accountAddress ] is null")
    private String accountAddress;
}
