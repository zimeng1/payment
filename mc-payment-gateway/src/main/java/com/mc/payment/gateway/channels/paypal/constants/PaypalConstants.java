package com.mc.payment.gateway.channels.paypal.constants;

/**
 * EzeebillConstants
 *
 * @author GZM
 * @since 2024/10/20 上午2:00
 */
public interface PaypalConstants {
    /**
     * 出金账户类型
     */
    String RECIPIENT_TYPE_WITHDRAWAL = "EMAIL";
    String PURPOSE_WITHDRAWAL = "CASHBACK";
    String NOTE_WITHDRAWAL = "Thank you.";
    String EMAIL_SUBJECT_WITHDRAWAL = "You have a payout!";
    String EMAIL_MESSAGE_WITHDRAWAL = "You have received a payout! Thanks for using our service!";

}
