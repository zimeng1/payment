package com.mc.payment.core.service.model.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mc.payment.core.service.base.BasePageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author conor
 * @since 2024/2/1 11:04:26
 */
@Data
@Schema(title = "通道配置-分页查询参数实体")
public class ChannelPageReq extends BasePageReq {
    private static final long serialVersionUID = -5686746619667063701L;

    @Schema(title = "通道id")
    private String id;

    @Schema(title = "通道名称")
    private String name;

    @Schema(title = "通道状态,[0:禁用,1:激活]")
    private Integer status;

    @Schema(title = "通道类型,[0:虚拟货币支付,1:法币]")
    private Integer channelType;

    @Schema(title = "通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    private Integer channelSubType;

    @Schema(title = "通道有效期-开始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expirationDateStart;

    @Schema(title = "通道有效期-结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expirationDateEnd;

    @Schema(title = "优先级,[分为5个等级，1为最高，5为最小]")
    private Integer priority;

}
