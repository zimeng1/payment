package com.mc.payment.core.service.model.rsp;

import com.mc.payment.core.service.entity.ChannelEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author conor
 * @since 2024/2/1 11:11:18
 */
@Data
@Schema(title = "通道配置-分页返回实体")
public class ChannelPageRsp extends ChannelEntity {
    private static final long serialVersionUID = -1560313421823133299L;

    @Schema(title = "资产名称,[如:BTC]")
    private String assetName;

    @Schema(title = "业务通道成本规则")
    private String costRuleName;
}
