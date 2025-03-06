package com.mc.payment.third.party.api.model.req.fireBlocks;

import com.mc.payment.common.base.BaseReq;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Create a new wallet
 * @author Marty
 * @since 2024/04/13 15:43
 */
@EqualsAndHashCode(callSuper = true)
@Data
//Create a new wallet")
public class CreateWalletReq extends BaseReq {

    //保管库帐户 ID
    //The ID of the vault account to return, or 'default' for the default vault account")
    @NotBlank(message = "[vaultAccountId] is null")
    private String vaultAccountId;

    //资产ID
    //The ID of the asset")
    @NotBlank(message = "[Account assetId] is null")
    private String assetId;

    //可选 - 创建 EOS 钱包时，帐户名称。如果未提供，将生成一个随机名称
    //Optional - when creating an EOS wallet, the account name. If not provided, a random name will be generated")
    private String eosAccountName;

}
