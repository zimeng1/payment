package com.mc.payment.core.service.model.req;

import com.mc.payment.common.base.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.util.List;

/**
 * @author Marty
 * @since 2024/5/7 15:00
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserUpdateReq extends BaseReq {

    private static final long serialVersionUID = -2159228106641271846L;

    @Schema(title = "账户id")
    @NotBlank(message = "[账户id]不能为空")
    private String id;

    @Schema(title = "用户名")
    @NotBlank(message = "[用户名]不能为空")
    @Length(max = 20, message = "[用户名]长度不能超过20")
    private String userName;

    @Schema(title = "邮箱")
    @NotBlank(message = "[邮箱]不能为空")
    @Length(max = 40, message = "[邮箱]长度不能超过40")
    @Email(message = "[邮箱]格式不正确")
    private String email;

    @Schema(title = "账号状态,[0:禁用,1:激活]")
    @NotNull(message = "[用户状态]不能为空")
    @Range(min = 0, max = 1, message = "[用户状态]必须为0或1, 0:正常, 1:禁用")
    private Integer status;

    @Schema(title = "角色编码")
    @Length(max = 20, message = "[角色编码]长度不能超过20")
    @NotBlank(message = "[角色编码]不能为空")
    private String roleCode;

    @Schema(title = "所属商户关联类型,[0:全部商户,1:部分商户]")
    @NotNull(message = "[用户状态]不能为空")
    @Range(min = 0, max = 1, message = "[用户状态]必须为0或1, 0:禁用,1:激活")
    private Integer merchantRelType;

    @Schema(title = "所属商户id集合", description = "当[所属商户关联类型]为1时必填")
    private List<String> merchantIds;
}
