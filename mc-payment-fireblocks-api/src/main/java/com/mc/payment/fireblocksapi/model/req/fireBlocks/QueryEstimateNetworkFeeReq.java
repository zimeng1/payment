package com.mc.payment.fireblocksapi.model.req.fireBlocks;

import com.mc.payment.common.base.BaseReq;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Marty
 * @since 2024/4/24 19:28
 */
@EqualsAndHashCode(callSuper = true)
@Data
//estimate_network_fee")
public class QueryEstimateNetworkFeeReq extends BaseReq {

    //资产ID
    //The ID of the asset")
    @NotBlank(message = "[Account assetId] is null")
    private String assetId;
}
