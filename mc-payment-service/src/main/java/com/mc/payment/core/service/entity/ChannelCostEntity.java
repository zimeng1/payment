package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mc.payment.core.service.base.BaseEntity;
import com.mc.payment.core.service.model.dto.AssetSimpleDto;
import com.mc.payment.core.service.model.enums.*;
import com.mc.payment.core.service.model.req.ChannelCostSaveReq;
import com.mc.payment.core.service.model.req.ChannelCostUpdateReq;
import com.mc.payment.core.service.serializer.BigDecimalToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author conor
 * @since 2024-01-31 18:46:14
 */
@Getter
@Setter
@TableName("mcp_channel_cost")
@Schema(title = "ChannelCostEntity对象", description = "渠道费用")
public class ChannelCostEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(title = "成本规则名称")
    @TableField("cost_rule_name")
    private String costRuleName;

    @Schema(title = "业务动作,[0:入金,1:出金]")
    @TableField("business_action")
    private Integer businessAction;

    @Schema(title = "成本类型,[0:按笔收费/U,1:按费率收费/%]")
    @TableField("cost_type")
    private Integer costType;

    @Schema(title = "成本")
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
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
    @TableField(value = "min_cost_limit", updateStrategy = FieldStrategy.IGNORED, insertStrategy = FieldStrategy.IGNORED)
    private BigDecimal minCostLimit;

    @Schema(title = "最高成本")
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    @TableField(value = "max_cost_limit", updateStrategy = FieldStrategy.IGNORED, insertStrategy = FieldStrategy.IGNORED)
    private BigDecimal maxCostLimit;

    @Schema(title = "结算周期,[0:按日,1:按周,2:按月]")
    @TableField("billing_cycle")
    private Integer billingCycle;

    @Schema(title = "成本精度")
    @NotNull(message = "[成本精度]不能为空")
    @Range(min = 0, max = 20, message = "[成本精度]必须为[0-20]")
    @TableField("cost_precision")
    private Integer costPrecision;

    @Schema(title = "通道子类型")
    @TableField(value = "channel_sub_type")
    private Integer channelSubType;

    @Schema(title = "支持的资产列表")
    @TableField(exist = false)
    private transient List<AssetSimpleDto> assetList;

    // 枚举描述字段===========================
    @Schema(title = "业务动作-描述")
    public String getBusinessActionDesc() {
        return BusinessActionEnum.getEnumDesc(businessAction);
    }

    @Schema(title = "成本类型-描述")
    public String getCostTypeDesc() {
        return CostTypeEnum.getEnumDesc(costType);
    }

    public static ChannelCostEntity valueOf(ChannelCostSaveReq req) {
        ChannelCostEntity channelCostEntity = new ChannelCostEntity();
        channelCostEntity.setCostRuleName(req.getCostRuleName());
        channelCostEntity.setChannelSubType(req.getChannelSubType());
        channelCostEntity.setBusinessAction(req.getBusinessAction());
        channelCostEntity.setCostType(req.getCostType());
        channelCostEntity.setCost(req.getCost());
        channelCostEntity.setRoundMethod(req.getRoundMethod());
        channelCostEntity.setCostLimitOption(req.getCostLimitOption());
        channelCostEntity.setMinCostLimit(req.getMinCostLimit());
        channelCostEntity.setMaxCostLimit(req.getMaxCostLimit());
        channelCostEntity.setBillingCycle(req.getBillingCycle());
        channelCostEntity.setRate(req.getRate());
        channelCostEntity.setCostPrecision(req.getCostPrecision());
        return channelCostEntity;
    }

    @Schema(title = "取整方式,[0:向上取整,1:向下取整,2:四舍五入,9:无]")
    public String getRoundMethodDesc() {
        return RoundMethodEnum.getEnumDesc(roundMethod);
    }

    @Schema(title = "结算周期,[0:按日,1:按周,2:按月]")
    public String getBillingCycleDesc() {
        return BillingCycleEnum.getEnumDesc(billingCycle);
    }


    //=================

    public static ChannelCostEntity valueOf(ChannelCostUpdateReq req) {
        ChannelCostEntity channelCostEntity = new ChannelCostEntity();
        channelCostEntity.setCostRuleName(req.getCostRuleName());
        channelCostEntity.setChannelSubType(req.getChannelSubType());
        channelCostEntity.setBusinessAction(req.getBusinessAction());
        channelCostEntity.setCostType(req.getCostType());
        channelCostEntity.setCost(req.getCost());
        channelCostEntity.setRoundMethod(req.getRoundMethod());
        channelCostEntity.setCostLimitOption(req.getCostLimitOption());
        channelCostEntity.setMinCostLimit(req.getMinCostLimit());
        channelCostEntity.setMaxCostLimit(req.getMaxCostLimit());
        channelCostEntity.setBillingCycle(req.getBillingCycle());
        channelCostEntity.setRate(req.getRate());
        channelCostEntity.setCostPrecision(req.getCostPrecision());
        channelCostEntity.setId(req.getId());
        return channelCostEntity;
    }

    @Schema(title = "通道子类型-描述")
    public String getChannelSubTypeDesc() {
        return ChannelSubTypeEnum.getEnumDesc(channelSubType);
    }
}
