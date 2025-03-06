package com.mc.payment.core.service.util;

import cn.hutool.core.util.StrUtil;
import com.mc.payment.common.constant.NationAndCurrencyCodeEnum;
import com.mc.payment.core.service.constant.AssetConstants;
import com.mc.payment.core.service.entity.WithdrawalRecordEntity;
import com.mc.payment.core.service.model.dto.BaseWebhookEventVo;
import com.mc.payment.core.service.model.dto.CryptoWithdrawalEventVo;
import com.mc.payment.core.service.model.enums.AssetTypeEnum;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 常用的工具类
 *
 * @author Marty
 * @since 2024/5/21 14:23
 */
@Slf4j
public class CommonUtil {

    /**
     * 拼接资产名称和手续费币种名称, 返回list
     *
     * @param assetName    资产名称
     * @param feeAssetName 手续费币种-资产名称
     * @return
     */
    public static List<String> getSymbolListByNames(String assetName, String feeAssetName) {
        List<String> symbolList = new ArrayList<>();
        symbolList.add(assetName + AssetConstants.AN_USDT);
        if (!assetName.equals(feeAssetName)) {
            symbolList.add(feeAssetName + AssetConstants.AN_USDT);
        }
        return symbolList;
    }

    /**
     * 查询assetName对应USDT的汇率, 如果是usdt则返回1, 其余找map, map没有就返回0
     *
     * @param assetName      资产名称
     * @param symbolPriceMap 汇率map, key为资产名称+USDT
     * @return
     */
    public static BigDecimal getRateByNameAndMap(String assetName, Map<String, BigDecimal> symbolPriceMap) {
        return AssetConstants.AN_USDT.equals(assetName) ? BigDecimal.ONE : symbolPriceMap.get(assetName + AssetConstants.AN_USDT) == null ? BigDecimal.ZERO : symbolPriceMap.get(assetName + AssetConstants.AN_USDT);
    }

    /**
     * 检查金额是否符合最小金额的现在, true:小于最小充值金额, 不给继续出入金
     *
     * @param assetName      资产名称
     * @param amount         金额
     * @param minAmount      最小金额(单位U)
     * @param symbolPriceMap 汇率
     * @return
     */
    public static boolean checkMinAmount(String assetName, BigDecimal amount, BigDecimal minAmount, Map<String, BigDecimal> symbolPriceMap) {
        if (AssetConstants.AN_USDT.equals(assetName) || AssetConstants.AN_USDC.equals(assetName)) {
            if (amount.compareTo(minAmount) < 0) {
                return true;
            }
        } else {
            if (symbolPriceMap.get(assetName + AssetConstants.AN_USDT) != null) {
                BigDecimal amountToU = amount.multiply(symbolPriceMap.get(assetName + AssetConstants.AN_USDT));
                if (amountToU.compareTo(minAmount) < 0) {
                    return true;
                }
            } else {
                log.warn("[checkMinAmount]查不到汇率, 不检查最小充值金额, assetName:{}", assetName);
            }
        }
        return false;
    }

    /**
     * 计算换汇率并转换, 例如: 1BTC需要转换成ETH单位,
     * 假设BTC转USDT的汇率为60000, 则1BTC = 1*60000 = 60000 USDT
     * 假设ETH转USDT的汇率为3000, 则1USDT = 1 / 3000 = 0.00033ETH
     * 则1BTC = 60000 USDT = 60000 / 3000 = 20ETH
     *
     * @param assetName   从哪个币种转换
     * @param toAssetName 需要转换成哪个币种
     * @param fee         转换的金额
     * @param rate        assetName转u的汇率
     * @param toRate      toAssetName转u的汇率
     * @return
     */
    public static BigDecimal getExchangeFeeByRate(String assetName, String toAssetName, BigDecimal fee, BigDecimal rate, BigDecimal toRate) {
        try {
            if (assetName.equals(toAssetName)) {
                return fee;
            }
            // 如果是 USDT 和 USDC 那么不需要转换
            if (assetName.equals("USDT") && toAssetName.equals("USDC") || assetName.equals("USDC") && toAssetName.equals("USDT")) {
                return fee;
            }

            if (fee.compareTo(BigDecimal.ZERO) > 0) {
                // 如果assetName不是USDT，就需要转换成USDT
                if (!assetName.startsWith("USDT")) {
                    fee = fee.multiply(rate);
                }
                if (!toAssetName.equals("USDT")) {
                    if (toRate.compareTo(BigDecimal.ZERO) > 0) {
                        fee = fee.divide(toRate, 20, RoundingMode.HALF_UP);
                    } else {
                        log.error("[getExchangeFeeByRate], 转换的汇率为0, toAssetName:{}, toRate:{}", toAssetName, toRate);
                        return BigDecimal.ZERO;
                    }
                }
            }
            return fee;
        } catch (Exception e) {
            log.error("[getExchangeFeeByRate] 执行异常", e);
            return BigDecimal.ZERO;
        }
    }

    public static Map<String, String> getAllHeaders(HttpServletRequest request) {
        Map<String, String> headersMap = new HashMap<>();

        // 获取所有请求头的名称
        Enumeration<String> headerNames = request.getHeaderNames();

        // 遍历所有请求头并存储在Map中
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headersMap.put(headerName, headerValue);
        }

