package com.mc.payment.core.service.model.rsp.merchant;

import com.mc.payment.core.service.model.dto.MerchantChannelAssetDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class MerchantConfigGetByIdRsp {

    @Schema(title = "商户id")
    private String id;

    @Schema(title = "商户名称")
    private String name;

    @Schema(title = "Access Key")
    private String accessKey;

    @Schema(title = "Secret Key")
    private String secretKey;

    @Schema(title = "告警邮箱,[英文逗号隔开]")
    private String alarmEmail;

    @Schema(title = "ip白名单,[英文逗号隔开]")
    private String ipWhitelist;

    @Schema(title = "支持通道")
    private List<Integer> channelList;

    @Schema(title = "通道支持的资产")
    private List<MerchantChannelAssetDto> channelAssetList;

}
