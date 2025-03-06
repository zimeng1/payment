package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.model.req.*;
import com.mc.payment.core.service.model.rsp.LoginRsp;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.UserEntity;
import com.mc.payment.core.service.model.rsp.UserGetRsp;
import com.mc.payment.core.service.model.rsp.UserPageRsp;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author conor
 * @since 2024-01-25 10:12:50
 */
public interface IUserService extends IService<UserEntity> {

    UserEntity getOne(String userAccount, String password);

    UserEntity getOne(String userAccount);


    RetResult<LoginRsp> login(LoginReq loginParam, String ipAddress);

    BasePageRsp<UserPageRsp> page(UserPageReq req);

    RetResult<String> save(UserSaveReq req);

    RetResult<Boolean> updateById(UserUpdateReq req);

    RetResult<Boolean> updatePassword(UserUpdatePasswordReq req);

    /**
     * 重置账号密码,返回新密码
     *
     * @param id
     * @return
     */
    RetResult<String> resetPassword(String id);

    RetResult<UserGetRsp> getUserGetRsp(String id);

    /**
     * 获取用户的角色码
     * @param id
     * @return
     */
    String getRoleCode(String id);

    String getRoleCodeFromDB(String id);

}
