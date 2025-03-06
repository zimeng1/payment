package com.mc.payment.gateway.adapter;

import com.mc.payment.common.base.RetResult;
import com.mc.payment.gateway.PaymentGateway;
import com.mc.payment.gateway.channels.ezeebill.config.EzeebillConfig;
import com.mc.payment.gateway.channels.ezeebill.constants.EzeebillConstants;
import com.mc.payment.gateway.channels.ezeebill.model.req.EzeebillOrderReq;
import com.mc.payment.gateway.channels.ezeebill.model.req.EzeebillWithdrawalReq;
import com.mc.payment.gateway.channels.ezeebill.model.rsp.EzeebillOrderRsp;
import com.mc.payment.gateway.channels.ezeebill.model.rsp.EzeebillWithdrawalRsp;
import com.mc.payment.gateway.channels.ezeebill.service.EzeebillService;
import com.mc.payment.gateway.channels.ezeebill.util.EzeebillUtil;
import com.mc.payment.gateway.model.req.*;
import com.mc.payment.gateway.model.rsp.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * EzeebillPaymentGatewayAdapter
 *
 * @author GZM
 * @since 2024/10/18 下午5:30
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class EzeebillPaymentGatewayAdapter implements PaymentGateway {

    private final EzeebillService ezeebillService;
    private final EzeebillConfig ezeebillConfig;

    @Override
    public RetResult<GatewayDepositRsp> deposit(GatewayDepositReq req) {
        RetResult<GatewayDepositRsp> retResult = new RetResult<>();
        try{
            //拼装ezeebillOrderReq
            EzeebillOrderReq ezeebillOrderReq = EzeebillOrderReq.convertParent(req);
            ezeebillOrderReq.setMerchId(ezeebillConfig.getMerch_id(req.getCurrency()));
            ezeebillOrderReq.setTermId(ezeebillConfig.getTerm_id());
            ezeebillOrderReq.setAccessId(ezeebillConfig.getAccess_id(req.getCurrency()));
//            ezeebillOrderReq.setSecretKey(ezeebillConfig.getHashKey(EzeebillUtil.getCurrencyCode(req.getCurrency())));
            //发起订单申请
            RetResult<EzeebillOrderRsp> result = ezeebillService.createOrder(ezeebillOrderReq);
            //判断返回结果是否成功
            if (result.isSuccess()) {
                EzeebillOrderRsp data = result.getData();
                GatewayDepositRsp gatewayDepositRsp = new GatewayDepositRsp();
                gatewayDepositRsp.setTransactionId(data.getMerch_order_id()+"");
                gatewayDepositRsp.setRedirectUrl(data.getRedirectUrl());
                gatewayDepositRsp.setChannelTransactionId(data.getTxn_no());
                retResult = RetResult.data(gatewayDepositRsp);
            }else {
                retResult = RetResult.error(result.getMsg());
            }
        } catch (Exception e) {
            log.error("deposit API call failed", e);
            retResult = RetResult.error("The API call failed");
        } finally {
            log.info("deposit API call req:{},result: {}", req, retResult);
        }

        return retResult;
    }

    @Override
    public RetResult<GatewayQueryDepositRsp> queryDeposit(GatewayQueryDepositReq req) {
        return null;
    }

    @Override
    public RetResult<GatewayWithdrawalRsp> withdrawal(GatewayWithdrawalReq req) {
        BigDecimal amount = new BigDecimal(req.getAmount());
        if (amount.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException("金额需为整数!");
        }
        RetResult<GatewayWithdrawalRsp> retResult = new RetResult<>();
        try {
            EzeebillWithdrawalReq ezeebillWithdrawalReq = EzeebillWithdrawalReq.valueOf(req);
            ezeebillWithdrawalReq.setMerchId(ezeebillConfig.getMerch_id(req.getAssetName()));
            ezeebillWithdrawalReq.setTermId(ezeebillConfig.getTerm_id());
            ezeebillWithdrawalReq.setAccessId(ezeebillConfig.getAccess_id(req.getAssetName()));
            ezeebillWithdrawalReq.setOperatorId(ezeebillConfig.getOperator_id());
            ezeebillWithdrawalReq.setPassword(ezeebillConfig.getPassword());
            ezeebillWithdrawalReq.setSecretKey(ezeebillConfig.getHashKey(EzeebillUtil.getCurrencyCode(req.getAssetName())));

            RetResult<EzeebillWithdrawalRsp> result = ezeebillService.createPayOut(ezeebillWithdrawalReq);
            GatewayWithdrawalRsp gatewayWithdrawalRsp = new GatewayWithdrawalRsp();
            EzeebillWithdrawalRsp data = result.getData();
            if(result.isSuccess()) {
                gatewayWithdrawalRsp.setChannelTransactionId(data.getTxn_no());
                retResult = RetResult.data(gatewayWithdrawalRsp);
            }else {
                if(data.getTxn_response_code() == EzeebillConstants.EZEEBILL_PAYOUT_INSUFFICIENT_BALANCE_CODE){
                    gatewayWithdrawalRsp.setStatus(2);
                }
                retResult = RetResult.error(result.getMsg(),gatewayWithdrawalRsp);
            }
        } catch (Exception e) {
            retResult = RetResult.error("The API call failed");
            log.error("withdrawal API call failed", e);
        } finally {
            log.info("withdrawal API call req:{},result: {}", req, retResult);
        }
        return retResult;
    }

    @Override
    public RetResult<GatewayQueryWithdrawalRsp> queryWithdrawal(GatewayQueryWithdrawalReq req) {
        return null;
    }

    @Override
    public RetResult<GatewayQueryBalanceRsp> queryBalance(GatewayQueryBalanceReq req) {
        return null;
    }
}
