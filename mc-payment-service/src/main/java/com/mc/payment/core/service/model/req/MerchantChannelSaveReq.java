package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

/**
 * @author Conor
 * @since 2024/4/16 下午2:14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantChannelSaveReq {
    @Schema(title = "通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    @NotNull(message = "[通道子类型]不能为空")
    @Range(min = 1, max = 6, message = "[通道子类型]必须为[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    private Integer channelSubType;

    @Schema(title = "商户的储备金, 单位: 如果备付金类型=0, 单位为:U, 如果备付金类型=1, 单位为:被币种单位")
    @NotNull(message = "[商户的储备金]不能为空")
    @Range(min = 0, message = "[商户的储备金]不能小于0")
    private BigDecimal reserveRatio;


    @Schema(title = "备付金类型[0:全部币种, 1:部分币种]")
    @NotNull(message = "[备付金类型]不能为空")
    private Integer reserveFundType;

    @Schema(title = "资产名称,[如:BTC]")
    private String assetName = "";


}
