package com.mc.payment.core.service.manager.sys;

import com.mc.crm.common.dto.authority.*;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.model.req.SysRolePageReq;

import java.util.List;

public interface SysRoleManager {
    /**
     * 角色列表查询
     *
     * @param req
     * @return
     */
    BasePageRsp<SystemRoleDto> page(SysRolePageReq req);

    /**
     * 新增角色
     *
     * @param req
     */
    void addSystemRole(SystemRoleReq req);

    /**
     * 更新角色
     *
     * @param req
     */
    void updateSystemRole(SystemRoleModifyReq req);

    /**
     * 查看角色名称是否存在
     *
     * @param roleName
     * @return
     */
    Boolean existRoleName(String roleName);

    /**
     * 角色查看
     *
     * @param roleReq
     * @return
     */
    SystemRoleDto roleDetail(RoleReq roleReq);

    /**
     * 角色关联的菜单查询，树状结构"
     *
     * @param roleReq
     * @return
     */
    List<SystemMenuDto> menuList(RoleReq roleReq);

    /**
     * 整个系统的所有菜单权限项
     *
     * @param menuReq
     * @return
     */
    List<SystemMenuDto> menuTreeList(SystemMenuReq menuReq);
}
