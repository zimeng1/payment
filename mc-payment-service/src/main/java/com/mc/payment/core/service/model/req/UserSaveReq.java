package com.mc.payment.core.service.model.req;

import com.mc.payment.common.base.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class UserSaveReq extends BaseReq {

    private static final long serialVersionUID = 683327237439689275L;

    @Schema(title = "登录账号")
    @NotBlank(message = "[登录账号]不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{5,20}$",
            message = "[登录账号]只能包含字母（大小写）、数字，并且长度在5到20个字符之间")
    private String userAccount;

    @Schema(title = "账号名称")
    @NotBlank(message = "[账号名称]不能为空")
    @Length(max = 20, message = "[账号名称]长度不能超过20")
    private String userName;

    @Schema(title = "密码", defaultValue = "Ll1*1234")
    @NotBlank(message = "[密码]不能为空")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,30}$",
            message = "密码必须包含至少一个大写字母、一个小写字母、一个数字和一个特殊字符（如 !@#$%^&*），并且长度在8到30个字符之间")
    private String password;

    @Schema(title = "邮箱")
    @NotBlank(message = "[邮箱]不能为空")
    @Length(max = 40, message = "[邮箱]长度不能超过40")
    @Email(message = "[邮箱]格式不正确")
    private String email;

    @Schema(title = "账号状态,[0:禁用,1:激活]")
    @NotNull(message = "[用户状态]不能为空")
    @Range(min = 0, max = 1, message = "[用户状态]必须为0或1, 0:禁用,1:激活")
    private Integer status;

    @Schema(title = "角色编码")
    @NotBlank(message = "[角色编码]不能为空")
    @Length(max = 20, message = "[角色编码]长度不能超过20")
    private String roleCode;

    @Schema(title = "所属商户关联类型,[0:全部商户,1:部分商户]")
    @NotNull(message = "[所属商户关联类型]不能为空")
    @Range(min = 0, max = 1, message = "[所属商户关联类型]必须为0或1, 0:禁用,1:激活")
    private Integer merchantRelType;

    @Schema(title = "所属商户id集合", description = "当[所属商户关联类型]为1时必填")
    private List<String> merchantIds;
}
