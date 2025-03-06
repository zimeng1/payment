package com.mc.payment.core.service.model.rsp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mc.payment.core.service.model.enums.*;
import com.mc.payment.core.service.serializer.BigDecimalToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author conor
 * @since 2024/2/1 11:11:18
 */
@Data
@Schema(title = "通道成本-分页返回实体")
public class ChannelCostPageRsp implements Serializable {
    private static final long serialVersionUID = -1560313421823133299L;

    @Schema(title = "资产名称,[如:BTC]")
    private String assetName;

    @Schema(title = "id")
    protected String id;

    @Schema(title = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date createTime;

    @Schema(title = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date updateTime;

    @Schema(title = "成本规则名称")
    @TableField("cost_rule_name")
    private String costRuleName;

    @Schema(title = "业务动作,[0:入金,1:出金]")
    @TableField("business_action")
    private Integer businessAction;

    @Schema(title = "成本类型,[0:按笔收费/U,1:按费率收费/%]")
    @TableField("cost_type")
    private Integer costType;

    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    @Schema(title = "成本")
    @TableField("cost")
    private BigDecimal cost;


    @Schema(title = "费率,单位%")
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    @TableField("rate")
    private BigDecimal rate;

    @Schema(title = "取整方式,[0:向上取整,1:向下取整,2:四舍五入,9:无]")
    @TableField("round_method")
    private Integer roundMethod;

    @Schema(title = "成本限额选项,[0:最低/U,1:最高/U,多选英文逗号隔开]")
    @TableField("cost_limit_option")
    private String costLimitOption;


    @Schema(title = "最低成本")
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    @TableField("min_cost_limit")
    private BigDecimal minCostLimit;


    @Schema(title = "最高成本")
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    @TableField("max_cost_limit")
    private BigDecimal maxCostLimit;

    @Schema(title = "结算周期,[0:按日,1:按周,2:按月]")
    @TableField("billing_cycle")
    private Integer billingCycle;

    @Schema(title = "更新者")
    protected String updateBy;

    @Schema(title = "成本精度")
    @NotNull(message = "[成本精度]不能为空")
    @Range(min = 0, max = 20, message = "[成本精度]必须为[0-20]")
    @TableField("cost_precision")
    private Integer costPrecision;

    @Schema(title = "通道子类型")
    @TableField(value = "channel_sub_type")
    private Integer channelSubType;

    // 枚举描述字段===========================
    @Schema(title = "业务动作-描述")
    public String getBusinessActionDesc() {
        return BusinessActionEnum.getEnumDesc(businessAction);
    }

    @Schema(title = "成本类型-描述")
    public String getCostTypeDesc() {
        return CostTypeEnum.getEnumDesc(costType);
    }

    @Schema(title = "取整方式,[0:向上取整,1:向下取整,2:四舍五入,9:无]")
    public String getRoundMethodDesc() {
        return RoundMethodEnum.getEnumDesc(roundMethod);
    }

    @Schema(title = "结算周期,[0:按日,1:按周,2:按月]")
    public String getBillingCycleDesc() {
        return BillingCycleEnum.getEnumDesc(billingCycle);
    }


    @Schema(title = "通道子类型-描述")
    public String getChannelSubTypeDesc() {
        return ChannelSubTypeEnum.getEnumDesc(channelSubType);
    }

}
