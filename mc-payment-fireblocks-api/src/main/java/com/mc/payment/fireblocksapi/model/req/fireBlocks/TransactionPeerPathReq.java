package com.mc.payment.fireblocksapi.model.req.fireBlocks;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Marty
 * @since 2024/04/15 17:18
 */
@Data
//source")
public class TransactionPeerPathReq {

    // 来源的类型
    @NotBlank(message = "[type] is null")
    //type")
    private String type;

    // 来源的子类型
    //subType")
    private String subType;

    // 来源的账户id
    //id")
    private String id;

    // 来源的账户名称
    //name")
    private String name;

    //钱包id，可不传
    //walletId")
    private String walletId;
    
}
