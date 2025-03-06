package com.mc.payment.core.service.model.req;

import com.mc.payment.common.base.BaseReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author conor
 * @since 2024/01/25 11:31
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LoginReq extends BaseReq {
    private static final long serialVersionUID = 7514066660527458657L;
    /**
     * 登录账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String password;

}
