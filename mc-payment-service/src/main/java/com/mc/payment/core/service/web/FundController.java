package com.mc.payment.core.service.web;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.IdUtil;
import com.mc.payment.api.model.rsp.DepositRsp;
import com.mc.payment.api.model.rsp.QueryAssetRsp;
import com.mc.payment.api.model.rsp.QueryAssetSupportedBankRsp;
import com.mc.payment.api.model.rsp.WithdrawalRsp;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.WithdrawalRecordEntity;
import com.mc.payment.core.service.facade.ExternalServiceFacade;
import com.mc.payment.core.service.manager.BackendDepositWithdrawalManager;
import com.mc.payment.core.service.manager.deposit.DepositManager;
import com.mc.payment.core.service.manager.withdrawal.WithdrawalManager;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.req.*;
import com.mc.payment.core.service.model.rsp.*;
import com.mc.payment.core.service.service.IDepositRecordDetailService;
import com.mc.payment.core.service.service.IDepositRecordService;
import com.mc.payment.core.service.service.IWithdrawalRecordDetailService;
import com.mc.payment.core.service.service.IWithdrawalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Conor
 * @since 2024/4/19 上午11:05
 */
@RequiredArgsConstructor
@Tag(name = "资金管理")
@RestController
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/fund")
public class FundController {
    private final IDepositRecordService depositRecordService;
    private final IWithdrawalRecordService withdrawalRecordService;
    private final ExternalServiceFacade externalServiceFacade;
    private final IDepositRecordDetailService depositRecordDetailService;
    private final IWithdrawalRecordDetailService withdrawalRecordDetailService;
    private final BackendDepositWithdrawalManager backendDepositWithdrawalManager;
    private final WithdrawalManager withdrawalManager;
    private final DepositManager depositManager;


    @Operation(summary = "入金记录-分页查询", description = "分页查询")
    @PostMapping("/deposit/page")
    public RetResult<BasePageRsp<DepositRecordPageRsp>> page(@RequestBody DepositPageReq req) {
        return RetResult.data(depositRecordService.page(req));
    }


    @Operation(summary = "入金记录导出", description = "入金记录导出")
    @GetMapping("/deposit/export")
    public void depositExport(DepositPageReq req, HttpServletResponse response) throws Exception {
        req.setCurrent(1);
        req.setSize(30000);
        depositRecordService.depositExport(req, response);
    }


    @Operation(summary = "入金记录-明细", description = "入金明细")
    @PostMapping("/deposit/detail")
    public RetResult<List<DepositRecordDetailRsp>> detail(@RequestBody GetDepositRecordDetailReq req) {
        List<DepositRecordDetailRsp> data = depositRecordDetailService.detailList(req);
        return RetResult.data(data);
    }


    @Operation(summary = "模拟入金", description = "用于测试环境与下游联调时使用")
    @PostMapping("/deposit/mock")
    public RetResult<Void> depositMock(@RequestBody @Valid DepositMockReq req) {
        depositManager.mock(req);
        return RetResult.ok();
    }

    @Operation(summary = "出金记录-分页查询", description = "分页查询")
    @PostMapping("/withdrawal/page")
    public RetResult<BasePageRsp<WithdrawalRecordPageRsp>> page(@RequestBody WithdrawalPageReq req) {
        return RetResult.data(withdrawalRecordService.page(req));
    }


    /**
     * 出金记录-刷新状态
     * 二期: 仅刷新状态
     * 三期: 刷新状态,并且执行出金审核流程
     *
     * @param req
     * @return
     */
    @Operation(summary = "出金记录-刷新状态", description = "刷新状态")
    @PostMapping("/withdrawal/refresh")
    public RetResult<WithdrawalRecordEntity> refresh(@RequestBody WithdrawalRecordIdReq req) {
        return externalServiceFacade.withdrawalRecordRefresh(req.getId());
    }

    // 出金操作
    @Operation(summary = "出金", description = "执行出金,会生成出金记录")
    @PostMapping("/withdrawal/execute")
    public RetResult<Boolean> withdrawalExecute(@RequestBody WithdrawalExecuteReq req) {
        WithdrawalRequestReq withdrawalRequestReq = new WithdrawalRequestReq();
        withdrawalRequestReq.setAssetName(req.getAssetName());
        withdrawalRequestReq.setNetProtocol(req.getNetProtocol());
        withdrawalRequestReq.setAddress(req.getAddress());
        withdrawalRequestReq.setAmount(req.getAmount());
        withdrawalRequestReq.setRemark("管理端出金操作");
        withdrawalRequestReq.setTrackingId(IdUtil.fastSimpleUUID());
        withdrawalRequestReq.setChannelSubType(ChannelSubTypeEnum.FIRE_BLOCKS.getCode());
        withdrawalRequestReq.setAutoAudit(1);
        RetResult<List<WithdrawalRequestRsp>> result = externalServiceFacade.withdrawalRequest(req.getMerchantId(), req.getMerchantName(), List.of(withdrawalRequestReq));
        if (!result.isSuccess()) {
            return RetResult.error(result.getMsg());
        }
        return RetResult.data(result.isSuccess());
    }

