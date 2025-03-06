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
 * 通道钱包日志表
 * @TableName mcp_channel_wallet_log
 */
@TableName(value ="mcp_channel_wallet_log")
@Data
public class ChannelWalletLogEntity extends BaseNoLogicalDeleteEntity implements Serializable {
    /**
     * 钱包id
     */
    @TableField(value = "wallet_id")
    private String walletId;

    /**
     * 变动余额
     */
    @TableField(value = "change_balance")
    private BigDecimal changeBalance;

    /**
     * 变动冻结金额
     */
    @TableField(value = "change_freeze_amount")
    private BigDecimal changeFreezeAmount;

    /**
     * 钱包更新时间
     */
    @TableField(value = "wallet_update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date walletUpdateTime;

    /**
     * 钱包更新原因
     */
    @TableField(value = "wallet_update_msg")
    private String walletUpdateMsg;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}