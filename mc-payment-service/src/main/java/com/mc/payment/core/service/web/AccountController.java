package com.mc.payment.core.service.web;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.base.BaseController;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.AccountEntity;
import com.mc.payment.core.service.model.req.AccountPageReq;
import com.mc.payment.core.service.model.rsp.AccountPageRsp;
import com.mc.payment.core.service.service.IAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 账户管理
 *
 * @author conor
 * @since 2024/01/25 10:38
 */
@Tag(name = "账户管理")
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/account")
public class AccountController extends BaseController {

    private final IAccountService accountService;

    @SaCheckPermission("account-query")
    @Operation(summary = "分页查询", description = "分页查询")
    @PostMapping("/page")
    public RetResult<BasePageRsp<AccountPageRsp>> page(@RequestBody AccountPageReq req) {
        return RetResult.data(accountService.page(req));
    }


    @SaCheckPermission("account-query")
    @Operation(summary = "查询", description = "查询详情")
    @GetMapping("/getById/{id}")
    public RetResult<AccountEntity> getById(@PathVariable("id") String id) {
        return RetResult.data(accountService.getById(id));
    }

//    @SaCheckPermission("account-add")
//    @Operation(summary = "新增", description = "新增数据,data:id")
//    @PostMapping("/save")
//    public RetResult<String> save(@RequestBody @Validated AccountSaveReq req) {
//        return accountService.save(req);
//    }

//    @SaCheckPermission("account-update")
//    @Operation(summary = "修改", description = "修改数据")
//    @PostMapping("/updateById")
//    public RetResult<Boolean> updateById(@RequestBody @Validated AccountUpdateReq req) {
//        return accountService.updateById(req);
//    }


}
