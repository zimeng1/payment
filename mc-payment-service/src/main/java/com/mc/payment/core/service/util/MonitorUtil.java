package com.mc.payment.core.service.util;

import cn.hutool.core.util.NumberUtil;
import com.mc.payment.api.model.req.DepositReq;
import com.mc.payment.api.model.req.WithdrawalReq;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
public class MonitorUtil {
    private MonitorUtil() {
        throw new IllegalStateException("Utility class");
    }
    // 以下是辅助方法

    /**
     * 将tags中的null转为""
     */
    private static void convertNullToEmpty(String[] tags) {
        for (int i = 0; i < tags.length; i++) {
            if (tags[i] == null) {
                tags[i] = "";
            }
        }
    }

    // 以下是具体的监控方法

    /**
     * 登录次数统计
     *
     * @param userAccount
     */
    public static void loginCounter(String userAccount) {
        try {
            String[] tags = new String[4];
            tags[0] = "userAccount";
            tags[1] = userAccount;
            tags[2] = "day";
            tags[3] = LocalDate.now().toString();
            convertNullToEmpty(tags);
            Metrics.counter("payment_login", tags).increment();
        } catch (Exception e) {
            log.error("payment_login error,userAccount:{}", userAccount, e);
        }
    }


    /**
     * 入金申请次数统计
     *
     * @param merchantName
     * @param req
     */
    public static void depositRequestCounter(String merchantName, DepositReq req) {
        try {
            String[] tags = new String[10];
            tags[0] = "merchantName";
            tags[1] = merchantName;
            tags[2] = "day";
            tags[3] = LocalDate.now().toString();
            tags[4] = "trackingId";
            tags[5] = req.getTrackingId();
            tags[6] = "amount";
            tags[7] = req.getAmount().toString();
            tags[8] = "userId";
            tags[9] = req.getUserId();


            convertNullToEmpty(tags);
            Metrics.counter("payment_deposit_request", tags).increment();
        } catch (Exception e) {
            log.error("payment_deposit_request error,merchantName:{},req:{}", merchantName, req, e);
        }
    }

    /**
     * 入金成功次数统计
     *
     * @param merchantName
     */
    public static void depositSuccessCounter(String merchantName) {
        try {
            String[] tags = new String[4];
            tags[0] = "merchantName";
            tags[1] = merchantName;
            tags[2] = "day";
            tags[3] = LocalDate.now().toString();
            convertNullToEmpty(tags);
            Metrics.counter("payment_deposit_success", tags).increment();
        } catch (Exception e) {
            log.error("payment_deposit_success error,merchantName:{}", merchantName, e);
        }
    }

    /**
     * 入金失败次数统计
     *
     * @param merchantName
     */
    public static void depositErrorCounter(String merchantName) {
        try {
            String[] tags = new String[4];
            tags[0] = "merchantName";
            tags[1] = merchantName;
            tags[2] = "day";
            tags[3] = LocalDate.now().toString();
            convertNullToEmpty(tags);
            Metrics.counter("payment_deposit_error", tags).increment();
        } catch (Exception e) {
            log.error("payment_deposit_error error,merchantName:{}", merchantName, e);
        }
    }


    /**
     * 出金申请次数统计
     *
     * @param merchantName
     */
    public static void withdrawRequestCounter(String merchantName, WithdrawalReq req) {
        try {
            String[] tags = new String[10];
            tags[0] = "merchantName";
            tags[1] = merchantName;
            tags[2] = "day";
            tags[3] = LocalDate.now().toString();
            tags[4] = "trackingId";
            tags[5] = req.getTrackingId();
            tags[6] = "amount";
            tags[7] = req.getAmount().toString();
            tags[8] = "userId";
            tags[9] = req.getUserId();
            convertNullToEmpty(tags);
            Metrics.counter("payment_withdraw_request", tags).increment();
        } catch (Exception e) {
            log.error("payment_withdraw_request error,merchantName:{},req:{}", merchantName, req, e);
        }
    }


    /**
     * 出金成功次数统计
     *
     * @param merchantName
     */
    public static void withdrawSuccessCounter(String merchantName) {
        try {
            String[] tags = new String[4];
            tags[0] = "merchantName";
            tags[1] = merchantName;
            tags[2] = "day";
            tags[3] = LocalDate.now().toString();
            convertNullToEmpty(tags);
            Metrics.counter("payment_withdraw_success", tags).increment();
        } catch (Exception e) {
            log.error("payment_withdraw_success error,merchantName:{}", merchantName, e);
        }
    }

    /**
     * 出金待审核订单数统计
     *
     * @param merchantName
     */
    public static void withdrawPendingCounter(String merchantName) {
        try {
            String[] tags = new String[4];
            tags[0] = "merchantName";
            tags[1] = merchantName;
            tags[2] = "day";
            tags[3] = LocalDate.now().toString();
            convertNullToEmpty(tags);
            Metrics.counter("payment_withdraw_pending", tags).increment();
        } catch (Exception e) {
            log.error("payment_withdraw_pending error,merchantName:{}", merchantName, e);
        }
    }

