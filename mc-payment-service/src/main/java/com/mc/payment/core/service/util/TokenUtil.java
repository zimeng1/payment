package com.mc.payment.core.service.util;

import cn.dev33.satoken.stp.StpUtil;

/**
 * @author Conor
 * @since 2024/6/12 下午2:06
 */
public class TokenUtil {
    private TokenUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 判断是否登录 且屏蔽掉所有异常情况
     * sa-token 在费web环境下会抛出异常,影响本项目使用:xxljob调用时
     *
     * @return
     */
    public static boolean isLogin() {
        try {
            return StpUtil.isLogin();
        } catch (Exception e) {
            // cn.dev33.satoken.exception.NotWebContextException: 非 web 上下文无法获取 HttpServletRequest
            return false;
        }
    }
}
