package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 权限表
 * @TableName mcp_sys_permission
 */
@TableName(value ="mcp_sys_permission")
@Data
public class SysPermissionEntity extends BaseNoLogicalDeleteEntity {
    /**
     * 权限码
     */
    @TableField(value = "permission_code")
    @Schema(title = "权限码")
    private String permissionCode;

    /**
     * 权限名称
     */
    @TableField(value = "permission_name")
    @Schema(title = "权限名称")
    private String permissionName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}