package com.mc.payment.gateway.channels.ezeebill.service;

import com.mc.payment.common.base.RetResult;
import com.mc.payment.gateway.channels.ezeebill.model.req.EzeebillOrderReq;
import com.mc.payment.gateway.channels.ezeebill.model.req.EzeebillWithdrawalReq;
import com.mc.payment.gateway.channels.ezeebill.model.rsp.EzeebillOrderRsp;
import com.mc.payment.gateway.channels.ezeebill.model.rsp.EzeebillWithdrawalRsp;

/**
 * EzeebillService
 *
 * @author GZM
 * @since 2024/10/18 下午7:44
 */
public interface EzeebillService {

    RetResult<EzeebillOrderRsp> createOrder(EzeebillOrderReq req);

    RetResult<EzeebillWithdrawalRsp> createPayOut(EzeebillWithdrawalReq req);

}
