package com.mc.payment.core.service.model.dto;

import com.mc.payment.core.service.constant.AssetConstants;
import com.mc.payment.core.service.entity.DepositRecordEntity;
import com.mc.payment.core.service.model.enums.AssetTypeEnum;
import com.mc.payment.core.service.util.CommonUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Schema(title = "加密货币-入金事件实体")
@Data
public class CryptoDepositEventVo extends BaseWebhookEventVo {

    @Schema(title = "跟踪id")
    private String trackingId;

    @Schema(title = "状态,[0:待入金,1:部分入金,2:完全入金,3:撤销入金,4:请求失败]")
    private Integer status;

    @Schema(title = "金额")
    private BigDecimal amount;

//    @Schema(title = "预估Gas费")
//    private BigDecimal gasFee;

    @Schema(title = "通道费")
    private BigDecimal channelFee;

    @Schema(title = "来源地址")
    private String sourceAddress;

    @Schema(title = "目标地址")
    private String destinationAddress;

    @Schema(title = "资产名称")
    private String assetName;

    @Schema(title = "网络")
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

    @Schema(title = "事件明细")
    private List<CryptoDepositEventDetailVo> detailList;

    public static CryptoDepositEventVo valueOf(DepositRecordEntity entity) {
        CryptoDepositEventVo cryptoDepositEventVo = new CryptoDepositEventVo();
        cryptoDepositEventVo.setAmount(entity.getAmount() == null ? BigDecimal.ZERO : entity.getAmount());
        BigDecimal gasFee = entity.getGasFee() == null ? BigDecimal.ZERO : entity.getGasFee();
        BigDecimal channelFee = entity.getChannelFee() == null ? BigDecimal.ZERO : entity.getChannelFee();
        BigDecimal feeRate = entity.getFeeRate() == null ? BigDecimal.ZERO : entity.getFeeRate();
        BigDecimal rate = entity.getRate() == null ? BigDecimal.ZERO : entity.getRate();
//        depositEventVo.setGasFee();
        // 对商户来说通道费是只 payment的服务费(收取的和gas费同一个单位)+ gas费(第三方通道的费用)
        cryptoDepositEventVo.setRealGasFee(gasFee);
        cryptoDepositEventVo.setGasFeeToU(gasFee.multiply(feeRate));
        cryptoDepositEventVo.setItselfGasFee(CommonUtil.getExchangeFeeByRate(entity.getFeeAssetName(), entity.getAssetName(), gasFee, feeRate, rate));

        cryptoDepositEventVo.setRealServerFee(channelFee);
        cryptoDepositEventVo.setServerFeeToU(channelFee.multiply(feeRate));
        cryptoDepositEventVo.setItselfServerFee(CommonUtil.getExchangeFeeByRate(entity.getFeeAssetName(), entity.getAssetName(), channelFee, feeRate, rate));

        // 通道费= 服务费+gas费
        cryptoDepositEventVo.setRealChannelFee(gasFee.add(channelFee));
        cryptoDepositEventVo.setChannelFeeToU(cryptoDepositEventVo.getGasFeeToU().add(cryptoDepositEventVo.getServerFeeToU()));
        cryptoDepositEventVo.setItselfChannelFee(cryptoDepositEventVo.getItselfGasFee().add(cryptoDepositEventVo.getItselfServerFee()));

        cryptoDepositEventVo.setRealUnit(entity.getFeeAssetName());
        cryptoDepositEventVo.setItselfUnit(entity.getAssetName());
        cryptoDepositEventVo.setUUnit(entity.getAssetType()== AssetTypeEnum.CRYPTO_CURRENCY.getCode()? AssetConstants.AN_USDT: AssetConstants.AN_USD);

        cryptoDepositEventVo.setChannelFee(cryptoDepositEventVo.getItselfChannelFee());
//        depositEventVo.setChannelFee(channelFee.add(gasFee));
        cryptoDepositEventVo.setSourceAddress(entity.getSourceAddress());
        cryptoDepositEventVo.setDestinationAddress(entity.getDestinationAddress());
        cryptoDepositEventVo.setAssetName(entity.getAssetName());
        cryptoDepositEventVo.setAssetNet(entity.getAssetNet());
        cryptoDepositEventVo.setStatus(entity.getStatus());
        cryptoDepositEventVo.setTrackingId(entity.getTrackingId());
        return cryptoDepositEventVo;
    }
}
