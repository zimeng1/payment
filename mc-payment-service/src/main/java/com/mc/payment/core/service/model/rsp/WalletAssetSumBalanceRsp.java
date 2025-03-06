package com.mc.payment.core.service.model.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mc.payment.core.service.serializer.BigDecimalSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author conor
 * @since 2024/2/2 15:33:01
 */
@Data
@Schema(title = "钱包-查账号和资产为维度的汇总总额")
public class WalletAssetSumBalanceRsp implements Serializable {

    private static final long serialVersionUID = -8681391934877963610L;

    @Schema(title = "账号id")
    private String accountId;

    @Schema(title = "资产名称,[如:BTC]")
    private String assetName;

    @JsonFormat(pattern = "#.####", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "资产总额：展示对应资产钱包下，所有协议余额的总额")
    private BigDecimal sumBalance;

    public WalletAssetSumBalanceRsp(String accountId, String assetName, BigDecimal sumBalance) {
        this.accountId = accountId;
        this.assetName = assetName;
        this.sumBalance = sumBalance;
    }

    public WalletAssetSumBalanceRsp() {
    }
}
