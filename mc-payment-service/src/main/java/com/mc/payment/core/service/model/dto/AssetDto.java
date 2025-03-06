package com.mc.payment.core.service.model.dto;

import com.mc.payment.core.service.model.rsp.AssetBankDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class AssetDto {

    @Schema(title = "资产名称/币种")
    private String assetName;

    @Schema(title = "资产网络/币种网络/支付类型全称名称")
    private String assetNet;

    @Schema(title = "网络协议/支付类型")
    private String netProtocol;

    @Schema(title = "合约地址")
    private String tokenAddress;

    @Schema(title = "支持的银行列表")
    private List<AssetBankDto> bankList;
}
