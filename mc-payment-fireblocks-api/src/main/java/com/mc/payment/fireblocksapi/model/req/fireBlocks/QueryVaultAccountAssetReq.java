package com.mc.payment.fireblocksapi.model.req.fireBlocks;

import com.mc.payment.common.base.BaseReq;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Get the asset balance for a vault account
 * @author Marty
 * @since 2024/04/13 15:50
 */
@EqualsAndHashCode(callSuper = true)
@Data
//Get the asset balance for a vault account")
public class QueryVaultAccountAssetReq extends BaseReq {

    //保管库帐户 ID
    //The ID of the vault account to return, or 'default' for the default vault account")
    @NotBlank(message = "[vaultAccountId] is null")
    private String vaultAccountId;

    //资产ID
    //The ID of the asset")
    @NotBlank(message = "[Account assetId] is null")
    private String assetId;
}
