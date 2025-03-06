package com.mc.payment.core.service.model.rsp;

import com.mc.payment.core.service.entity.MerchantEntity;
import com.mc.payment.core.service.model.dto.MerchantChannelAssetDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author taoliu
 * @date 2024-04-25 15:14
 */
@Data
@Schema(title = "商户-查询详情返回实体")
public class MerchantGetByIdRsp extends MerchantEntity {
    private static final long serialVersionUID = -1560313411823133299L;

    @Schema(title = "通道名称集合")
    private String channelNameDescs;

    @Schema(title = "业务范围名称集合")
    private String businessScopeDescs;

    @Schema(title = "通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    private String channelSubType;

    @Schema(title = "商户的储备金")
    private String reserveRatio;

    @Schema(title = "支持通道")
    @NotNull(message = "[支持通道]不能为空")
    private List<Integer> channelList;

    @Schema(title = "通道支持的资产")
    @NotNull(message = "[通道支持的资产]不能为空")
    private List<MerchantChannelAssetDto> channelAssetList;
}
