package com.mc.payment.core.service.facade;

import jakarta.servlet.http.HttpServletRequest;

/**
 * IEzeebillServiceFacade
 *
 * @author GZM
 * @since 2024/11/15 下午3:37
 */
public interface IPayPalServiceFacade {
	/**
	 * PayPal出金回调处理
	 * @return
	 */
	String paypalPayoutCallback(String payload, HttpServletRequest request);
	
	String paypalPayoutFailCallback(String payload, HttpServletRequest request);
}
