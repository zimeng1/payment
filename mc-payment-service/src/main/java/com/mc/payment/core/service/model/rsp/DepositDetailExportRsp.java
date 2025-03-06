package com.mc.payment.core.service.model.rsp;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mc.payment.core.service.serializer.BigDecimalSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Marty
 * @since 2024/5/16 11:22
 */
@Data
public class DepositDetailExportRsp implements Serializable {

    @Schema(title = "入金详情id")
    @ExcelIgnore
    private String id;

    @Schema(title = "入金id")
    @ExcelProperty(value = "入金id",index = 0)
    @ColumnWidth(20)
    private String recordId;

    @Schema(title = "txHash")
    @ExcelProperty(value = "txHash",index = 1)
    @ColumnWidth(20)
    private String txHash;

    @Schema(title = "入金确认时间")
    @ExcelProperty(value = "入金确认时间",index = 2)
    @ColumnWidth(18)
    private Date createTime;

    @Schema(title = "入金资产")
    @ExcelProperty(value = "入金资产",index = 3)
    @ColumnWidth(12)
    private String assetName;

    @Schema(title = "入金网络协议")
    @ExcelProperty(value = "入金网络协议",index = 4)
    @ColumnWidth(16)
    private String netProtocol;

    @Schema(title = "入金金额")
    @ExcelProperty(value = "入金金额",index = 5)
    @ColumnWidth(12)
    private BigDecimal amount;

    @Schema(title = "来源地址")
    @ExcelProperty(value = "来源地址",index = 6)
    @ColumnWidth(12)
    private String sourceAddress;

    @Schema(title = "入金商户")
    @ExcelProperty(value = "入金商户",index = 7)
    @ColumnWidth(15)
    private String merchantName;

    @Schema(title = "目标地址")
    @ExcelProperty(value = "目标地址",index = 8)
    @ColumnWidth(12)
    private String destinationAddress;

    @Schema(title = "交易费")
    @ExcelProperty(value = "交易费",index = 9)
    @ColumnWidth(12)
    private BigDecimal networkFee;

    @Schema(title = "目标地址余额")
    @ExcelProperty(value = "目标地址余额",index = 10)
    @ColumnWidth(16)
    private BigDecimal addrBalance;
}
