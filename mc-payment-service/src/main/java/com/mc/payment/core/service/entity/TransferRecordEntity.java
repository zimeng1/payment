package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mc.payment.core.service.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 划转记录
 * </p>
 *
 * @author conor
 * @since 2024-02-02 15:30:01
 */
@Getter
@Setter
@TableName("mcp_transfer_record")
@Schema(title = "TransferRecordEntity对象", description = "划转记录")
public class TransferRecordEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(title = "发起账号")
    @TableField("source_account_id")
    private String sourceAccountId;

    @Schema(title = "发起账号地址")
    @TableField("source_wallet_address")
    private String sourceWalletAddress;

    @Schema(title = "接收账号")
    @TableField("destination_account_id")
    private String destinationAccountId;

    @Schema(title = "接收账号地址")
    @TableField("destination_wallet_address")
    private String destinationWalletAddress;

    @Schema(title = "金额")
    @TableField("amount")
    private BigDecimal amount;

    @Schema(title = "资产id")
    @TableField("asset_id")
    private String assetId;

    @Schema(title = "资产名称")
    @TableField("asset_type")
    private String assetType;

    @Schema(title = "资产网络")
    @TableField("asset_net")
    private String assetNet;

    @Schema(title = "备注")
    @TableField("remark")
    private String remark;

    @Schema(title = "划转状态,[0:待确认,1:已确认,2:失败]")
    @TableField("record_status")
    private Integer recordStatus;

    @Schema(title = "手续费")
    @TableField("fee")
    private BigDecimal fee;

    @Schema(title = "完成时间")
    @TableField("completion_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date completionTime;


}
