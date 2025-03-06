package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 资产最新报价表
 * </p>
 *
 * @author conor
 * @since 2024-05-14 10:23:47
 */
@Getter
@Setter
@TableName("mcp_asset_last_quote")
@Schema(title = "AssetLastQuoteEntity对象", description = "资产最新报价表")
public class AssetLastQuoteEntity extends BaseNoLogicalDeleteEntity {

    private static final long serialVersionUID = 1L;

    @Schema(title = "品种/币对")
    @TableField("symbol")
    private String symbol;

    @Schema(title = "卖价")
    @TableField("bid")
    private BigDecimal bid;

    @Schema(title = "买价")
    @TableField("ask")
    private BigDecimal ask;

    @Schema(title = "最新报价时间")
    @TableField("tick_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date tickTime;

    @Schema(title = "数据来源,[0:MT5,1:币安]")
    @TableField("data_source")
    private Integer dataSource;
}
