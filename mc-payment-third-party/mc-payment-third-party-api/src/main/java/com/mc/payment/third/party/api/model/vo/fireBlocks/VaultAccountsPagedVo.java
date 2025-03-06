package com.mc.payment.third.party.api.model.vo.fireBlocks;

import lombok.Data;

import java.util.List;

/**
 * @author Marty
 * @since 2024/04/17 17:31
 */
@Data
//Gets all vault accounts in your workspace.
public class VaultAccountsPagedVo {

    //all account
    private List<VaultAccountVo> accounts;

    //previousUrl
    private String previousUrl;

    //nextUrl
    private String nextUrl;
}
