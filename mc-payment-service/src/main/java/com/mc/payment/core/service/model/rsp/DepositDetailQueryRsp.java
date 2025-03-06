package com.mc.payment.core.service.model.rsp;

import com.mc.payment.core.service.entity.DepositRecordDetailEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Conor
 * @since 2024/4/13 上午11:33
 */
@Data
public class DepositDetailQueryRsp {

    @Schema(title = "资产名称,[如:BTC]")
    private String assetName;

    @Schema(title = "网络协议")
    private String netProtocol;

    @Schema(title = "来源地址")
    private String sourceAddress;

    @Schema(title = "目标地址")
    private String destinationAddress;

    @Schema(title = "TxHash")
    private String txHash;

    @Schema(title = "金额")
    private BigDecimal amount;

    @Schema(title = "网络费")
    private BigDecimal networkFee;

    @Schema(title = "服务费")
    private BigDecimal serviceFee;

    @Schema(title = "目标地址-余额")
    private BigDecimal addrBalance;

    @Schema(title = "fireblocks回调内容的交易更新时间(精确到毫秒):The transaction’s last update date and time, in Unix timestamp. eg:1713267871232")
    private Long lastUpdated;

    @Schema(title = "当时币种转换为USDT的汇率")
    private BigDecimal rate;

    @Schema(title = "当时手续费币种转换为USDT的汇率")
    private BigDecimal feeRate;


    public static DepositDetailQueryRsp valueOf(DepositRecordDetailEntity entity) {
        DepositDetailQueryRsp rsp = new DepositDetailQueryRsp();
        rsp.setAssetName(entity.getAssetName());
        rsp.setNetProtocol(entity.getNetProtocol());
        rsp.setSourceAddress(entity.getSourceAddress());
        rsp.setDestinationAddress(entity.getDestinationAddress());
        rsp.setTxHash(entity.getTxHash());
        rsp.setAmount(entity.getAmount());
        rsp.setNetworkFee(entity.getNetworkFee());
        rsp.setServiceFee(entity.getServiceFee());
        rsp.setAddrBalance(entity.getAddrBalance());
        rsp.setLastUpdated(entity.getLastUpdated());
        rsp.setRate(entity.getRate());
        rsp.setFeeRate(entity.getFeeRate());
        return rsp;
    }
}
