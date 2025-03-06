package com.mc.payment.core.service.model.req;

import com.mc.payment.core.service.base.BasePageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "通道成本分页查询参数实体")
public class ChannelCostPageReq extends BasePageReq {
    private static final long serialVersionUID = -6200304811032124199L;

    @Schema(title = "成本规则id")
    private String id;

    @Schema(title = "成本规则名称")
    private String costRuleName;

    @Schema(title = "业务动作,[0:入金,1:出金]")
    private Integer businessAction;

    @Schema(title = "成本类型,[0:按笔收费/U,1:按费率收费/%]")
    private Integer costType;

    @Schema(title = "成本")
    private BigDecimal cost;

    @Schema(title = "取整方式,[0:向上取整,1:向下取整,2:四舍五入,9:无]")
    private Integer roundMethod;

    @Schema(title = "成本限额,[0:最低/U,1:最高/U]")
    private Integer costLimit;

    @Schema(title = "结算周期,[0:按日,1:按周,2:按月]")
    private Integer billingCycle;


}
