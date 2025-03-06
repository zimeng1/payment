package com.mc.payment.core.service.model.req.merchant;

import com.mc.payment.core.service.base.BasePageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class MerchantConfigPageReq extends BasePageReq {
    @Schema(title = "商户id", description = "支持右模糊查询")
    private String id;
    @Schema(title = "商户名称", description = "支持模糊查询")
    private String name;
    @Schema(title = "通道子类型集合", description = "不传则查询所有")
    private List<Integer> channelSubTypeList;
}
