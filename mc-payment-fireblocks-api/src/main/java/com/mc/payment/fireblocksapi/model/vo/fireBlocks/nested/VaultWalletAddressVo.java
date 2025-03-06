package com.mc.payment.fireblocksapi.model.vo.fireBlocks.nested;

import lombok.Data;

/**
 * @author Marty
 * @since 2024/04/17 17:40
 */
@Data
//the addresses for a given vault account and asset.
public class VaultWalletAddressVo {

    //assetId 资产id
    private String assetId;

    //addresses
    private String address;

    //description
    private String description;

    //tag
    private String tag;

    //type
    private String type;

    //a customer reference ID
    private String customerRefId;

    //addressFormat: SEGWIT LEGACY BASE PAYMENT
    private String addressFormat;

    //legacyAddress
    private String legacyAddress;

    //enterpriseAddress
    private String enterpriseAddress;

    //bip44AddressIndex
    private Integer bip44AddressIndex;

    //userDefined
    private Boolean userDefined;
}
