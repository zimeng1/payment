package com.mc.payment.fireblocksapi.model.req.fireBlocks;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Marty
 * @since 2024/04/15 17:29
 */

@Data
//OneTimeAddress")
public class OneTimeAddressReq{

    //目标的地址
    @NotNull(message = "[address] is null")
    //The address of the oneTimeAddress.")
    private TransactionValReq address;

    //标签
    //The tag of the oneTimeAddress.")
    private TransactionValReq tag;
}
