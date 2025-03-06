package com.mc.payment.core.service.model.rsp;

import com.mc.payment.core.service.entity.UserEntity;
import com.mc.payment.core.service.model.dto.MerchantSimpleVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.util.List;

/**
 * @author Conor
 * @since 2024/6/3 下午5:45
 */
@Data
public class UserGetRsp extends UserEntity {

    @Serial
    private static final long serialVersionUID = 5313421174833416715L;

    @Schema(title = "所属商户", description = "key:商户id,value:商户名称")
    private List<MerchantSimpleVo> merchantList;



    public static UserGetRsp valueOf(UserEntity userEntity) {
        UserGetRsp userGetRsp = new UserGetRsp();
        userGetRsp.setUserAccount(userEntity.getUserAccount());
        userGetRsp.setUserName(userEntity.getUserName());
        userGetRsp.setPasswordHash(userEntity.getPasswordHash());
        userGetRsp.setEmail(userEntity.getEmail());
        userGetRsp.setStatus(userEntity.getStatus());
        userGetRsp.setLastLoginIp(userEntity.getLastLoginIp());
        userGetRsp.setLastLoginTime(userEntity.getLastLoginTime());
        userGetRsp.setRoleCode(userEntity.getRoleCode());
        userGetRsp.setHistoryPasswordHash(userEntity.getHistoryPasswordHash());
        userGetRsp.setMerchantRelType(userEntity.getMerchantRelType());
        userGetRsp.setDeleted(userEntity.getDeleted());
        userGetRsp.setId(userEntity.getId());
        userGetRsp.setCreateBy(userEntity.getCreateBy());
        userGetRsp.setCreateTime(userEntity.getCreateTime());
        userGetRsp.setUpdateBy(userEntity.getUpdateBy());
        userGetRsp.setUpdateTime(userEntity.getUpdateTime());
        return userGetRsp;
    }
}
