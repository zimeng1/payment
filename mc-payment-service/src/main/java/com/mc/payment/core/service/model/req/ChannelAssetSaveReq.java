package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

/**
 * @author Conor
 * @since 2024/5/22 下午3:21
 */
@Data
public class ChannelAssetSaveReq {
    @Schema(title = "通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    @NotNull(message = "[通道子类型]不能为空")
    @Range(min = 1, max = 6, message = "[通道子类型]必须为[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    private Integer channelSubType;

    @Schema(title = "通道资产名称")
    @NotBlank(message = "[通道资产名称]不能为空")
    @Length(max = 20, message = "[通道资产名称]长度不能超过20")
    private String channelAssetName;

    @Schema(title = "资产名称")
    @NotBlank(message = "[资产名称]不能为空")
    @Length(max = 20, message = "[资产名称]长度不能超过20")
    private String assetName;

    @Schema(title = "资产网络")
    @NotBlank(message = "[资产网络]不能为空")
    @Length(max = 50, message = "[资产网络]长度不能超过50")
    private String assetNet;

    @Schema(title = "网络协议")
    @NotBlank(message = "[网络协议]不能为空")
    @Length(max = 20, message = "[网络协议]长度不能超过20")
    private String netProtocol;
}
