package com.mc.payment.core.service.manager;

import com.mc.payment.core.service.model.req.platform.CryptoProtocolUpdateReq;
import com.mc.payment.core.service.model.req.platform.FiatPayTypeUpdateReq;

public interface PayProtocolManager {

    boolean fiatUpdateById(FiatPayTypeUpdateReq req);

    boolean cryptoUpdateById(CryptoProtocolUpdateReq req);
}
