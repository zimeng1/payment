package com.mc.payment.third.party.api.model.vo.fireBlocks;

import lombok.Data;

import java.util.List;

/**
 * @author Marty
 * @since 2024/04/17 16:53
 */
@Data
//Create a new vault account VO
public class VaultAccountVo {

    //The ID of the account
    private String id;

    //Account Name
    private String name;

    //Account assets
    private List<VaultAssetVo> assets;

    //Optional - if true, the created account and all related transactions will not be shown on Fireblocks console
    private Boolean hiddenOnUI;

    //客户参考 ID
    //Optional - Sets a customer reference ID
    private String customerRefId;

    //保管库帐户的 autoFuel 属性
    //Optional - Sets the autoFuel property of the vault account
    private Boolean autoFuel;
}
