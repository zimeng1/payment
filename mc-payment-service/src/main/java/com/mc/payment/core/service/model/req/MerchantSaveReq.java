package com.mc.payment.core.service.model.req;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mc.payment.common.annotation.MaxDecimalScale;
import com.mc.payment.common.base.BaseReq;
import com.mc.payment.core.service.entity.MerchantEntity;
import com.mc.payment.core.service.model.dto.TieredRateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "商户保存参数实体")
public class MerchantSaveReq extends BaseReq {
    private static final long serialVersionUID = -6200304811032124199L;
    @Schema(title = "商户名称")
    @NotBlank(message = "[商户名称]不能为空")
    @Length(max = 20, message = "[商户名称]长度不能超过20")
    private String name;

    @Schema(title = "商户状态,[0:禁用,1:激活]")
    @NotNull(message = "[商户状态]不能为空")
    @Range(min = 0, max = 1, message = "[商户状态]必须为[0:禁用,1:激活]")
    private Integer status;

    @Schema(title = "业务范围,[字典编码:BUSINESS_SCOPE 由英文逗号隔开]")
    @NotBlank(message = "[业务范围]不能为空")
    @Length(max = 255, message = "[业务范围]长度不能超过255")
    private String businessScope;

    @Schema(title = "结算主体")
    @NotBlank(message = "[结算主体]不能为空")
    @Length(max = 40, message = "[结算主体]长度不能超过40")
    private String settlementSubject;

    @Schema(title = "结算信息")
    @NotBlank(message = "[结算信息]不能为空")
    @Length(max = 255, message = "[结算信息]长度不能超过255")
    private String settlementInfo;

    @Schema(title = "结算对接人邮箱", description = "个数不能超过20")
    @NotBlank(message = "[结算对接人邮箱]不能为空")
    private String settlementEmail;

    @Schema(title = "商户联系人")
    @NotBlank(message = "[商户联系人]不能为空")
    @Length(max = 20, message = "[商户联系人]长度不能超过20")
    private String contact;

    @Schema(title = "商户联系方式")
    @NotBlank(message = "[商户联系方式]不能为空")
    @Length(max = 20, message = "[商户联系方式]长度不能超过20")
    private String contactTel;

    @Schema(title = "商户结算机制,[0:基础费率/笔,1:累加费用/笔,2:固定费率/笔,3:阶梯费率/月交易金额]")
    @Range(min = 0, max = 3, message = "[商户结算机制]必须为[0:基础费率/笔,1:累加费用/笔,2:固定费率/笔,3:阶梯费率/月交易金额]")
    private Integer billingType;

    @Schema(title = "累加费用/固定费率")
    @MaxDecimalScale(value = 20, message = "[累加费用/固定费率]的小数位数不能超过 {value}")
    @Range(min = 0, max = 999999999, message = "[累加费用/固定费率]必须为[0-999999999]")
    private BigDecimal additionalFee;

    @Schema(title = "费用的货币单位,[例如“USD”或“SOL”]")
    private String currency;

    @Schema(title = "阶梯费率")
    private List<TieredRateDto> tieredRateList;


    public void validate() {
        String[] emails = settlementEmail.split(",");
        if (emails.length > 20) {
            throw new ValidateException("[结算对接人邮箱]个数不能超过20");
        }
        for (String email : emails) {
            if (!Validator.isEmail(email)) {
                throw new ValidateException("[结算对接人邮箱]格式错误,邮箱:" + email);
            }
        }
        if (billingType == 1) {
            if (additionalFee == null) {
                throw new ValidateException("[累加费用/固定费率]不能为空");
            }
            if (StrUtil.isBlank(currency)) {
                throw new ValidateException("[费用的货币单位]不能为空");
            }
        } else if (billingType == 2) {
            if (additionalFee == null) {
                throw new ValidateException("[累加费用/固定费率]不能为空");
            }
        } else if (billingType == 3) {
            if (tieredRateList == null || tieredRateList.isEmpty()) {
                throw new ValidateException("[阶梯费率]不能为空");
            }
            int size = tieredRateList.size();
            if (size != 3) {
                throw new ValidateException("[阶梯费率]必须为3个");
            }
            for (TieredRateDto tieredRateDto : tieredRateList) {
                if (StrUtil.isBlank(tieredRateDto.getStartAmount())) {
                    throw new ValidateException("[阶梯费率]起始金额不能为空");
                }
                if (StrUtil.isBlank(tieredRateDto.getEndAmount())) {
                    throw new ValidateException("[阶梯费率]结束金额不能为空");
                }
                if (StrUtil.isBlank(tieredRateDto.getRate())) {
                    throw new ValidateException("[阶梯费率]费率不能为空");
                }
            }
            if (!TieredRateDto.FIRST_START_AMOUNT.equals(tieredRateList.get(0).getStartAmount())) {
                throw new ValidateException("[阶梯费率]第一个起始金额必须为" + TieredRateDto.FIRST_START_AMOUNT);
            }
            if (!TieredRateDto.INFINITY_SYMBOL.equals(tieredRateList.get(size - 1).getEndAmount())) {
                throw new ValidateException("[阶梯费率]最后一个结束金额必须为" + TieredRateDto.INFINITY_SYMBOL);
            }
        }

    }

    public MerchantEntity convert() {
        MerchantEntity merchantEntity = new MerchantEntity();
        merchantEntity.setName(name);
        merchantEntity.setStatus(status);
        merchantEntity.setBusinessScope(businessScope);
        merchantEntity.setSettlementSubject(settlementSubject);
        merchantEntity.setSettlementInfo(settlementInfo);
        merchantEntity.setSettlementEmail(settlementEmail);
        merchantEntity.setContact(contact);
        merchantEntity.setContactTel(contactTel);
        if (billingType == 0) {
            merchantEntity.setBillingType(billingType);
            merchantEntity.setAdditionalFee(BigDecimal.ZERO);
            merchantEntity.setCurrency(StrUtil.EMPTY);
            merchantEntity.setTieredRateJson(StrUtil.EMPTY_JSON);
        } else if (billingType == 1) {
            merchantEntity.setBillingType(billingType);
            merchantEntity.setAdditionalFee(additionalFee);
            merchantEntity.setCurrency(currency);
            merchantEntity.setTieredRateJson(StrUtil.EMPTY_JSON);
        } else if (billingType == 2) {
            merchantEntity.setBillingType(billingType);
            merchantEntity.setAdditionalFee(additionalFee);
            merchantEntity.setCurrency(currency);
            merchantEntity.setTieredRateJson(StrUtil.EMPTY_JSON);
        } else if (billingType == 3) {
            merchantEntity.setBillingType(billingType);
            merchantEntity.setAdditionalFee(BigDecimal.ZERO);
            merchantEntity.setCurrency(StrUtil.EMPTY);
            merchantEntity.setTieredRateJson(JSONUtil.toJsonStr(tieredRateList));
        }

        return merchantEntity;
    }
}
