package com.mc.payment.core.service.config.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import com.mc.payment.core.service.service.IpWhitelistService;
import com.mc.payment.core.service.util.IPUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Slf4j
@Component
public class IPInterceptor implements HandlerInterceptor {

    private final IpWhitelistService ipWhitelistService;

    public IPInterceptor(IpWhitelistService ipWhitelistService) {
        this.ipWhitelistService = ipWhitelistService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 检测是否admin角色,是的话直接放行
        if (StpUtil.hasRole("admin")) {
            return true;
        }
        List<String> ipWhiteList = ipWhitelistService.getIpWhiteList();
        String ip = IPUtil.getClientIP(request);
        if (ipWhiteList.contains(ip)) {
            log.debug("IP {} is in whitelist:{}", ip,ipWhiteList);
            return true;
        } else {
            log.info("IP {} is not in whitelist:{}", ip,ipWhiteList);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Forbidden");
            return false;
        }
    }




}
