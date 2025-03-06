package com.mc.payment.core.service.facade;

import com.mc.payment.gateway.channels.ezeebill.model.req.EzeebillWithdrawalCallBackReq;

/**
 * IEzeebillServiceFacade
 *
 * @author GZM
 * @since 2024/11/19 下午4:39
 */
public interface IEzeebillServiceFacade {
	
	String processWithdrawalCallback(EzeebillWithdrawalCallBackReq req);
	
}
