package com.mc.payment.core.service.web.openapi;

import com.mc.payment.api.PaymentFeignClient;
import com.mc.payment.api.model.req.*;
import com.mc.payment.api.model.rsp.*;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.config.RequestContext;
import com.mc.payment.core.service.config.aspect.LogExecutionTime;
import com.mc.payment.core.service.facade.OpenApiServiceFacade;
import com.mc.payment.core.service.manager.DepositManagerOld;
import com.mc.payment.core.service.manager.deposit.DepositManager;
import com.mc.payment.core.service.model.dto.CurrentMerchantDto;
import com.mc.payment.core.service.model.dto.MerchantAssetDto;
import com.mc.payment.core.service.service.MerchantChannelAssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@Tag(name = "开放接口")
@RequiredArgsConstructor
@LogExecutionTime
@RestController
@RequestMapping("/openapi")
public class OpenApiController implements PaymentFeignClient {

    private final OpenApiServiceFacade openApiServiceFacade;
    private final DepositManagerOld depositManagerOld;
    private final DepositManager depositManager;
    private final MerchantChannelAssetService merchantChannelAssetService;

    @Override
    @Operation(summary = "入金申请")
    @Parameters({
            @Parameter(name = "X-Access-Key", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Signature", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Timestamp", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping("/v1/deposit/request")
    public RetResult<DepositRsp> deposit(@Validated @RequestBody DepositReq req) {
        CurrentMerchantDto currentMerchant = RequestContext.getCurrentMerchant();
//        return RetResult.data(depositManager.depositOld(currentMerchant.getId(), currentMerchant.getName(), req));
//        return RetResult.data(depositManager.deposit(currentMerchant.getId(), currentMerchant.getName(), req));
        return depositManager.requestProcess(currentMerchant.getId(), currentMerchant.getName(), req);
    }

    @Override
    @Operation(summary = "入金查询")
    @Parameters({
            @Parameter(name = "X-Access-Key", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Signature", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Timestamp", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping("/v1/deposit/query")
    public RetResult<QueryDepositRsp> queryDeposit(@Validated @RequestBody QueryDepositReq req) {
        CurrentMerchantDto currentMerchant = RequestContext.getCurrentMerchant();
        return openApiServiceFacade.queryDeposit(currentMerchant.getId(), req.getTrackingId());
    }

    @Override
    @Operation(summary = "取消入金")
    @Parameters({
            @Parameter(name = "X-Access-Key", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Signature", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Timestamp", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping("/v1/deposit/cancel")
    public RetResult<Boolean> cancelDeposit(@Validated @RequestBody CancelDepositReq req) {
        CurrentMerchantDto currentMerchant = RequestContext.getCurrentMerchant();
        return openApiServiceFacade.cancelDeposit(currentMerchant.getId(), req.getTrackingId());
    }

    @Override
    @Operation(summary = "汇率查询")
    @Parameters({
            @Parameter(name = "X-Access-Key", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Signature", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Timestamp", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping("/v1/queryExchangeRate")
    public RetResult<QueryExchangeRateRsp> queryExchangeRate(@Validated @RequestBody QueryExchangeRateReq req) {
        QueryExchangeRateRsp rsp = openApiServiceFacade.queryExchangeRate(req.getAssetType(), req.getBaseCurrency(), req.getTargetCurrency());
        return rsp != null ? RetResult.data(rsp) : RetResult.error("未查询到汇率");
    }

    @Override
    @Operation(summary = "出金申请")
    @Parameters({
            @Parameter(name = "X-Access-Key", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Signature", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Timestamp", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping("/v1/withdrawal/request")
    public RetResult<WithdrawalRsp> withdrawal(@Validated @RequestBody WithdrawalReq req) {
        CurrentMerchantDto currentMerchant = RequestContext.getCurrentMerchant();
        return openApiServiceFacade.withdrawal(currentMerchant.getId(), currentMerchant.getName(), req);
    }

    @Override
    @Operation(summary = "出金查询")
    @Parameters({
            @Parameter(name = "X-Access-Key", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Signature", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Timestamp", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping("/v1/withdrawal/query")
    public RetResult<QueryWithdrawalRsp> queryWithdrawal(@Validated @RequestBody QueryWithdrawalReq req) {
        CurrentMerchantDto currentMerchant = RequestContext.getCurrentMerchant();
        return openApiServiceFacade.queryWithdrawal(currentMerchant.getId(), req.getTrackingId());
    }


    @Override
    @Operation(summary = "资产列表查询")
    @Parameters({
            @Parameter(name = "X-Access-Key", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Signature", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Timestamp", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping("/v1/queryAsset")
    public RetResult<List<QueryAssetRsp>> queryAsset(@Validated @RequestBody QueryAssetReq req) {
        CurrentMerchantDto currentMerchant = RequestContext.getCurrentMerchant();
        List<MerchantAssetDto> list = merchantChannelAssetService.queryAsset(currentMerchant.getId(), req.getAssetType());
        return RetResult.data(list.stream().map(MerchantAssetDto::convert).toList());
    }

    @Override
    @Operation(summary = "资产支持银行查询")
    @Parameters({
            @Parameter(name = "X-Access-Key", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Signature", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Timestamp", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    @PostMapping("/v1/queryAssetSupportedBank")
    public RetResult<List<QueryAssetSupportedBankRsp>> queryAssetSupportedBank(@Validated @RequestBody QueryAssetSupportedBankReq req) {
        return RetResult.data(openApiServiceFacade.queryAssetSupportedBank(req));
    }

    @Override
    @Operation(summary = "入金报表查询")
    @PostMapping("/v1/deposit/queryReport")
    @Parameters({
            @Parameter(name = "X-Access-Key", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Signature", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Timestamp", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    public RetResult<List<QueryDepositReportRsp>> queryReport(@RequestBody @Validated QueryDepositReportReq req) {
//        return RetResult.data(openApiServiceFacade.queryReport(req));
        return RetResult.ok();
    }

    @Override
    @Operation(summary = "商户钱包快照查询")
    @PostMapping("/v1/queryMerchantSnapshot")
    @Parameters({
            @Parameter(name = "X-Access-Key", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Signature", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
            @Parameter(name = "X-Timestamp", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
    })
    public RetResult<PageRsp<MerchantWalletSnapshotRsp>> queryMerchantSnapshot(@RequestBody QueryMerchantSnapshotReq req) {
        //return RetResult.data(openApiServiceFacade.queryMerchantSnapshot(req));
        return RetResult.ok();
    }
}
