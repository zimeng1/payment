package com.mc.payment.core.service.web.merchant;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.base.BaseController;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.MerchantEntity;
import com.mc.payment.core.service.model.req.MerchantAuditReq;
import com.mc.payment.core.service.model.req.MerchantPageReq;
import com.mc.payment.core.service.model.req.MerchantSaveReq;
import com.mc.payment.core.service.model.req.MerchantUpdateReq;
import com.mc.payment.core.service.model.req.merchant.MerchantListReq;
import com.mc.payment.core.service.model.rsp.MerchantPageRsp;
import com.mc.payment.core.service.model.rsp.merchant.MerchantListRsp;
import com.mc.payment.core.service.service.IMerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商户管理
 *
 * @author conor
 * @since 2024/01/25 10:38
 */
@Tag(name = "商户管理")
@Slf4j
@RestController
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/merchant")
public class MerchantController extends BaseController {


    @Autowired
    private IMerchantService merchantService;

    @SaCheckPermission("merchant-query")
    @Operation(summary = "分页查询", description = "分页查询")
    @PostMapping("/page")
    public RetResult<BasePageRsp<MerchantPageRsp>> page(@RequestBody MerchantPageReq req) {
        return RetResult.data(merchantService.page(req));
    }

    @SaCheckPermission("merchant-query")
    @Operation(summary = "当前登录用户的商户列表查询")
    @PostMapping("/currentLoginList")
    public RetResult<List<MerchantListRsp>> currentLoginList(@RequestBody MerchantListReq req) {
        return RetResult.data(merchantService.currentLoginList(req));
    }


    @SaCheckPermission("merchant-query")
    @Operation(summary = "查询", description = "查询详情")
    @GetMapping("/getById/{id}")
    public RetResult<MerchantEntity> getById(@PathVariable("id") String id) {
        return RetResult.data(merchantService.getById(id));
    }

    @SaCheckPermission("merchant-add")
    @Operation(summary = "新增", description = "新增数据,data:id")
    @PostMapping("/save")
    public RetResult<String> save(@RequestBody @Validated MerchantSaveReq req) {
        return RetResult.data(merchantService.save(req));
    }

    @SaCheckPermission("merchant-update")
    @Operation(summary = "修改", description = "修改数据")
    @PostMapping("/updateById")
    public RetResult<Boolean> updateById(@RequestBody @Validated MerchantUpdateReq req) {
        return RetResult.data(merchantService.updateById(req));
    }

    @SaCheckPermission("merchant-update")
    @Operation(summary = "重置商户sk")
    @GetMapping("/resetSK/{id}")
    public RetResult<String> resetSK(@PathVariable("id") String id) {
        return RetResult.data(merchantService.resetSK(id));
    }

    @PostMapping("/updateDepositAudit")
    @Operation(summary = "修改商户入金审核", description = "修改商户入金审核")
    public RetResult updateDepositAudit(@RequestBody @Validated MerchantAuditReq req) {
        merchantService.updateDepositAudit(req);
        return RetResult.ok();
    }

    @PostMapping("/updateWithdralAudit")
    @Operation(summary = "修改商户出金审核", description = "修改商户出金审核")
    public RetResult updateWithdralAudit(@RequestBody @Validated MerchantAuditReq req) {
        merchantService.updateWithdralAudit(req);
        return RetResult.ok();
    }

}
