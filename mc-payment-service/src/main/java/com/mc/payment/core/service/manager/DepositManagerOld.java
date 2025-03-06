package com.mc.payment.core.service.manager;

import com.mc.payment.api.model.req.DepositReq;
import com.mc.payment.api.model.rsp.DepositRsp;
import com.mc.payment.core.service.model.rsp.ConfirmRsp;

public interface DepositManagerOld {
    DepositRsp depositOld(String merchantId, String merchantName, DepositReq req);

    DepositRsp deposit(String merchantId, String merchantName, DepositReq req);

    /**
     * 执行入金
     * <p>
     * 根据入金记录中的通道选择不同的入金方式
     * 比如fireblocks通道的加密货币,是本地获取一个可用的入金钱包地址返回
     * 比如paypal通道的法币,则是调用上游接口申请入金得到一个支付页面地址返回
     *
     * @param depositId
     * @return
     */
    ConfirmRsp executeDeposit(String depositId);

}
