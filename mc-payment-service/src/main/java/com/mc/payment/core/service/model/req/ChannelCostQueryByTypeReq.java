package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Marty
 * @since 2024/5/22 10:37
 */
@Data
@Schema(title = "通过通道子类型获取支持资产")
public class ChannelCostQueryByTypeReq {

    // ps:前端传递了String, 需要处理下.
    @Schema(title = "通道子类型集合")
    private List<String> channelSubTypeList;
}
