package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色权限表
 * @TableName mcp_sys_role_permission_relation
 */
@TableName(value ="mcp_sys_role_permission_relation")
@Data
public class SysRolePermissionRelationEntity extends BaseNoLogicalDeleteEntity {
    /**
     * 角色码
     */
    @TableField(value = "role_code")
    @Schema(title = "角色码")
    private String roleCode;

    /**
     * 权限码
     */
    @TableField(value = "permission_code")
    @Schema(title = "权限码")
    private String permissionCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}