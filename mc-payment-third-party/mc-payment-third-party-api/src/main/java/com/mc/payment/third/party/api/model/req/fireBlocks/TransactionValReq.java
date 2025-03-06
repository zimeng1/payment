package com.mc.payment.third.party.api.model.req.fireBlocks;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Marty
 * @since 2024/04/15 17:29
 */

@Data
//OneTimeAddress-val")
public class TransactionValReq {

    //目标的地址值
    @NotBlank(message = "[The value of the address] is null")
    //The value of the address.")
    private String value;

}
