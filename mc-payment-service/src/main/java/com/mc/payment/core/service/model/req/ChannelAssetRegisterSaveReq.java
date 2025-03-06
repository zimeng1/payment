package com.mc.payment.core.service.model.req;

import com.mc.payment.common.base.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author Marty
 * @since 2024-06-20 14:59:26
 */
@Data
@Schema(title = "注册新币种-保存参数实体")
public class ChannelAssetRegisterSaveReq extends BaseReq {

    @Schema(title = "通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    private Integer channelSubType = 1;

    @NotBlank(message = "[资产名称]不能为空")
    @Length(max = 20, message = "[资产名称]长度不能超过20")
    @Schema(title = "资产名称")
    private String assetName;

    @NotBlank(message = "[原生资产ID]不能为空")
    @Length(max = 20, message = "[原生资产ID]长度不能超过20")
    @Schema(title = "原生资产ID")
    private String blockChainId;

    @Schema(title = "合约地址/资产地址")
    @NotBlank(message = "[合约地址]不能为空")
    @Length(max = 255, message = "[合约地址]长度不能超过255")
    private String chainAddress;

    @Schema(title = "Asset symbol")
    private String symbol = "";

}
