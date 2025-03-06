package com.mc.payment.core.service.web.merchant;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.base.BaseController;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.manager.wallet.FireBlocksWalletManager;
import com.mc.payment.core.service.model.enums.PurposeTypeEnum;
import com.mc.payment.core.service.model.req.merchant.GenerateWalletReq;
import com.mc.payment.core.service.model.req.merchant.MerchantConfigPageReq;
import com.mc.payment.core.service.model.req.merchant.MerchantConfigUpdateReq;
import com.mc.payment.core.service.model.rsp.merchant.MerchantConfigGetByIdRsp;
import com.mc.payment.core.service.model.rsp.merchant.MerchantConfigPageRsp;
import com.mc.payment.core.service.service.IMerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商户配置")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/merchantConfig")
public class MerchantConfigController extends BaseController {
    private final IMerchantService service;
    private final FireBlocksWalletManager fireBlocksWalletManager;

    @SaCheckPermission("merchantConfig-query")
    @Operation(summary = "分页查询", description = "分页查询")
    @PostMapping("/page")
    public RetResult<BasePageRsp<MerchantConfigPageRsp>> page(@RequestBody MerchantConfigPageReq req) {
        return RetResult.data(service.configPage(req));
    }

    @SaCheckPermission("merchantConfig-query")
    @Operation(summary = "查询", description = "查询详情")
    @GetMapping("/getById/{id}")
    public RetResult<MerchantConfigGetByIdRsp> getConfigById(@PathVariable("id") String id) {
        return RetResult.data(service.getConfigById(id));
    }


    @SaCheckPermission("merchantConfig-update")
    @Operation(summary = "修改", description = "修改数据")
    @PostMapping("/updateById")
    public RetResult<Boolean> updateById(@RequestBody @Validated MerchantConfigUpdateReq req) {
        return RetResult.data(service.configUpdateById(req));
    }

    @SaCheckPermission("merchantConfig-generateWallet")
    @Operation(summary = "手动生成钱包")
    @PostMapping("/generateWallet")
    public RetResult<Void> generateWallet(@RequestBody @Validated GenerateWalletReq req) {
        // 只能生成fireblocks入金钱包
        fireBlocksWalletManager.generateWallet(req, PurposeTypeEnum.DEPOSIT);
        return RetResult.ok();
    }
}
