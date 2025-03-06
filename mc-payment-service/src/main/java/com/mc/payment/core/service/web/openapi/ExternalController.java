package com.mc.payment.core.service.web.openapi;

import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.base.BaseController;
import com.mc.payment.core.service.config.RequestContext;
import com.mc.payment.core.service.config.aspect.LogExecutionTime;
import com.mc.payment.core.service.facade.AssetConfigServiceFacade;
import com.mc.payment.core.service.facade.ExternalServiceFacade;
import com.mc.payment.core.service.model.dto.CurrentMerchantDto;
import com.mc.payment.core.service.model.req.*;
import com.mc.payment.core.service.model.rsp.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "统一外部系统访问接口")
@LogExecutionTime
@RestController
@RequestMapping("/external")
public class ExternalController extends BaseController {
    @Autowired
    private ExternalServiceFacade service;

    @Autowired
    private AssetConfigServiceFacade assetConfigServiceFacade;

    @Operation(summary = "入金申请")
    @Parameters({
            @Parameter(name = "X-Access-Key", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Signature", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Timestamp", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping(ApiVersionConstants.API_V1_PREFIX + "/deposit/request")
    public RetResult<DepositRequestRsp> depositRequest(@RequestBody @Validated DepositRequestReq req) {
        CurrentMerchantDto currentMerchant = RequestContext.getCurrentMerchant();
        RetResult<DepositRequestRsp> result = null;
        try {
            result = service.depositRequest(currentMerchant.getId(), currentMerchant.getName(), req);
        } catch (Exception e) {
            log.error("入金申请 req:{} 异常", req, e);
            result = RetResult.error();
        }
        return result;
    }

    @Operation(summary = "取消入金申请")
    @Parameters({
            @Parameter(name = "X-Access-Key", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Signature", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Timestamp", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping(ApiVersionConstants.API_V1_PREFIX + "/deposit/cancel")
    public RetResult<Boolean> depositCancel(@RequestBody @Validated DepositCancelReq req) {
        return service.depositRequest(RequestContext.getCurrentMerchant().getId(), req);
    }

    @Operation(summary = "钱包地址余额查询")
    @Parameters({
            @Parameter(name = "X-Access-Key", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Signature", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Timestamp", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping(ApiVersionConstants.API_V1_PREFIX + "/wallet/balance")
    public RetResult<List<WalletBalanceRsp>> walletBalance(@RequestBody @Validated WalletBalanceReq req) {
        return service.walletBalance(RequestContext.getCurrentMerchant().getId(), req);
    }

    @Operation(summary = "钱包地址余额汇总查询")
    @Parameters({
            @Parameter(name = "X-Access-Key", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Signature", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Timestamp", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping(ApiVersionConstants.API_V1_PREFIX + "/wallet/balance/sum")
    public RetResult<List<WalletBalanceSumRsp>> walletBalanceSum(@RequestBody @Validated WalletBalanceSumReq req) {
        return service.walletBalanceSum(RequestContext.getCurrentMerchant().getId(), req);
    }

    @Operation(summary = "出金申请")
    @Parameters({
            @Parameter(name = "X-Access-Key", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Signature", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Timestamp", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping(ApiVersionConstants.API_V1_PREFIX + "/withdrawal/request")
    public RetResult<List<WithdrawalRequestRsp>> withdrawalRequest(@RequestBody List<@Valid WithdrawalRequestReq> req) {
        CurrentMerchantDto currentMerchant = RequestContext.getCurrentMerchant();
        RetResult<List<WithdrawalRequestRsp>> result = null;
        try {
            result = service.withdrawalRequest(currentMerchant.getId(), currentMerchant.getName(), req);
        } catch (Exception e) {
            log.error("出金申请 req:{} 异常", req, e);
            result = RetResult.error();
        }
        return result;
    }

    @Operation(summary = "出金审核")
    @Parameters({
            @Parameter(name = "X-Access-Key", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Signature", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Timestamp", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping(ApiVersionConstants.API_V1_PREFIX + "/withdrawal/audit")
    public RetResult<WithdrawalAuditRsp> withdrawalAudit(@RequestBody @Validated WithdrawalAuditReq req) {
        return service.withdrawalAudit(RequestContext.getCurrentMerchant().getId(), req);
    }

    @Operation(summary = "Webhook事件查询")
    @Parameters({
            @Parameter(name = "X-Access-Key", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Signature", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Timestamp", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping(ApiVersionConstants.API_V1_PREFIX + "/webhook/query")
    public RetResult<List<Object>> webhookQuery(@RequestBody @Validated WebhookEventQueryReq req) {
        return service.webhookQuery(RequestContext.getCurrentMerchant().getId(), req);
    }

    @Operation(summary = "资产列表查询")
    @Parameters({
            @Parameter(name = "X-Access-Key", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Signature", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Timestamp", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping(ApiVersionConstants.API_V1_PREFIX + "/asset/list/query")
    public RetResult<List<AssetListQueryRsp>> assetListQuery(@RequestBody AssetListQueryReq req) {
        return service.assetListQuery(req);
    }

    @Operation(summary = "预估费用查询接口")
    @Parameters({
            @Parameter(name = "X-Access-Key", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Signature", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Timestamp", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping(ApiVersionConstants.API_V1_PREFIX + "/estimate/fee")
    public RetResult<EstimateFeeRsp> estimateFee(@RequestBody @Validated EstimateFeeReq req) {
        return assetConfigServiceFacade.estimateFeeNew(req);
    }


    @Operation(summary = "检查是否可以出金")
    @Parameters({
            @Parameter(name = "X-Access-Key", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Signature", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Timestamp", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping(ApiVersionConstants.API_V1_PREFIX + "/withdrawal/check")
    public RetResult<WithdrawalCheckRsp> withdrawalCheck(@RequestBody @Validated WithdrawalCheckReq req) {
        return service.withdrawalCheck(RequestContext.getCurrentMerchant().getId(), req);
    }

    @Operation(summary = "出金查询")
    @Parameters({
            @Parameter(name = "X-Access-Key", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Signature", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Timestamp", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping(ApiVersionConstants.API_V1_PREFIX + "/withdrawal/query")
    public RetResult<List<WithdrawalQueryRsp>> withdrawalQuery(@RequestBody @Validated WithdrawalQueryReq req) {
        return service.withdrawalQuery(RequestContext.getCurrentMerchant().getId(), req);
    }


    @Operation(summary = "入金查询")
    @Parameters({
            @Parameter(name = "X-Access-Key", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Signature", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Timestamp", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping(ApiVersionConstants.API_V1_PREFIX + "/deposit/query")
    public RetResult<List<DepositQueryRsp>> depositQuery(@RequestBody @Validated DepositQueryReq req) {
        return service.depositQuery(RequestContext.getCurrentMerchant().getId(), req);
    }


}