        return headersMap;
    }

    public static void main(String[] args) {
        //     * 计算换汇率并转换, 例如: 1BTC需要转换成ETH单位,
        //     * 假设BTC转USDT的汇率为60000, 则1BTC = 1*60000 = 60000 USDT
        //     * 假设ETH转USDT的汇率为3000, 则1USDT = 1 / 3000 = 0.00033ETH
        //     * 则1BTC = 60000 USDT = 60000 / 3000 = 20ETH
        String assetName = "BTC";
        String toAssetName = "ETH";
        BigDecimal fee = new BigDecimal("1");
        BigDecimal feeRate = new BigDecimal("3000");
        BigDecimal rate = new BigDecimal("60000");
        BigDecimal exchangeFeeByRate = CommonUtil.getExchangeFeeByRate(assetName, toAssetName, fee, rate, feeRate);
        System.out.println(exchangeFeeByRate);

        //模拟测试测试
        WithdrawalRecordEntity recordEntity = new WithdrawalRecordEntity();
        recordEntity.setAssetName("ETH");
        recordEntity.setStatus(0);
        recordEntity.setAmount(new BigDecimal("10"));
        recordEntity.setGasFee(new BigDecimal("1"));
        recordEntity.setChannelFee(new BigDecimal("2"));
        recordEntity.setFeeAssetName("TRX");
        recordEntity.setRate(new BigDecimal("3000"));
        recordEntity.setFeeRate(new BigDecimal("0.11886000000000000000"));
        BaseWebhookEventVo eventVo = CryptoWithdrawalEventVo.valueOf(recordEntity);
        System.out.println(eventVo.toString());
    }


    /**
     * 选择支付通道
     *
     * @param assetType
     * @param assetName
     * @param netProtocol
     * @return
     */
    public static ChannelSubTypeEnum choosePaymentChannel(int assetType, String assetName, String netProtocol) {
        ChannelSubTypeEnum channelSubTypeEnum = ChannelSubTypeEnum.UNDECIDED;
        if (AssetTypeEnum.CRYPTO_CURRENCY.getCode() == assetType) {
            channelSubTypeEnum = ChannelSubTypeEnum.FIRE_BLOCKS;
        } else {
            if (StrUtil.isNotBlank(netProtocol)) {
                if (ChannelSubTypeEnum.PAY_PAL.getDesc().equals(netProtocol)) {
                    channelSubTypeEnum = ChannelSubTypeEnum.PAY_PAL;
                } else if ("CNY".equals(assetName) && ("Alipay QR".equals(netProtocol) || "Alipay WAP".equals(netProtocol))) {
                    // 写死是因为当前还未有通道优先级功能,以及该通道只支持这种资产
                    channelSubTypeEnum = ChannelSubTypeEnum.PASS_TO_PAY;
                } else if (ChannelSubTypeEnum.EZEEBILL.getDesc().equals(netProtocol)) {
                    channelSubTypeEnum = ChannelSubTypeEnum.EZEEBILL;
                } else if (judgeCheezeePay(assetName, netProtocol)) {
                    channelSubTypeEnum = ChannelSubTypeEnum.CHEEZEE_PAY;
                } else {
                    channelSubTypeEnum = ChannelSubTypeEnum.OFA_PAY;
                }
            }
        }
        return channelSubTypeEnum;
    }

    public static boolean judgeCheezeePay(String assetName, String netProtocol) {
        if (NationAndCurrencyCodeEnum.IDR.name().equals(assetName)) {
            if ("QRIS".equals(netProtocol) || "DANA".equals(netProtocol) || "LINKAJA".equals(netProtocol) ||
                    "OVO".equals(netProtocol) || "SHOPEEPAY".equals(netProtocol) || "BNI".equals(netProtocol) ||
                    "BRI".equals(netProtocol) || "CIMB".equals(netProtocol) || "DANAMON".equals(netProtocol) ||
                    "MANDIRI".equals(netProtocol) || "MAYBANK".equals(netProtocol) || "PERMATA".equals(netProtocol)) {
                return true;
            }

            // 出金方式
            if ("BANK_ID".equals(netProtocol)) {
                return true;
            }
        }

        if (NationAndCurrencyCodeEnum.INR.name().equals(assetName)) {
            if ("LOCAL_PAY".equals(netProtocol)) {
                return true;
            }

            //出金方式
            if ("BANK_IN".equals(netProtocol)) {
                return true;
            }
        }

        if (NationAndCurrencyCodeEnum.THB.name().equals(assetName)) {
            if ("KBANK_OR_qrPay".equals(netProtocol)) {
                return true;
            }
            //出金方式
            if ("BANK_TH".equals(netProtocol)) {
                return true;
            }
        }

        if (NationAndCurrencyCodeEnum.BRL.name().equals(assetName)) {
            if ("LOCAL_PAY".equals(netProtocol) || "LOCAL_PAY".equals(netProtocol)) {
                return true;
            }
            //出金方式
            if ("PIX".equals(netProtocol)) {
                return true;
            }
        }

        return false;
    }


}
