package com.mc.payment.gateway.channels.passtopay.service;

import com.mc.payment.common.base.RetResult;
import com.mc.payment.gateway.channels.passtopay.model.req.PassToPayCreateOrderReq;
import com.mc.payment.gateway.channels.passtopay.model.rsp.PassToPayCreateOrderRsp;

public interface PassToPayService {

    RetResult<PassToPayCreateOrderRsp> createOrder(PassToPayCreateOrderReq req);
}
