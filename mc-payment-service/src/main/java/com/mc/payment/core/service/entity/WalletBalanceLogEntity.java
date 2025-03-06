package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 钱包余额变动表
 * @TableName mcp_wallet_balance_log
 */
@TableName(value ="mcp_wallet_balance_log")
@Data
public class WalletBalanceLogEntity extends BaseNoLogicalDeleteEntity implements Serializable {
    /**
     * 钱包id
     */
    @TableField(value = "wallet_id")
    private String walletId;

    /**
     * 本次余额
     */
    @TableField(value = "current_balance")
    private BigDecimal currentBalance;

    /**
     * 本次冻结金额
     */
    @TableField(value = "current_freeze_amount")
    private BigDecimal currentFreezeAmount;

    /**
     * 上一次余额
     */
    @TableField(value = "previous_balance")
    private BigDecimal previousBalance;

    /**
     * 上一次冻结金额
     */
    @TableField(value = "previous_freeze_amount")
    private BigDecimal previousFreezeAmount;

    /**
     * 钱包更新时间
     */
    @TableField(value = "wallet_update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date walletUpdateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}