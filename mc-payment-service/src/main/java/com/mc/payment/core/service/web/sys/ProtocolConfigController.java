package com.mc.payment.core.service.web.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.ProtocolConfigEntity;
import com.mc.payment.core.service.model.req.ProtocolConfigReq;
import com.mc.payment.core.service.service.ProtocolConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * ProtocolConfigController
 *
 * @author GZM
 * @since 2024/10/14 上午10:54
 */
@Slf4j
@Tag(name = "加密货币正则配置")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/protocol/config")
public class ProtocolConfigController {

    private final ProtocolConfigService protocolConfigService;

    // 根据条件查询数据
    @SaCheckPermission("protocol-config-query")
    @Operation(summary = "查询加密货币正则配置", description = "查询加密货币正则配置")
    @PostMapping("/page")
    public RetResult<BasePageRsp<ProtocolConfigEntity>> page(@RequestBody ProtocolConfigReq req) {
        return RetResult.data(protocolConfigService.page(req));
    }

    // 新增数据
    @SaCheckPermission("protocol-config-add")
    @Operation(summary = "新增加密货币正则配置", description = "新增加密货币正则配置")
    @PostMapping("/add")
    public RetResult<Boolean> add(@RequestBody ProtocolConfigReq req) {
        return RetResult.data(protocolConfigService.add(req));
    }

    // 根据 ID 删除数据
    @SaCheckPermission("protocol-config-remove")
    @Operation(summary = "删除加密货币正则配置", description = "删除加密货币正则配置")
    @GetMapping("/delete/{id}")
    public RetResult<Boolean> delete(@PathVariable("id") String id) {
        return RetResult.data(protocolConfigService.delete(id));
    }

    // 更新数据
    @SaCheckPermission("protocol-config-update")
    @Operation(summary = "更新加密货币正则配置", description = "更新加密货币正则配置")
    @PostMapping("/update")
    public RetResult<Boolean> update(@RequestBody ProtocolConfigReq req) {
        return RetResult.data(protocolConfigService.update(req));
    }

}
