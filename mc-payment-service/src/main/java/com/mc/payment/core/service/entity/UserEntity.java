package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mc.payment.core.service.base.BaseEntity;
import com.mc.payment.core.service.model.req.UserSaveReq;
import com.mc.payment.core.service.model.req.UserUpdateReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 系统用户表
 * </p>
 *
 * @author conor
 * @since 2024-01-25 10:12:50
 */
@Getter
@Setter
@TableName("mcp_user")
@Schema(title = "UserEntity对象", description = "系统账号表")
public class UserEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(title = "登录账号")
    @TableField("user_account")
    private String userAccount;

    @Schema(title = "账号名称")
    @TableField("user_name")
    private String userName;

    @JsonIgnore
    @Schema(title = "密码哈希值")
    @TableField("password_hash")
    private String passwordHash;

    @Schema(title = "邮箱")
    @TableField("email")
    private String email;

    @Schema(title = "账号状态,[0:禁用,1:激活]")
    @TableField("status")
    private Integer status;

    @Schema(title = "最后登录ip")
    @TableField("last_login_ip")
    private String lastLoginIp;

    @Schema(title = "最后登录时间")
    @TableField("last_login_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastLoginTime;

    @Schema(title = "角色编码")
    @TableField("role_code")
    private String roleCode;

    @JsonIgnore
    @Schema(title = "最近3次的历史密码哈希值,英文逗号分隔")
    @TableField("history_password_hash")
    private String historyPasswordHash;

    @Schema(title = "所属商户关联类型,[0:全部商户,1:部分商户]")
    @NotNull(message = "[所属商户关联类型]不能为空")
    @Range(min = 0, max = 1, message = "[所属商户关联类型]必须为0或1, 0:全部商户,1:部分商户")
    @TableField("merchant_rel_type")
    private Integer merchantRelType;

    public static UserEntity valueOf(UserSaveReq req) {
        UserEntity entity = new UserEntity();
        entity.setUserName(req.getUserName());
        entity.setUserAccount(req.getUserAccount());
        entity.setEmail(req.getEmail());
        entity.setStatus(req.getStatus());
        entity.setRoleCode(req.getRoleCode());
        entity.setMerchantRelType(req.getMerchantRelType());
        return entity;
    }

    public static UserEntity valueOf(UserUpdateReq req) {
        UserEntity entity = new UserEntity();
        entity.setUserName(req.getUserName());
        entity.setEmail(req.getEmail());
        entity.setStatus(req.getStatus());
        entity.setRoleCode(req.getRoleCode());
        entity.setMerchantRelType(req.getMerchantRelType());
        entity.setId(req.getId());
        return entity;
    }

    // 新增方法，用于更新密码历史记录
    public static String updatePasswordHistory(String historyPasswordHash, String newPasswordHash) {
        // 将新密码哈希添加到历史记录的最前面
        String[] passwordHistory = historyPasswordHash.split(",");
        List<String> list = new ArrayList<>(Arrays.asList(newPasswordHash));
        list.addAll(Arrays.asList(passwordHistory));

        // 仅保留最近的三次密码历史
        if (list.size() > 3) {
            list.subList(3, list.size()).clear();
        }

        // 将更新后的密码历史转换回字符串形式
        return String.join(",", list);
    }
}