    @Operation(summary = "入金审核")
    @PostMapping("/deposit/audit")
    public RetResult<DepositAuditRsp> depositAudit(@RequestBody @Validated FundDepositAuditReq req) {
        // DepositAuditRsp rsp = externalServiceFacade.depositAudit(req);
//        return RetResult.data(rsp);
        return RetResult.ok();
    }

    @Operation(summary = "出金审核")
    @PostMapping("/withdrawal/audit")
    public RetResult<WithdrawalAuditRsp> withdrawalAudit(@RequestBody @Validated FundWithdrawalAuditReq req) {
//        return externalServiceFacade.withdrawalAudit(req);
        return RetResult.data(withdrawalManager.auditProcess(req));
    }

    @Operation(summary = "出金记录导出", description = "出金记录导出")
    @GetMapping("/withdrawal/export")
    public void withdrawalExport(WithdrawalPageReq req, HttpServletResponse response) throws Exception {
        req.setCurrent(1);
        req.setSize(30000);
        withdrawalRecordService.withdrawalExport(req, response);
    }

    @Operation(summary = "余额不足重新执行")
    @PostMapping("/withdrawal/reExecute")
    public RetResult<WithdrawalReExecuteRsp> reExecute(@RequestBody @Validated WithdrawalReexecuteReq req) {
//        WithdrawalReExecuteRsp rsp = externalServiceFacade.reExecute(req);
//        return RetResult.data(rsp);
        return RetResult.data(withdrawalManager.reExecuteProcess(req.getId()));
    }

//    @Operation(summary = "余额不足终止出金")
//    @PostMapping("/withdrawal/stopWithdrawal")
//    public RetResult<WithdrawalStopRsp> stopWithdrawal(@RequestBody @Validated WithdrawalStopReq req) {
//        WithdrawalStopRsp rsp = withdrawalRecordService.stopWithdrawal(req);
//        return RetResult.data(rsp);
//    }

    @Operation(summary = "余额不足终止出金")
    @PostMapping("/withdrawal/stopWithdrawal")
    public RetResult<Void> stopWithdrawal(@RequestBody @Validated WithdrawalStopReq req) {
        withdrawalManager.cancelProcess(req.getId());
        return RetResult.ok();
    }


    @Operation(summary = "出金记录明细列表", description = "出金记录明细列表")
    @PostMapping("/withdrawal/getDetailPageList")
    public RetResult<BasePageRsp<WithdrawalDetailRsp>> getDetailPageList(@RequestBody WithdrawalRecordDetailReq req) {
        return RetResult.data(withdrawalRecordDetailService.getDetailPageList(req));
    }


    @Operation(summary = "出金记录明细导出", description = "出金记录明细导出")
    @GetMapping("/withdrawal/detailExport")
    public void withdrawalDetailExport(WithdrawalRecordDetailReq req, HttpServletResponse response) throws Exception {
        req.setCurrent(1);
        req.setSize(30000);
        withdrawalRecordService.withdrawalDetailExport(req, response);
    }

    @Operation(summary = "入金记录明细导出", description = "入金记录明细导出")
    @GetMapping("/deposit/detailExport")
    public void depositDetailExport(DepositRecordDetailReq req, HttpServletResponse response) throws Exception {
        req.setCurrent(1);
        req.setSize(30000);
        depositRecordService.depositDetailExport(req, response);
    }


    @Operation(summary = "入金记录明细列表", description = "入金记录明细列表")
    @PostMapping("/deposit/getDetailPageList")
    public RetResult<BasePageRsp<DepositDetailRsp>> getDetailPageList(@RequestBody DepositRecordDetailReq req) {
        return RetResult.data(depositRecordDetailService.getDetailPageList(req));
    }

    // ==========后台管理端发起出入金操作==========

    @Operation(summary = "商户对应的资产列表查询接口", description = "仅供后台管理端发起出入金操作使用")
    @PostMapping("/queryAsset")
    public RetResult<List<QueryAssetRsp>> queryAsset(@RequestBody BackendQueryAssetReq req) {
        return RetResult.data(backendDepositWithdrawalManager.queryAsset(req));
    }

    @Operation(summary = "商户对应的资产支持银行列表查询接口", description = "仅供后台管理端发起出入金操作使用")
    @PostMapping("/queryAssetSupportedBank")
    public RetResult<List<QueryAssetSupportedBankRsp>> queryAssetSupportedBank(@RequestBody BackendQueryAssetSupportedBankReq req) {
        return RetResult.data(backendDepositWithdrawalManager.queryAssetSupportedBank(req));
    }

    @SaCheckPermission("fund-deposit-request")
    @Operation(summary = "入金申请接口", description = "仅供后台管理端发起出入金操作使用")
    @PostMapping("/deposit/request")
    public RetResult<DepositRsp> depositRequest(@RequestBody @Validated BackendDepositRequestReq req, HttpServletRequest request) {
        return RetResult.data(backendDepositWithdrawalManager.depositRequest(req, request));
    }

    @SaCheckPermission("fund-withdrawal-request")
    @Operation(summary = "出金申请接口", description = "仅供后台管理端发起出入金操作使用")
    @PostMapping("/withdrawal/request")
    public RetResult<WithdrawalRsp> withdrawalRequest(@RequestBody @Validated BackendWithdrawalRequestReq req, HttpServletRequest request) {
        return RetResult.data(backendDepositWithdrawalManager.withdrawalRequest(req, request));
    }


}
