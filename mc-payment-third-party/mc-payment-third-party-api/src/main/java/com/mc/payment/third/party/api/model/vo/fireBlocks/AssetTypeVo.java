package com.mc.payment.third.party.api.model.vo.fireBlocks;

import lombok.Data;
/**
 * @author Marty
 * @since 2024/4/20 10:50
 */
@Data
//Asset Type 
public class AssetTypeVo {
    //Asset id
    private String id;
    //Asset type
    private String type;
    //Asset contractAddress
    private String contractAddress;
    //Asset nativeAsset
    private String nativeAsset;
    //Asset nativeAsset
    private Integer decimals;

}
