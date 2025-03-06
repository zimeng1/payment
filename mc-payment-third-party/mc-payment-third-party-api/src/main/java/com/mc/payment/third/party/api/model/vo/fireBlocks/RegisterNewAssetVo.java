package com.mc.payment.third.party.api.model.vo.fireBlocks;

import com.mc.payment.third.party.api.model.vo.fireBlocks.nested.AssetMetadataVo;
import com.mc.payment.third.party.api.model.vo.fireBlocks.nested.AssetOnChainVo;
import lombok.Data;

/**
 * @author Marty
 * @since 2024/06/04 11:26
 */
@Data
//@ApiModel("RegisterNewAssetVo")
public class RegisterNewAssetVo {
//    @ApiModelProperty("legacyId")
    private String legacyId;

//    @ApiModelProperty("assetClass")
    private String assetClass;

//    @ApiModelProperty("onchain")
    private AssetOnChainVo onchain;

//    @ApiModelProperty("metadata")
    private AssetMetadataVo metadata;

}
