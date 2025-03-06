package com.mc.payment.core.service.web.sys;

import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.service.SysIpCountryService;
import com.mc.payment.core.service.util.IPUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 临时放到这个服务,后续需要迁移到对应的集团基础服务
 *
 * @author Conor
 * @since 2024/6/11 下午4:35
 */
@RequiredArgsConstructor
@Tag(name = "IP管理")
@RestController
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/ip")
public class IpController {
    private final SysIpCountryService service;

    // 查询ip归属地
    @CrossOrigin
    @Operation(summary = "查询ip归属地", description = "查询ip归属地")
    @GetMapping("/queryCountry/{ip}")
    public RetResult<String> queryCountry(@PathVariable("ip") String ip) {
        return RetResult.data(service.queryCountry(ip));
    }

    @CrossOrigin
    @Operation(summary = "当前ip归属地", description = "查询ip归属地")
    @GetMapping("/currentIPCountry")
    public RetResult<String> currentIPCountry(HttpServletRequest request) {
        String clientIP = IPUtil.getClientIP(request);
        return RetResult.data(service.queryCountry(clientIP));
    }
}
