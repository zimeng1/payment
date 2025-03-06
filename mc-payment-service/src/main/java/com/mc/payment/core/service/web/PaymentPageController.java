package com.mc.payment.core.service.web;

import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.facade.AssetConfigServiceFacade;
import com.mc.payment.core.service.facade.IPaymentPageServiceFacade;
import com.mc.payment.core.service.manager.deposit.DepositManager;
import com.mc.payment.core.service.model.req.*;
import com.mc.payment.core.service.model.rsp.ConfirmRsp;
import com.mc.payment.core.service.model.rsp.DepositInfoRsp;
import com.mc.payment.core.service.model.rsp.EstimateFeeRsp;
import com.mc.payment.core.service.model.rsp.WithDrawalInfoRsp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author conor
 * @since 2024/7/24 下午9:02:18
 */
@RequiredArgsConstructor
@Tag(name = "支付页面接口")
@RestController
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/payment/page")
public class PaymentPageController {

    private final IPaymentPageServiceFacade paymentPageServiceFacade;

    private final AssetConfigServiceFacade assetConfigServiceFacade;
    private final DepositManager depositManager;

    @Operation(summary = "查询收银(入金)页面基础信息")
    @PostMapping("/deposit/info")
    public RetResult<DepositInfoRsp> depositInfo(@RequestBody @Validated PaymentPageInfoReq req) {
        return paymentPageServiceFacade.depositInfo(req.getEncryptId());
    }

    @Operation(summary = "确认支付")
    @PostMapping("/deposit/confirm")
    public RetResult<ConfirmRsp> depositConfirm(@Validated @RequestBody DepositConfirmReq req) {
//        return paymentPageServiceFacade.depositConfirm(req);
        return depositManager.executeProcess(req);
    }

    @Operation(summary = "入金状态查询")
    @PostMapping("/deposit/status")
    public RetResult<Integer> depositStatus(@Validated @RequestBody DepositStatusReq req) {
        return RetResult.data(paymentPageServiceFacade.depositStatus(req));
    }

    @Operation(summary = "查询提现(出金)页面基础信息")
    @PostMapping("/withdraw/info")
    public RetResult<WithDrawalInfoRsp> withdrawInfo(@RequestBody @Validated PaymentPageInfoReq req) {
        return paymentPageServiceFacade.withdrawInfo(req.getEncryptId());
    }

    @Operation(summary = "确认提现(出金)")
    @PostMapping("/withdraw/confirm")
    public RetResult<ConfirmRsp> withdrawConfirm(@Validated @RequestBody WithdrawalConfirmReq req) {
        return paymentPageServiceFacade.withdrawConfirm(req);
    }

    @Operation(summary = "预估费用查询接口")
    @PostMapping("/estimate/fee")
    public RetResult<EstimateFeeRsp> estimateFee(@RequestBody @Validated PaymentPageEstimateFeeReq req) {
        EstimateFeeReq estimateFeeReq = new EstimateFeeReq();
        estimateFeeReq.setAssetName(req.getAssetName());
        estimateFeeReq.setNetProtocol(req.getNetProtocol());
        estimateFeeReq.setChannelSubType(1);
        return assetConfigServiceFacade.estimateFeeNew(estimateFeeReq);
    }

}
