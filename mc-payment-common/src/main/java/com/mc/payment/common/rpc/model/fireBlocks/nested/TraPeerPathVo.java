package com.mc.payment.common.rpc.model.fireBlocks.nested;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Marty
 * @since 2024/04/17 11:21
 */
@Data
public class TraPeerPathVo implements Serializable {
    // VAULT_ACCOUNT, EXCHANGE_ACCOUNT, INTERNAL_WALLET, EXTERNAL_WALLET, ONE_TIME_ADDRESS, NETWORK_CONNECTION, FIAT_ACCOUNT, GAS_STATION, UNKNOWN
    private String type;

    // The ID of the account to return. Can return as null if the related transaction fails due to a connectivity error.
    private String id;

    // The name of the account.
    private String name;

    // The specific account or wallet.
    private String subType;


}
