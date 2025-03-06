package com.mc.payment.third.party.api.model.req.fireBlocks;

import com.mc.payment.common.base.BaseReq;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * List addresses (Paginated)
 * @author Marty
 * @since 2024/04/13 15:48
 */
@EqualsAndHashCode(callSuper = true)
@Data
//List addresses (Paginated)")
public class QueryAssetAddressesReq extends BaseReq {

    //保管库帐户 ID
    //The ID of the vault account to return, or 'default' for the default vault account")
    @NotBlank(message = "[vaultAccountId] is null")
    private String vaultAccountId;

    //资产ID
    //The ID of the asset")
    @NotBlank(message = "[Account assetId] is null")
    private String assetId;

    //Optional - before")
    private String before;

    //Optional - after")
    private String after;

    //分页
    //Optional - default to 200")
    private BigDecimal limit;

}
