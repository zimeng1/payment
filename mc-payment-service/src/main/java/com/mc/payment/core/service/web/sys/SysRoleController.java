package com.mc.payment.core.service.web.sys;


import cn.dev33.satoken.annotation.SaCheckRole;
import com.mc.crm.common.dto.authority.SystemRoleDto;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.SysRoleEntity;
import com.mc.payment.core.service.manager.sys.SysRoleManager;
import com.mc.payment.core.service.model.req.SysRolePageReq;
import com.mc.payment.core.service.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Conor
 * @description 针对表【mcp_sys_role(角色表)】的数据库操作Controller实现
 * @createDate 2024-06-04 13:50:42
 */
@RequiredArgsConstructor
@Tag(name = "系统角色")
@RestController
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/sysRole")
public class SysRoleController {

    private final SysRoleManager manager;

    private final SysRoleService service;


    @Operation(summary = "列表查询", description = "查询所有数据")
    @PostMapping("/list")
    public RetResult<List<SysRoleEntity>> list() {
        return RetResult.data(service.list());
    }

    @Operation(summary = "查询", description = "查询详情")
    @GetMapping("/getById/{id}")
    public RetResult<SysRoleEntity> getById(@PathVariable("id") String id) {
        return RetResult.data(service.getById(id));
    }

    @SaCheckRole("admin")
    @Operation(summary = "新增", description = "新增数据,data:id")
    @PostMapping("/save")
    public RetResult<String> save(@RequestBody @Validated SysRoleEntity entity) {
        service.save(entity);
        return RetResult.data(entity.getId());
    }

    @Operation(summary = "分页查询", description = "author: conor")
    @PostMapping("page")
    public RetResult<BasePageRsp<SystemRoleDto>> page(@RequestBody SysRolePageReq req) {
        return RetResult.data(manager.page(req));
    }

}