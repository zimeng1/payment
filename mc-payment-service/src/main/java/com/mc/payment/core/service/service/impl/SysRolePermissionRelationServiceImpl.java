package com.mc.payment.core.service.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.core.service.entity.SysRolePermissionRelationEntity;
import com.mc.payment.core.service.service.SysRolePermissionRelationService;
import com.mc.payment.core.service.mapper.SysRolePermissionRelationMapper;
import com.mc.payment.core.service.util.SimpleCache;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Conor
 * @description 针对表【mcp_sys_role_permission_relation(角色权限表)】的数据库操作Service实现
 * @createDate 2024-06-04 14:32:26
 */
@Service
public class SysRolePermissionRelationServiceImpl extends ServiceImpl<SysRolePermissionRelationMapper, SysRolePermissionRelationEntity>
        implements SysRolePermissionRelationService {
    public static final String PERMISSION_CODE_KEY = "PermissionCodeList#";
    public static final int EXPIRY_IN_SECONDS = 5 * 60;
    private final SimpleCache<String, List<String>> cache = new SimpleCache<>();

    /**
     * 查询拥有的权限码集合
     *
     * @param roleCode
     */
    @Override
    public List<String> queryPermissionCodeList(String roleCode) {
        List<String> list = cache.get(PERMISSION_CODE_KEY + roleCode);
        if (list != null) {
            return list;
        }
        list = queryPermissionCodeListFromDB(roleCode);
        cache.put(PERMISSION_CODE_KEY + roleCode, list, EXPIRY_IN_SECONDS);
        return list;
    }

    @Override
    public List<String> queryPermissionCodeListFromDB(String roleCode) {
        return list(Wrappers.lambdaQuery(SysRolePermissionRelationEntity.class)
                .eq(SysRolePermissionRelationEntity::getRoleCode, roleCode))
                .stream()
                .map(SysRolePermissionRelationEntity::getPermissionCode).toList();
    }

}




