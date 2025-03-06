package com.mc.payment.core.service.model.req;

import com.mc.payment.common.annotation.MaxDecimalScale;
import com.mc.payment.common.base.BaseReq;
import com.mc.payment.core.service.model.dto.AssetSimpleDto;
import com.mc.payment.core.service.model.enums.CostTypeEnum;
import com.mc.payment.core.service.model.enums.RoundMethodEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(title = "通道成本保存参数实体")
public class ChannelCostSaveReq extends BaseReq {
    private static final long serialVersionUID = -6200304811032124199L;
    @Schema(title = "成本规则名称")
    @NotBlank(message = "[成本规则名称]不能为空")
    @Length(max = 30, message = "[成本规则名称]长度不能超过20")
    private String costRuleName;

    @Schema(title = "业务动作,[0:入金,1:出金]")
    @NotNull(message = "[业务动作]不能为空")
    @Range(min = 0, max = 1, message = "[业务动作]必须为[0:入金,1:出金]")
    private Integer businessAction;

    @Schema(title = "成本类型,[0:按笔收费/U,1:按费率收费/%]")
    @NotNull(message = "[成本类型]不能为空")
    @Range(min = 0, max = 1, message = "[成本类型]必须为[0:按笔收费/U,1:按费率收费/%]")
    private Integer costType;

    @Schema(title = "成本")
    @MaxDecimalScale(value = 20, message = "[成本]的小数位数不能超过 {value}")
    @Range(min = 0, max = 999999999, message = "[成本]必须为[0-999999999]")
    private BigDecimal cost;

    @Schema(title = "费率")
    @MaxDecimalScale(value = 20, message = "[费率]的小数位数不能超过 {value}")
    @Range(min = 0, max = 999999999, message = "[费率]必须为[0-999999999]")
    private BigDecimal rate;

    @Schema(title = "取整方式,[0:向上取整,1:向下取整,2:四舍五入,9:无]")
    @NotNull(message = "[取整方式]不能为空")
    @Range(min = 0, max = 2, message = "[取整方式]必须为[0:向上取整,1:向下取整,2:四舍五入]")
    private Integer roundMethod;

    @Schema(title = "成本限额选项,[0:最低/U,1:最高/U,多选英文逗号隔开]")
    @NotNull(message = "[成本限额选项]不能为空")
    @Length(max = 10, message = "[成本限额选项]长度不能超过10")
    private String costLimitOption;

    @Schema(title = "最低成本")
    @MaxDecimalScale(value = 20, message = "[最低成本]的小数位数不能超过 {value}")
    @Range(min = 0, max = 999999999, message = "[最低成本]必须为[0-999999999]")
    private BigDecimal minCostLimit;

    @Schema(title = "最高成本")
    @MaxDecimalScale(value = 20, message = "[最高成本]的小数位数不能超过 {value}")
    @Range(min = 0, max = 999999999, message = "[最高成本]必须为[0-999999999]")
    private BigDecimal maxCostLimit;

    @Schema(title = "结算周期,[0:按日,1:按周,2:按月]")
    @NotNull(message = "[结算周期]不能为空")
    @Range(min = 0, max = 2, message = "[结算周期]必须为[0:按日,1:按周,2:按月]")
    private Integer billingCycle;

    @Schema(title = "成本精度")
    @NotNull(message = "[成本精度]不能为空")
    @Range(min = 0, max = 20, message = "[成本精度]必须为[0-20]")
    private Integer costPrecision;


    @Schema(title = "通道子类型")
    @NotNull(message = "[通道子类型]不能为空")
    @Range(min = 1, max = 6, message = "[通道子类型]必须为[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    private Integer channelSubType;

    @Schema(title = "资产的资产列表")
    @NotNull(message = "[资产的资产列表]不能为空")
    private List<AssetSimpleDto> assetList;

    public void validate() {
        // 最低成本不能大于最高成本
        if (minCostLimit != null && maxCostLimit != null && minCostLimit.compareTo(maxCostLimit) > 0) {
            throw new IllegalArgumentException("[最低成本]不能大于[最高成本]");
        }
    }

    public void init() {
        // 如果是按笔收费,取整方式只能为无,成本限额选项为空
        if (costType == CostTypeEnum.ITEM_0.getCode()) {
            roundMethod = RoundMethodEnum.NONE.getCode();
            costLimitOption = "";
        }
    }

}
