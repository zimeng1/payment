package com.mc.payment.core.service.model.req;

import com.mc.payment.core.service.base.BasePageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author conor
 * @since 2024/2/1 11:04:26
 */
@Data
@Schema(title = "商户-分页查询参数实体")
public class MerchantPageReq extends BasePageReq {
    private static final long serialVersionUID = -5686746619667063701L;

    @Schema(title = "商户id")
    private String id;

    @Schema(title = "商户名称")
    private String name;

    @Schema(title = "支持通道")
    private List<Integer> channelSubTypeList;

    @Schema(title = "商户状态,[0:禁用,1:激活]")
    private Integer status;

    @Schema(title = "业务范围")
    private String businessScope;

}
