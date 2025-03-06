package com.mc.payment.gateway;

import com.mc.payment.common.base.RetResult;
import com.mc.payment.gateway.model.req.*;
import com.mc.payment.gateway.model.rsp.*;

/**
 * 支付网关接口
 */
public interface PaymentGateway {

    RetResult<GatewayDepositRsp> deposit(GatewayDepositReq req);

    RetResult<GatewayQueryDepositRsp> queryDeposit(GatewayQueryDepositReq req);

    RetResult<GatewayWithdrawalRsp> withdrawal(GatewayWithdrawalReq req);

    RetResult<GatewayQueryWithdrawalRsp> queryWithdrawal(GatewayQueryWithdrawalReq req);

    RetResult<GatewayQueryBalanceRsp> queryBalance(GatewayQueryBalanceReq req);
}
