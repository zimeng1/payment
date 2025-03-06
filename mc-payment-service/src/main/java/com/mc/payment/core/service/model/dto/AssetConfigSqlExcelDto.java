package com.mc.payment.core.service.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Conor
 * @since 2024/4/26 下午4:13
 */
@Data
public class AssetConfigSqlExcelDto {
    @Schema(title = "资产名称,[如:BTC]")
    @ExcelProperty("资产名称")
    private String assetName;

    @Schema(title = "资产网络,[如:BRC20]")
    @ExcelProperty("资产网络")
    private String assetNet;

    @Schema(title = "网络协议")
    @ExcelProperty("网络协议")
    private String netProtocol;

    @Schema(title = "通道资产名称")
    @ExcelProperty("通道资产名称")
    private String channelAssetName;

    @Schema(title = "需要转换的手续费 资产名称,[如:USDT 出金就需用到 ETH 的手续费]")
    @ExcelProperty("手续费币种")
    private String feeAssetName;
}
