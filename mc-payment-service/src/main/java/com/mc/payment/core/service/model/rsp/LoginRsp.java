package com.mc.payment.core.service.model.rsp;

import com.mc.payment.common.base.BaseRsp;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author conor
 * @since 2024/01/26 10:02
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LoginRsp extends BaseRsp {
    private static final long serialVersionUID = 4835503989924269901L;

    public LoginRsp() {
    }

    public LoginRsp(String userAccount,String userName, String tokenName, String tokenValue, long tokenTimeout,List<String> permissions) {
        this.userAccount = userAccount;
        this.userName = userName;
        this.tokenName = tokenName;
        this.tokenValue = tokenValue;
        this.tokenTimeout = tokenTimeout;
        this.permissions = permissions;
    }

    /**
     * 登录账号
     */
    private String userAccount;

    /**
     * 用户名
     */
    private String userName;
    /**
     * token 名称
     */
    public String tokenName;
    /**
     * token 值
     */
    public String tokenValue;
    /**
     * token 剩余有效期（单位: 秒）
     */
    public long tokenTimeout;
    /**
     * 权限列表
     */
    public List<String> permissions;
}
