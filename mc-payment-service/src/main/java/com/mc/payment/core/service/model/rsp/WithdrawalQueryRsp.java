package com.mc.payment.core.service.model.rsp;

import com.mc.payment.api.model.rsp.QueryWithdrawalRsp;
import com.mc.payment.core.service.entity.WithdrawalRecordEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Conor
 * @since 2024/4/13 上午11:33
 */
@Data
public class WithdrawalQueryRsp {
    @Schema(title = "资产名称,[如:BTC]")
    private String assetName;

    @Schema(title = "资产网络")
    private String assetNet;

    @Schema(title = "网络协议")
    private String netProtocol;

    @Schema(title = "来源地址")
    private String sourceAddress;

    @Schema(title = "目标地址")
    private String destinationAddress;

    @Schema(title = "商户id")
    private String merchantId;

    @Schema(title = "商户名称")
    private String merchantName;

    @Schema(title = "金额")
    private BigDecimal amount;

    @Schema(title = "txHash")
    private String txHash;

    @Schema(title = "Gas费")
    private BigDecimal gasFee;

    @Schema(title = "通道费")
    private BigDecimal channelFee;

    @Schema(title = "状态,[0:已提交,1:待审核,2:余额不足,3:出金中,4:出金完成,5:审核不通过,6:出金错误]")
    private Integer status;

    @Schema(title = "跟踪id,申请方提供唯一跟踪ID以查询处理结果")
    private String trackingId;

    @Schema(title = "备注说明")
    private String remark;

    @Schema(title = "通道子类型,[0:BlockATM,1:FireBlocks]")
    private Integer channelSubType;

    @Schema(title = "是否自动审核,[0:否,1:是]")
    private Integer autoAudit;

    public static WithdrawalQueryRsp valueOf(WithdrawalRecordEntity entity) {
        WithdrawalQueryRsp rsp = new WithdrawalQueryRsp();
        rsp.setAssetName(entity.getAssetName());
        rsp.setAssetNet(entity.getAssetNet());
        rsp.setNetProtocol(entity.getNetProtocol());
        rsp.setSourceAddress(entity.getSourceAddress());
        rsp.setDestinationAddress(entity.getDestinationAddress());
        rsp.setMerchantId(entity.getMerchantId());
        rsp.setMerchantName(entity.getMerchantName());
        rsp.setAmount(entity.getAmount());
        rsp.setTxHash(entity.getTxHash());
        rsp.setGasFee(entity.getGasFee());
        rsp.setChannelFee(entity.getChannelFee());
        rsp.setStatus(entity.getStatus());
        rsp.setTrackingId(entity.getTrackingId());
        rsp.setRemark(entity.getRemark());
        return rsp;
    }

    public static QueryWithdrawalRsp entityMap(WithdrawalRecordEntity entity) {
        QueryWithdrawalRsp rsp = new QueryWithdrawalRsp();
        rsp.setAssetName(entity.getAssetName());
        rsp.setAssetNet(entity.getAssetNet());
        rsp.setNetProtocol(entity.getNetProtocol());
        rsp.setSourceAddress(entity.getSourceAddress());
        rsp.setDestinationAddress(entity.getDestinationAddress());
        rsp.setMerchantId(entity.getMerchantId());
        rsp.setMerchantName(entity.getMerchantName());
        rsp.setAmount(entity.getAmount());
        rsp.setTxHash(entity.getTxHash());
        rsp.setGasFee(entity.getGasFee());
        rsp.setChannelFee(entity.getChannelFee());
        rsp.setStatus(entity.getStatus());
        rsp.setTrackingId(entity.getTrackingId());
        rsp.setRemark(entity.getRemark());
        return rsp;
    }

}
