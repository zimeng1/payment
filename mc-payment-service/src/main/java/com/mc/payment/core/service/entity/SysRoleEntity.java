package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色表
 * @TableName mcp_sys_role
 */
@TableName(value ="mcp_sys_role")
@Data
public class SysRoleEntity extends BaseNoLogicalDeleteEntity {
    /**
     * 角色码
     */
    @TableField(value = "role_code")
    @Schema(title = "角色码")
    private String roleCode;

    /**
     * 角色名称
     */
    @TableField(value = "role_name")
    @Schema(title = "角色名称")
    private String roleName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}