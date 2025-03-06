package com.mc.payment.core.service.web.sys;


import cn.dev33.satoken.annotation.SaCheckRole;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.core.service.entity.SysPermissionEntity;
import com.mc.payment.core.service.service.SysPermissionService;
import com.mc.payment.core.service.service.SysRolePermissionRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Conor
 * @description 针对表【mcp_sys_resource(资源表)】的数据库操作Controller实现
 * @createDate 2024-06-04 13:50:42
 */
@Tag(name = "系统权限")
@RestController
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/sysPermission")
public class SysPermissionController {

    private final SysPermissionService service;
    private final SysRolePermissionRelationService sysRolePermissionRelationService;

    public SysPermissionController(SysPermissionService service, SysRolePermissionRelationService sysRolePermissionRelationService) {
        this.service = service;
        this.sysRolePermissionRelationService = sysRolePermissionRelationService;
    }

    @Operation(summary = "列表查询", description = "查询所有数据")
    @GetMapping("/list")
    public RetResult<List<SysPermissionEntity>> list() {
        return RetResult.data(service.list());
    }

    @Operation(summary = "查询角色拥有的权限码集合", description = "查询所有数据,roleCode:角色编码")
    @GetMapping("/queryPermissionCodeList/{roleCode}")
    public RetResult<List<String>> queryPermissionCodeList(@PathVariable("roleCode") String roleCode) {
        return RetResult.data(sysRolePermissionRelationService.queryPermissionCodeList(roleCode));
    }

    @Operation(summary = "查询", description = "查询详情")
    @GetMapping("/getById/{id}")
    public RetResult<SysPermissionEntity> getById(@PathVariable("id") String id) {
        return RetResult.data(service.getById(id));
    }

    @SaCheckRole("admin")
    @Operation(summary = "新增", description = "新增数据,data:id")
    @PostMapping("/save")
    public RetResult<String> save(@RequestBody @Validated SysPermissionEntity entity) {
        service.save(entity);
        return RetResult.data(entity.getId());
    }
}