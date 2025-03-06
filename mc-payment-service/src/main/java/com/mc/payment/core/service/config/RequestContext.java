package com.mc.payment.core.service.config;

import com.mc.payment.core.service.model.dto.CurrentMerchantDto;

/**
 * 接口请求上下文
 *
 * @author Conor
 * @since 2024-04-17 17:25:59.595
 */
public class RequestContext {
    // 商户id
    private static final ThreadLocal<CurrentMerchantDto> CURRENT_MERCHANT = new ThreadLocal<>();

    public static void setCurrentMerchant(CurrentMerchantDto data) {
        CURRENT_MERCHANT.set(data);
    }

    public static CurrentMerchantDto getCurrentMerchant() {
        return CURRENT_MERCHANT.get();
    }

    public static void clear() {
        CURRENT_MERCHANT.remove();
    }
}
