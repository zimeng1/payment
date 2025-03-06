package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.core.service.entity.SysRolePermissionRelationEntity;

import java.util.List;

/**
* @author Conor
* @description 针对表【mcp_sys_role_permission_relation(角色权限表)】的数据库操作Service
* @createDate 2024-06-04 14:32:26
*/
public interface SysRolePermissionRelationService extends IService<SysRolePermissionRelationEntity> {

    /**
     * 查询拥有的权限码集合
     */
    List<String> queryPermissionCodeList(String roleCode);
    List<String> queryPermissionCodeListFromDB(String roleCode);

}
