package com.mc.payment.third.party.api.model.req.fireBlocks;

import com.mc.payment.common.base.BaseReq;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * Register an asset
 * Register a new asset to a workspace and return the newly created asset's details. Currently supported chains are:
 * <p>
 * EVM based chains
 * Stellar
 * Algorand
 * TRON
 * NEAR
 *
 * @author Marty
 * @since 2024/06/04 11:21
 */
@EqualsAndHashCode(callSuper = true)
@Data
//@ApiModel("Create a new transaction")
public class RegisterNewAssetReq extends BaseReq {

    //幂等性唯一key,24小时内同一个key的操作结果一致
//    @ApiModelProperty("idempotencyKey, 幂等性唯一key")
    private String idempotencyKey;

    //区块链原生资产ID
    @NotBlank(message = "[blockchainId] is null")
//    @ApiModelProperty("Native asset ID of the blockchain ")
    private String blockchainId;

    /**
     * 资产地址
     * For EVM based chains this should be the token contract address.
     * For Stellar (XLM) this should be the issuer address.
     * For Algorand (ALGO) this should be the asset ID.
     * For TRON (TRX) this should be the token contract address.
     * For NEAR this will be the token address.
     */
    @NotBlank(message = "[address] is null")
//    @ApiModelProperty("Asset address.")
    private String address;

    //仅适用于 Stellar，需要资产代码。
//    @ApiModelProperty("Required for Stellar only, asset code is expected.")
    private String symbol;

}
