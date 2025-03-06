package com.mc.payment.core.service.web.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.base.BaseController;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.model.req.UserPageReq;
import com.mc.payment.core.service.model.req.UserSaveReq;
import com.mc.payment.core.service.model.req.UserUpdatePasswordReq;
import com.mc.payment.core.service.model.req.UserUpdateReq;
import com.mc.payment.core.service.model.rsp.UserGetRsp;
import com.mc.payment.core.service.model.rsp.UserPageRsp;
import com.mc.payment.core.service.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 系统账号管理
 *
 * @author conor
 * @since 2024/01/25 10:38
 */
@Tag(name = "系统账号")
@RestController
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/user")
public class UserController extends BaseController {


    @Autowired
    private IUserService userService;

    @SaCheckPermission("user-query")
    @Operation(summary = "分页查询", description = "author: conor")
    @PostMapping("page")
    public RetResult<BasePageRsp<UserPageRsp>> page(@RequestBody UserPageReq req) {
        return RetResult.data(userService.page(req));
    }


    @SaCheckPermission("user-query")
    @Operation(summary = "查询账号", description = "查询账号详情")
    @GetMapping("/getById/{id}")
    public RetResult<UserGetRsp> getById(@PathVariable("id") String id) {
        return userService.getUserGetRsp(id);
    }

    @SaCheckPermission("user-add")
    @Operation(summary = "新增", description = "新增账号")
    @PostMapping("/save")
    public RetResult<String> save(@RequestBody @Validated UserSaveReq req) {
        return userService.save(req);
    }

    @SaCheckPermission("user-update")
    @Operation(summary = "修改", description = "修改账号信息")
    @PostMapping("/updateById")
    public RetResult<Boolean> updateById(@RequestBody @Validated UserUpdateReq req) {
        return userService.updateById(req);
    }

    @SaCheckPermission("user-update")
    @Operation(summary = "修改密码")
    @PostMapping("/updatePassword")
    public RetResult<Boolean> updatePassword(@RequestBody @Validated UserUpdatePasswordReq req) {
        return userService.updatePassword(req);
    }


    @SaCheckPermission("user-update")
    @Operation(summary = "重置密码", description = "重置账号密码,返回新密码")
    @GetMapping("/resetPassword/{id}")
    public RetResult<String> resetPassword(@PathVariable("id") String id) {
        return userService.resetPassword(id);
    }

}
