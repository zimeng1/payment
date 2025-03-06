package com.mc.payment.core.service.service.impl;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.EmailTemplateEntity;
import com.mc.payment.core.service.entity.MerchantEntity;
import com.mc.payment.core.service.entity.UserEntity;
import com.mc.payment.core.service.mapper.UserMapper;
import com.mc.payment.core.service.model.dto.EmailJobParamDto;
import com.mc.payment.core.service.model.dto.MerchantSimpleVo;
import com.mc.payment.core.service.model.enums.EmailContentEnum;
import com.mc.payment.core.service.model.enums.JobPlanHandlerEnum;
import com.mc.payment.core.service.model.req.*;
import com.mc.payment.core.service.model.rsp.LoginRsp;
import com.mc.payment.core.service.model.rsp.UserGetRsp;
import com.mc.payment.core.service.model.rsp.UserPageRsp;
import com.mc.payment.core.service.service.*;
import com.mc.payment.core.service.util.MonitorUtil;
import com.mc.payment.core.service.util.SimpleCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.groovy.util.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author conor
 * @since 2024-01-25 10:12:50
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements IUserService {
    /**
     * 密码盐
     */
    private static final String PASSWORD_SALT = "08ws*VZQ";
    public static final String ROLE_CODE_KEY = "RoleCodeList#";
    public static final int EXPIRY_IN_SECONDS = 5 * 60;
    private final SimpleCache<String, String> cache = new SimpleCache<>();


    private final IUserMerchantRelationService userMerchantRelationService;
    private final IMerchantService merchantService;
    private final SysRolePermissionRelationService sysRolePermissionRelationService;
    private final IEmailTemplateService emailTemplateService;

    private final IJobPlanService jobPlanService;

    public UserServiceImpl(IUserMerchantRelationService userMerchantRelationService, IMerchantService merchantService, SysRolePermissionRelationService sysRolePermissionRelationService, IEmailTemplateService emailTemplateService, IJobPlanService jobPlanService) {
        this.userMerchantRelationService = userMerchantRelationService;
        this.merchantService = merchantService;
        this.sysRolePermissionRelationService = sysRolePermissionRelationService;
        this.emailTemplateService = emailTemplateService;
        this.jobPlanService = jobPlanService;
    }


    @Override
    public UserEntity getOne(String userAccount, String password) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUserAccount, userAccount).eq(UserEntity::getPasswordHash, password);
        return getOne(queryWrapper);
    }

    @Override
    public UserEntity getOne(String userAccount) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUserAccount, userAccount);
        return getOne(queryWrapper);
    }

    @Override
    public RetResult<LoginRsp> login(LoginReq loginParam, String ipAddress) {
        UserEntity userEntity = this.getOne(loginParam.getUserAccount());
        if (userEntity == null) {
            return RetResult.error("用户不存在");
        }
        String encryptPassword = SecureUtil.sha256(loginParam.getPassword() + PASSWORD_SALT);
        if (!userEntity.getPasswordHash().equals(encryptPassword)) {
            return RetResult.error("密码错误");
        }
        if (userEntity.getStatus() != 1) {
            return RetResult.error("账号已被禁用");
        }
        String userMerchantIds = "";
        // 获取:当前账号所有拥有的商户id
        if (userEntity.getMerchantRelType() == 0) {
            userMerchantIds = "*";
        } else {
            userMerchantIds = userMerchantRelationService.queryMerchantIds(userEntity.getId()).stream().collect(Collectors.joining(","));
        }

        StpUtil.login(userEntity.getId(), new SaLoginModel().setExtraData(Maps.of(
                "userName", userEntity.getUserName(),
                "userMerchantIds", userMerchantIds,
                "userId", userEntity.getId())));
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        // 获取：当前账号所拥有的权限集合
        List<String> permissionList = StpUtil.getPermissionList();

        this.update(Wrappers.lambdaUpdate(UserEntity.class).eq(BaseNoLogicalDeleteEntity::getId, userEntity.getId())
                .set(UserEntity::getLastLoginTime, new Date())
                .set(UserEntity::getLastLoginIp, ipAddress));

        MonitorUtil.loginCounter(userEntity.getUserAccount());
        return RetResult.data(new LoginRsp(userEntity.getUserAccount(),
                userEntity.getUserName(),
                tokenInfo.getTokenName(),
                tokenInfo.getTokenValue(),
                tokenInfo.getTokenTimeout(),
                permissionList));
    }

    @Override
    public BasePageRsp<UserPageRsp> page(UserPageReq req) {
        Page<UserPageRsp> page = new Page<>(req.getCurrent(), req.getSize());
        page = (Page<UserPageRsp>) baseMapper.page(page, req);
        return BasePageRsp.valueOf(page);
    }

    @Override
    @Transactional
    public RetResult<String> save(UserSaveReq req) {
        if (req.getMerchantRelType() == 1 && CollUtil.isEmpty(req.getMerchantIds())) {
            return RetResult.error("商户关联类型为部分商户时,必须选择商户");
        }
        UserEntity userEntity = getOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getUserAccount, req.getUserAccount()));
        if (userEntity != null) {
            return RetResult.error("该登录账号已存在，请修改重试 ");
        }
        //先加密后保存
        String encryptPassword = SecureUtil.sha256(req.getPassword() + PASSWORD_SALT);
        UserEntity saveUser = UserEntity.valueOf(req);
        saveUser.setPasswordHash(encryptPassword);
        saveUser.setHistoryPasswordHash(encryptPassword);
        boolean b = this.save(saveUser);
        if (b) {
            userMerchantRelationService.updateRelation(saveUser.getId(), req.getMerchantIds());
        }
        return RetResult.data(saveUser.getId());
    }

    @Override
    @Transactional
    public RetResult<Boolean> updateById(UserUpdateReq req) {
        if (req.getMerchantRelType() == 1 && CollUtil.isEmpty(req.getMerchantIds())) {
            return RetResult.error("商户关联类型为部分商户时,必须选择商户");
        }
        UserEntity entity = getById(req.getId());
        if (entity == null) {
            return RetResult.error("该账户不存在");
        }
        UserEntity userEntity = UserEntity.valueOf(req);
        boolean b = this.updateById(userEntity);
        if (b) {
            userMerchantRelationService.updateRelation(userEntity.getId(), req.getMerchantIds());
        }
        return RetResult.data(b);
    }

    @Override
    public RetResult<Boolean> updatePassword(UserUpdatePasswordReq req) {
        UserEntity entity = getById(req.getId());
        if (entity == null) {
            return RetResult.error("该账户不存在");
        }
        //旧密码加密
        String encryptOldPassword = SecureUtil.sha256(req.getOldPassword() + PASSWORD_SALT);
        //新密码加密
        String encryptPassword = SecureUtil.sha256(req.getPassword() + PASSWORD_SALT);
        //判断旧密码是否正确
        if (!entity.getPasswordHash().equals(encryptOldPassword)) {
            return RetResult.error("旧密码不正确");
        }
        // 判断新密码是否和历史密码相同
        if (entity.getHistoryPasswordHash().contains(encryptPassword)) {
            return RetResult.error("新密码不能与最近3次历史密码相同");
        }
        boolean b = this.update(Wrappers.lambdaUpdate(UserEntity.class).eq(BaseNoLogicalDeleteEntity::getId, req.getId())
                .set(UserEntity::getPasswordHash, encryptPassword)
                .set(UserEntity::getHistoryPasswordHash, UserEntity.updatePasswordHistory(entity.getHistoryPasswordHash(), encryptPassword)));
        return RetResult.data(b);
    }


    /**
     * 重置账号密码,返回新密码
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RetResult<String> resetPassword(String id) {
        UserEntity entity = getById(id);
        if (entity == null) {
            return RetResult.error("该账户不存在");
        }
        if (StringUtils.isBlank(entity.getEmail())) {
            return RetResult.error("该账户未绑定邮箱,无法重置密码");
        }

        String upperCaseLetter = RandomUtil.randomString("ABCDEFGHIJKLMNOPQRSTUVWXYZ", 1);
        String lowerCaseLetter = RandomUtil.randomString("abcdefghijklmnopqrstuvwxyz", 1);
        String digit = RandomUtil.randomString("0123456789", 1);
        String specialCharacter = RandomUtil.randomString("!@#%^&", 1);

        String otherCharacters = RandomUtil.randomString("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#%^&", RandomUtil.randomInt(4, 27));

        String newPassword = upperCaseLetter + lowerCaseLetter + digit + specialCharacter + otherCharacters;
        List<String> passwordList = Arrays.asList(newPassword.split(""));
        // 对元素洗牌
        Collections.shuffle(passwordList);
        newPassword = String.join("", passwordList);
        //加密后保存
        String encryptPassword = SecureUtil.sha256(newPassword + PASSWORD_SALT);
        boolean b = this.update(Wrappers.lambdaUpdate(UserEntity.class).eq(BaseNoLogicalDeleteEntity::getId, id)
                .set(UserEntity::getPasswordHash, encryptPassword)
                .set(UserEntity::getHistoryPasswordHash, UserEntity.updatePasswordHistory(entity.getHistoryPasswordHash(), encryptPassword)));

        //这里需要发送邮件通知用户
        EmailTemplateEntity emailTemplateEntity = emailTemplateService.getEmailTemplateCacheMap().get(EmailContentEnum.RESET_PASSWORD.getCode());

        EmailJobParamDto emailJobParamDto = new EmailJobParamDto(
                entity.getEmail(),
                EmailContentEnum.RESET_PASSWORD.getSubject(),
                emailTemplateEntity.getContent().replaceFirst("%s", entity.getUserName()).replaceFirst("%s", newPassword));
        jobPlanService.addJobPlan(JobPlanHandlerEnum.SEND_EMAIL, emailJobParamDto);

        return b ? RetResult.data(newPassword) : RetResult.error("重置密码失败");
    }

    @Override
    public RetResult<UserGetRsp> getUserGetRsp(String id) {
        UserEntity userEntity = this.getById(id);
        if (userEntity == null) {
            return RetResult.error("该账户不存在");
        }
        List<MerchantSimpleVo> merchantSimpleVoList = List.of();
        List<MerchantEntity> merchantEntityList = List.of();
        if (userEntity.getMerchantRelType() == 0) {
            merchantEntityList = merchantService.list();
        } else {
            List<String> merchantIds = userMerchantRelationService.queryMerchantIds(id);
            if (CollUtil.isNotEmpty(merchantIds)) {
                merchantEntityList = merchantService.list(Wrappers.lambdaQuery(MerchantEntity.class).in(MerchantEntity::getId, merchantIds));
            }
        }
        merchantSimpleVoList = CollUtil.isNotEmpty(merchantEntityList) ? merchantEntityList.stream().map(MerchantSimpleVo::valueOf)
                .collect(Collectors.toList()) : merchantSimpleVoList;

        UserGetRsp userGetRsp = UserGetRsp.valueOf(userEntity);
        userGetRsp.setMerchantList(merchantSimpleVoList);
        return RetResult.data(userGetRsp);
    }

    /**
     * 获取用户的角色码
     *
     * @param id
     * @return
     */
    @Override
    public String getRoleCode(String id) {
        if (StrUtil.isEmpty(id)) {
            return "";
        }
        String roleCode = cache.get(ROLE_CODE_KEY + id);
        if (StrUtil.isNotBlank(roleCode)) {
            return roleCode;
        }
        roleCode = getRoleCodeFromDB(id);
        cache.put(ROLE_CODE_KEY + id, roleCode, EXPIRY_IN_SECONDS);
        return roleCode;
    }

    @Override
    public String getRoleCodeFromDB(String id) {
        UserEntity userEntity = getById(id);
        if (userEntity != null) {
            return userEntity.getRoleCode();
        }
        return "";
    }

}