    /**
     * 出金错误次数统计
     *
     * @param merchantName
     */
    public static void withdrawErrorCounter(String merchantName) {
        try {
            String[] tags = new String[4];
            tags[0] = "merchantName";
            tags[1] = merchantName;
            tags[2] = "day";
            tags[3] = LocalDate.now().toString();
            convertNullToEmpty(tags);
            Metrics.counter("payment_withdraw_error", tags).increment();
        } catch (Exception e) {
            log.error("payment_withdraw_error error,merchantName:{}", merchantName, e);
        }
    }

    /**
     * 统计商户当天入金金额
     *
     * @param merchantName 商户名
     * @param amountU      入金金额 单位U
     */
    public static void depositAmount(String merchantName, BigDecimal amountU) {
        try {
            String[] tags = new String[4];
            tags[0] = "merchantName";
            tags[1] = merchantName;
            tags[2] = "day";
            tags[3] = LocalDate.now().toString();
            convertNullToEmpty(tags);
            MeterRegistry registry = Metrics.globalRegistry;

            Gauge.builder("payment_deposit_amount", amountU, BigDecimal::doubleValue)
                    .description("Payment deposit amount")
                    .tags(tags)
                    .register(registry);
        } catch (Exception e) {
            log.error("payment_deposit_amount error,merchantName:{},amountU:{}", merchantName, amountU, e);
        }
    }

    /**
     * 统计商户当天出金金额
     */
    public static void withdrawAmount(String merchantName, BigDecimal amountU) {
        try {
            String[] tags = new String[4];
            tags[0] = "merchantName";
            tags[1] = merchantName;
            tags[2] = "day";
            tags[3] = LocalDate.now().toString();
            convertNullToEmpty(tags);
            MeterRegistry registry = Metrics.globalRegistry;

            Gauge.builder("payment_withdraw_amount", amountU, BigDecimal::doubleValue)
                    .description("Payment withdraw amount")
                    .tags(tags)
                    .register(registry);
        } catch (Exception e) {
            log.error("payment_withdraw_amount error,merchantName:{},amountU:{}", merchantName, amountU, e);
        }
    }

    /**
     * 入金各个状态统计
     *
     * @param merchantName
     * @param status
     * @param count
     */
    public static void registerDepositGauge(String merchantName, int status, int count) {
        try {
            MeterRegistry registry = Metrics.globalRegistry;
            String[] tags = new String[]{"merchantName", merchantName, "day", LocalDate.now().toString(), "status", String.valueOf(status)};
            Gauge.builder("payment_deposit", count, Integer::doubleValue)
                    .tags(tags)
                    .register(registry);
        } catch (Exception e) {
            log.error("payment_deposit error, merchantName: {}, status: {}, count: {}", merchantName, status, count, e);
        }
    }

    /**
     * 入金各币种金额统计
     *
     * @param merchantName
     * @param assetName
     * @param amount
     */
    public static void depositAmountGauge(String merchantName, String assetName, BigDecimal amount) {
        try {
            double amountDouble = NumberUtil.toDouble(amount);
            MeterRegistry registry = Metrics.globalRegistry;
            String[] tags = new String[]{"merchantName", merchantName, "day", LocalDate.now().toString(), "assetName", assetName};
            Gauge.builder("payment_deposit_asset_amount", amountDouble, Double::doubleValue)
                    .tags(tags)
                    .register(registry);
        } catch (Exception e) {
            log.error("payment_deposit_asset error, merchantName: {}, assetName: {}, amount: {}", merchantName, assetName, amount, e);
        }
    }

    /**
     * 出金各个状态统计
     *
     * @param merchantName
     * @param status
     * @param count
     */
    public static void registerWithdrawGauge(String merchantName, int status, int count) {
        try {
            MeterRegistry registry = Metrics.globalRegistry;
            String[] tags = new String[]{"merchantName", merchantName, "day", LocalDate.now().toString(), "status", String.valueOf(status)};
            Gauge.builder("payment_withdraw", count, Integer::doubleValue)
                    .tags(tags)
                    .register(registry);
        } catch (Exception e) {
            log.error("payment_withdraw error, merchantName: {}, status: {}, count: {}", merchantName, status, count, e);
        }
    }

    /**
     * 出金各币种金额统计
     *
     * @param merchantName
     * @param assetName
     * @param amount
     */
    public static void withdrawAmountGauge(String merchantName, String assetName, BigDecimal amount) {
        try {
            double amountDouble = NumberUtil.toDouble(amount);
            MeterRegistry registry = Metrics.globalRegistry;
            String[] tags = new String[]{"merchantName", merchantName, "day", LocalDate.now().toString(), "assetName", assetName};
            Gauge.builder("payment_withdraw_asset_amount", amountDouble, Double::doubleValue)
                    .tags(tags)
                    .register(registry);
        } catch (Exception e) {
            log.error("payment_withdraw_asset error, merchantName: {}, assetName: {}, amount: {}", merchantName, assetName, amount, e);
        }
    }


}
