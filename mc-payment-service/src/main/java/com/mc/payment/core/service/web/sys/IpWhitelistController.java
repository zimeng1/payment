package com.mc.payment.core.service.web.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.IpWhitelistEntity;
import com.mc.payment.core.service.model.req.IpWhitelistPageReq;
import com.mc.payment.core.service.model.req.IpWhitelistSaveReq;
import com.mc.payment.core.service.model.req.IpWhitelistUpdateReq;
import com.mc.payment.core.service.service.IpWhitelistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Conor
 * @since 2024/6/3 下午6:52
 */
@Tag(name = "IP白名单")
@RestController
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/ipWhitelist")
public class IpWhitelistController {

    private final IpWhitelistService service;

    public IpWhitelistController(IpWhitelistService ipWhitelistService) {
        this.service = ipWhitelistService;
    }

    @SaCheckPermission("ipWhitelist-query")
    @Operation(summary = "分页查询", description = "分页查询")
    @PostMapping("/page")
    public RetResult<BasePageRsp<IpWhitelistEntity>> page(@RequestBody IpWhitelistPageReq req) {
        return RetResult.data(service.page(req));
    }


    @SaCheckPermission("ipWhitelist-query")
    @Operation(summary = "查询", description = "查询详情")
    @GetMapping("/getById/{id}")
    public RetResult<IpWhitelistEntity> getById(@PathVariable("id") String id) {
        return RetResult.data(service.getById(id));
    }

    @SaCheckPermission("ipWhitelist-add")
    @Operation(summary = "新增", description = "新增数据,data:id")
    @PostMapping("/save")
    public RetResult<String> save(@RequestBody @Validated IpWhitelistSaveReq req) {
        return service.save(req);
    }

    @SaCheckPermission("ipWhitelist-update")
    @Operation(summary = "修改", description = "修改数据")
    @PostMapping("/updateById")
    public RetResult<Boolean> updateById(@RequestBody @Validated IpWhitelistUpdateReq req) {
        return service.updateById(req);
    }
}
