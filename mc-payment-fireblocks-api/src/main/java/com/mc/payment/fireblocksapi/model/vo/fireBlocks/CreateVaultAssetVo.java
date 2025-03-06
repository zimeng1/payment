package com.mc.payment.fireblocksapi.model.vo.fireBlocks;

import lombok.Data;

/**
 * @author Marty
 * @since 2024/04/17 17:24
 */
@Data
//Creates a wallet for a specific asset in a vault account.
public class CreateVaultAssetVo {

    //The ID of the asset
    private String id;

    //address
    private String address;

    //legacyAddress
    private String legacyAddress;

    //enterpriseAddress
    private String enterpriseAddress;

    //tag
    private String tag;

    //eosAccountName
    private String eosAccountName;

    //status
    private String status;

    //activationTxId
    private String activationTxId;
}
