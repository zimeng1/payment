package com.mc.payment.core.service.config.token;

import cn.dev33.satoken.stp.StpInterface;
import com.mc.payment.core.service.service.IUserService;
import com.mc.payment.core.service.service.SysRolePermissionRelationService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自定义权限加载接口实现类
 */
@Component    // 保证此类被 SpringBoot 扫描，完成 Sa-Token 的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {

    private final IUserService userService;
    private final SysRolePermissionRelationService sysRolePermissionRelationService;

    public StpInterfaceImpl(IUserService userService, SysRolePermissionRelationService sysRolePermissionRelationService) {
        this.userService = userService;
        this.sysRolePermissionRelationService = sysRolePermissionRelationService;
    }


    /**
     * 返回一个账号所拥有的权限码集合
     * 参数解释：
     * <p>
     * loginId：账号id，即你在调用 StpUtil.login(id) 时写入的标识值。
     * loginType：账号体系标识，此处可以暂时忽略，在 [ 多账户认证 ] 章节下会对这个概念做详细的解释。
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        if (loginId == null) {
            return List.of();
        }
        String roleCode = userService.getRoleCode(loginId.toString());
        return sysRolePermissionRelationService.queryPermissionCodeList(roleCode);
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        if (loginId == null) {
            return List.of();
        }
        return List.of(userService.getRoleCode(loginId.toString()));
    }



}
