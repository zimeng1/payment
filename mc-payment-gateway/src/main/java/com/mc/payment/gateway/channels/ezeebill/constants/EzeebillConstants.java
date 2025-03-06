package com.mc.payment.gateway.channels.ezeebill.constants;

/**
 * EzeebillConstants
 *
 * @author GZM
 * @since 2024/10/20 上午2:00
 */
public interface EzeebillConstants {
    /**
     * Ezeebill入金成功返回CODE
     */
    Integer EZEEBILL_DEPOSIT_SUCCESS_CODE = 5;
    Integer EZEEBILL_DEPOSIT_SUCCESS_CODE2 = 6;


    /**
     * Ezeebill出金成功返回CODE
     */
    Integer EZEEBILL_PAYOUT_SUCCESS_CODE = 0;
    Integer EZEEBILL_PAYOUT_WAITING_CODE = 6;
    
    /**
     * Ezeebill出金返回余额不足
     */
    Integer EZEEBILL_PAYOUT_INSUFFICIENT_BALANCE_CODE = 2033;
    
    /**
     * PayPal出金返回余额不足
     */
    String PAYPAL_PAYOUT_INSUFFICIENT_BALANCE_CODE = "INSUFFICIENT_FUNDS";

    /**
     * 付款成功状态码
     */
    int RSP_SUCCESS_CODE = 0;

}
