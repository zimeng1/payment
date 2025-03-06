package com.mc.payment.core.service.model.dto;

import com.mc.payment.core.service.constant.AssetConstants;
import com.mc.payment.core.service.entity.WithdrawalRecordEntity;
import com.mc.payment.core.service.util.CommonUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "加密货币-出金事件参数")
public class CryptoWithdrawalEventVo extends BaseWebhookEventVo {

    @Schema(title = "跟踪id")
    protected String trackingId;

    @Schema(title = "状态,[0:已提交,1:待审核,2:余额不足,3:出金中,4:出金完成,5:已拒绝,6:出金错误,7,终止出金]")
    protected int status;

    @Schema(title = "金额")
    private BigDecimal amount;

    @Schema(title = "交易标识")
    private String txHash;

    @Schema(title = "通道费")
    private BigDecimal channelFee;

    @Schema(title = "出金地址")
    private String walletAddress;

    @Schema(title = "资产名称")
    private String assetName;

    @Schema(title = "资产网络")
    private String assetNet;

    @Schema(title = "Gas费-实际扣费币种")
    private BigDecimal realGasFee;
    @Schema(title = "Gas费-本币")
    private BigDecimal itselfGasFee;
    @Schema(title = "Gas费转U = (Gas费*当时的汇率)")
    private BigDecimal gasFeeToU;

    @Schema(title = "服务费(平台费)-实际扣费币种")
    private BigDecimal realServerFee;
    @Schema(title = "服务费(平台费)-本币")
    private BigDecimal itselfServerFee;
    @Schema(title = "服务费(平台费)转U = (服务费(平台费)*当时的汇率)")
    private BigDecimal serverFeeToU;

    @Schema(title = "通道费-实际扣费币种")
    private BigDecimal realChannelFee;
    @Schema(title = "通道费-本币")
    private BigDecimal itselfChannelFee;
    @Schema(title = "通道费转U = (Gas费转U + 服务费转U)")
    private BigDecimal channelFeeToU;

    @Schema(title = "实际扣费币种-单位")
    private String realUnit;
    @Schema(title = "本币-单位")
    private String itselfUnit;
    @Schema(title = "转U-单位")
    private String uUnit;

    public static CryptoWithdrawalEventVo valueOf(WithdrawalRecordEntity entity) {
        CryptoWithdrawalEventVo cryptoWithdrawalEventVo = new CryptoWithdrawalEventVo();
        cryptoWithdrawalEventVo.setAmount(entity.getAmount() == null ? BigDecimal.ZERO : entity.getAmount());
        cryptoWithdrawalEventVo.setTxHash(entity.getTxHash());
        // 为空则为零
//        withdrawalEventVo.setGasFee();
        BigDecimal gasFee = entity.getGasFee() == null ? BigDecimal.ZERO : entity.getGasFee();
        BigDecimal channelFee = entity.getChannelFee() == null ? BigDecimal.ZERO : entity.getChannelFee();
        BigDecimal feeRate = entity.getFeeRate() == null ? BigDecimal.ZERO : entity.getFeeRate();
        BigDecimal rate = entity.getRate() == null ? BigDecimal.ZERO : entity.getRate();
        // 对商户来说通道费是只 payment的服务费(收取的和gas费同一个单位)+ gas费(第三方通道的费用)
        cryptoWithdrawalEventVo.setRealGasFee(gasFee);
        cryptoWithdrawalEventVo.setGasFeeToU(gasFee.multiply(feeRate));
        cryptoWithdrawalEventVo.setItselfGasFee(CommonUtil.getExchangeFeeByRate(entity.getFeeAssetName(), entity.getAssetName(), gasFee, feeRate, rate));

        cryptoWithdrawalEventVo.setRealServerFee(channelFee);
        cryptoWithdrawalEventVo.setServerFeeToU(channelFee.multiply(feeRate));
        cryptoWithdrawalEventVo.setItselfServerFee(CommonUtil.getExchangeFeeByRate(entity.getFeeAssetName(), entity.getAssetName(), channelFee, feeRate, rate));

        // 通道费= 服务费+gas费
        cryptoWithdrawalEventVo.setRealChannelFee(gasFee.add(channelFee));
        cryptoWithdrawalEventVo.setChannelFeeToU(cryptoWithdrawalEventVo.getGasFeeToU().add(cryptoWithdrawalEventVo.getServerFeeToU()));
        cryptoWithdrawalEventVo.setItselfChannelFee(cryptoWithdrawalEventVo.getItselfGasFee().add(cryptoWithdrawalEventVo.getItselfServerFee()));

        cryptoWithdrawalEventVo.setRealUnit(entity.getFeeAssetName());
        cryptoWithdrawalEventVo.setItselfUnit(entity.getAssetName());
        cryptoWithdrawalEventVo.setUUnit(AssetConstants.AN_USDT);

        cryptoWithdrawalEventVo.setChannelFee(cryptoWithdrawalEventVo.getItselfChannelFee());
        cryptoWithdrawalEventVo.setWalletAddress(entity.getSourceAddress());
        cryptoWithdrawalEventVo.setAssetName(entity.getAssetName());
        cryptoWithdrawalEventVo.setAssetNet(entity.getAssetNet());
        cryptoWithdrawalEventVo.setStatus(entity.getStatus());
        cryptoWithdrawalEventVo.setTrackingId(entity.getTrackingId());
        return cryptoWithdrawalEventVo;
    }
}
