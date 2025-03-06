package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.model.req.MerchantWalletLogReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商户钱包日志表
 *
 * @TableName mcp_merchant_wallet_log
 */
@TableName(value = "mcp_merchant_wallet_log")
@Data
public class MerchantWalletLogEntity extends BaseNoLogicalDeleteEntity implements Serializable {
    /**
     * 钱包id
     */
    @TableField(value = "wallet_id")
    private String walletId;

    /**
     * 变动事件类型,[0:入金,1:出金,2:fireblocks通道钱包同步]
     *
     * @see com.mc.payment.core.service.model.enums.ChangeEventTypeEnum
     */
    @TableField(value = "change_event_type")
    private Integer changeEventType;

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

    /**
     * changeEventType=0:入金记录id
     * changeEventType=1:出金记录id
     * changeEventType=2:通道钱包变更日志id
     */
    @Schema(title = "关联id,具体由变动事件类型决定")
    @TableField("correlation_id")
    private String correlationId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    public static MerchantWalletLogEntity valueOf(MerchantWalletLogReq merchantWalletLogReq) {
        MerchantWalletLogEntity merchantWalletLogEntity = new MerchantWalletLogEntity();
        merchantWalletLogEntity.setWalletId(merchantWalletLogReq.getWalletId());
        merchantWalletLogEntity.setChangeEventType(merchantWalletLogReq.getChangeEventTypeEnum().getCode());
        merchantWalletLogEntity.setChangeBalance(merchantWalletLogReq.getChangeBalance());
        merchantWalletLogEntity.setChangeFreezeAmount(merchantWalletLogReq.getChangeFreezeAmount());
        merchantWalletLogEntity.setWalletUpdateTime(merchantWalletLogReq.getWalletUpdateTime());
        merchantWalletLogEntity.setWalletUpdateMsg(merchantWalletLogReq.getWalletUpdateMsg());
        merchantWalletLogEntity.setCorrelationId(merchantWalletLogReq.getCorrelationId());
        return merchantWalletLogEntity;

    }
}