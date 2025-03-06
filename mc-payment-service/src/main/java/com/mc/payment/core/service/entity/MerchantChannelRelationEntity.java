package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.model.req.MerchantChannelSaveReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <p>
 * 商户通道关系表
 * </p>
 *
 * @author conor
 * @since 2024-02-19 16:39:16
 */
@Getter
@Setter
@TableName("mcp_merchant_channel_relation")
@Schema(title = "MerchantChannelRelationEntity对象", description = "商户通道关系表")
public class MerchantChannelRelationEntity extends BaseNoLogicalDeleteEntity {

    private static final long serialVersionUID = 1L;

    @Schema(title = "商户id")
    @TableField("merchant_id")
    private String merchantId;

    @Schema(title = "通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    @TableField("channel_sub_type")
    private Integer channelSubType;

    @Schema(title = "商户的储备金, 单位: 如果备付金类型=0, 单位为:U, 如果备付金类型=1, 单位为:被币种单位")
    @TableField("reserve_ratio")
    private BigDecimal reserveRatio;

    @Schema(title = "备付金类型[0:全部, 1:部分币种]")
    @TableField("reserve_fund_type")
    private Integer reserveFundType;

    @Schema(title = "资产名称,[如:BTC]")
    @TableField("asset_name")
    private String assetName;

    public static MerchantChannelRelationEntity valueOf(String merchantId, MerchantChannelSaveReq req) {
        MerchantChannelRelationEntity entity = new MerchantChannelRelationEntity();
        entity.setMerchantId(merchantId);
        entity.setChannelSubType(req.getChannelSubType());
        entity.setReserveRatio(req.getReserveRatio());
        entity.setReserveFundType(req.getReserveFundType());
        entity.setAssetName(req.getAssetName());
        return entity;
    }

}
