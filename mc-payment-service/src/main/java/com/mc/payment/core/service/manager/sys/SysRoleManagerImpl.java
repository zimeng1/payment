package com.mc.payment.core.service.manager.sys;

import com.mc.crm.api.feign.AuthorityFeign;
import com.mc.crm.common.dto.RetResult;
import com.mc.crm.common.dto.authority.*;
import com.mc.crm.common.page.PageResult;
import com.mc.payment.common.exception.BusinessException;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.model.req.SysRolePageReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class SysRoleManagerImpl implements SysRoleManager {
    /**
     * payment的子系统编号
     */
    private static final String SUB_SYSTEM_CODE = "000005";
    private final AuthorityFeign authorityFeign;
    private final AppConfig appConfig;

    @Override
    public BasePageRsp<SystemRoleDto> page(SysRolePageReq req) {

        RetResult<PageResult<List<SystemRoleDto>>> apiResult = authorityFeign.rolePage(req.convert(), appConfig.getCrmToken());

        if (!apiResult.isSuccess()) {
            throw new BusinessException(apiResult.getMsg());
        }

        PageResult<List<SystemRoleDto>> pageResult = apiResult.getData();

        BasePageRsp<SystemRoleDto> basePageRsp = new BasePageRsp<>();
        basePageRsp.setTotal(pageResult.getTotal());
        basePageRsp.setRecords(pageResult.getResult());

        basePageRsp.setSize(req.getSize());
        basePageRsp.setCurrent(pageResult.getCurrPage());
        basePageRsp.setPages(pageResult.getTotalPage());


        return basePageRsp;
    }

    /**
     * 新增角色
     *
     * @param req
     */
    @Override
    public void addSystemRole(SystemRoleReq req) {
        req.setSubSystemCode(SUB_SYSTEM_CODE);
        RetResult<Void> retResult = authorityFeign.addSystemRole(req, appConfig.getCrmToken());
        if (!retResult.isSuccess()) {
            throw new BusinessException(retResult.getMsg());
        }
    }

    /**
     * 更新角色
     *
     * @param req
     */
    @Override
    public void updateSystemRole(SystemRoleModifyReq req) {
        req.setSubSystemCode(SUB_SYSTEM_CODE);
        RetResult<Void> retResult = authorityFeign.updateSystemRole(req, appConfig.getCrmToken());
        if (!retResult.isSuccess()) {
            throw new BusinessException(retResult.getMsg());
        }
    }

    /**
     * 查看角色名称是否存在
     *
     * @param roleName
     * @return
     */
    @Override
    public Boolean existRoleName(String roleName) {
        RoleReq req = new RoleReq();
        req.setSubSystemCode(SUB_SYSTEM_CODE);
        req.setRoleName(roleName);
        RetResult<Boolean> retResult = authorityFeign.existRoleName(req, appConfig.getCrmToken());
        if (!retResult.isSuccess()) {
            throw new BusinessException(retResult.getMsg());
        }
        return retResult.getData();
    }

    /**
     * 角色查看
     *
     * @param req
     * @return
     */
    @Override
    public SystemRoleDto roleDetail(RoleReq req) {
        req.setSubSystemCode(SUB_SYSTEM_CODE);
        RetResult<SystemRoleDto> retResult = authorityFeign.roleDetail(req, appConfig.getCrmToken());
        if (!retResult.isSuccess()) {
            throw new BusinessException(retResult.getMsg());
        }
        return retResult.getData();
    }

    /**
     * 角色关联的菜单查询，树状结构"
     *
     * @param req
     * @return
     */
    @Override
    public List<SystemMenuDto> menuList(RoleReq req) {
        req.setSubSystemCode(SUB_SYSTEM_CODE);
        RetResult<List<SystemMenuDto>> retResult = authorityFeign.menuList(req, appConfig.getCrmToken());
        if (!retResult.isSuccess()) {
            throw new BusinessException(retResult.getMsg());
        }
        return retResult.getData();
    }

    /**
     * 整个系统的所有菜单权限项
     *
     * @param req
     * @return
     */
    @Override
    public List<SystemMenuDto> menuTreeList(SystemMenuReq req) {
        req.setSubSystemCode(SUB_SYSTEM_CODE);
        RetResult<List<SystemMenuDto>> retResult = authorityFeign.menuTreeList(req, appConfig.getCrmToken());
        if (!retResult.isSuccess()) {
            throw new BusinessException(retResult.getMsg());
        }
        return retResult.getData();
    }
}
