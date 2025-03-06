package com.mc.payment.fireblocksapi.model.vo.fireBlocks;

import com.mc.payment.fireblocksapi.model.vo.fireBlocks.nested.VaultWalletAddressVo;
import lombok.Data;

import java.util.List;

/**
 * @author Marty
 * @since 2024/04/17 17:38
 */
@Data
//Returns a paginated response of the addresses for a given vault account and asset.
public class PaginatedAddressVo {

    //addresses list
    private List<VaultWalletAddressVo> addresses;

}
