package com.mc.payment.core.service.util;

import com.mc.payment.core.service.service.IAssetLastQuoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Marty
 * @since 2024/5/11 19:32
 */
@Slf4j
@Component
public class Mt5util {
    @Autowired
    private IAssetLastQuoteService assetLastQuoteService;





    /**
     * 计算换汇率并转换, 例如: 1BTC = 10000USDT, 1USDT = 6.5CNY, 1BTC = 65000CNY
     *
     * @param assetName      从哪个币种转换
     * @param toFeeAssetName 需要转换成哪个币种
     * @param fee            转换的金额
     * @return
     */
    public BigDecimal getExchangeFee(String assetName, String toFeeAssetName, BigDecimal fee) {
        try {
            // 如果是 USDT 和 USDC 那么不需要转换
            if (assetName.equals("USDT") && toFeeAssetName.equals("USDC") || assetName.equals("USDC") && toFeeAssetName.equals("USDT")) {
                return fee;
            }
            if (fee.compareTo(BigDecimal.ZERO) > 0 && !assetName.equals(toFeeAssetName)) {
                // 如果assetName不是USDT，就需要转换成USDT
                if (!assetName.startsWith("USDT")) {
                    BigDecimal exchangeRate = assetLastQuoteService.getExchangeRate(assetName, true);
                    fee = fee.multiply(exchangeRate);
                }
                if (!toFeeAssetName.equals("USDT")) {
                    BigDecimal exchangeRate = assetLastQuoteService.getExchangeRate(toFeeAssetName, false);
                    if (exchangeRate.compareTo(BigDecimal.ZERO) > 0) {
                        fee = fee.divide(exchangeRate, 20, RoundingMode.HALF_UP);
                    } else {
                        log.error("[getExchangeFee]查询mt5失败, toFeeAssetName:{}, exchangeRate:{}", toFeeAssetName, exchangeRate);
                        return BigDecimal.ZERO;
                    }
                }
            }
            return fee;
        } catch (Exception e) {
            log.error("[getExchangeFee] 执行异常 rsp:{}", assetName, e);
            return BigDecimal.ZERO;
        }
    }
}
