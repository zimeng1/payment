package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 货币汇率表
 * @TableName mcp_currency_rate
 */
@TableName(value ="mcp_currency_rate")
@Data
public class CurrencyRateEntity extends BaseNoLogicalDeleteEntity implements Serializable {
    /**
     * 基础货币代码，例如 USD
     */
    @TableField(value = "base_currency")
    private String baseCurrency;

    /**
     * 目标货币代码，例如 EUR
     */
    @TableField(value = "target_currency")
    private String targetCurrency;

    /**
     * 汇率，表示将一单位基础货币转换为目标货币的汇率
     */
    @TableField(value = "exchange_rate")
    private BigDecimal exchangeRate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}