package com.mc.payment.core.service.web.sys;

import cn.dev33.satoken.stp.StpUtil;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.base.BaseController;
import com.mc.payment.core.service.model.req.LoginReq;
import com.mc.payment.core.service.model.rsp.LoginRsp;
import com.mc.payment.core.service.service.IUserService;
import com.mc.payment.core.service.util.IPUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证
 *
 * @author conor
 * @since 2024/01/25 10:38
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/auth")
public class AuthController extends BaseController {

    private final IUserService userService;

    public AuthController(IUserService userService) {
        this.userService = userService;
    }


    @Operation(summary = "登录", description = "author: conor")
    @PostMapping("login")
    public RetResult<LoginRsp> login(@RequestBody LoginReq loginParam, HttpServletRequest request) {
        return userService.login(loginParam, IPUtil.getClientIP(request));
    }

    @Operation(summary = "是否登录", description = "author: conor")
    @PostMapping("isLogin")
    public RetResult<Boolean> isLogin() {
        return RetResult.data(StpUtil.isLogin());
    }

    @Operation(summary = "注销登录")
    @PostMapping("logout")
    public RetResult logout() {
        StpUtil.logout();
        return RetResult.ok();
    }


}
