package com.mc.payment.third.party.api.model.vo.fireBlocks.nested;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Marty
 * @since 2024/06/04 11:33
 */
@Data
//@ApiModel("AssetOnChainVo")
public class AssetOnChainVo {
//    @ApiModelProperty("The asset symbol")
    private String symbol;

//    @ApiModelProperty("The asset name")
    private String name;

//    @ApiModelProperty("The asset address")
    private String address;

//    @ApiModelProperty("Number of decimals")
    private BigDecimal decimals;

//    @ApiModelProperty("The asset standard")
    private String standard;
}
