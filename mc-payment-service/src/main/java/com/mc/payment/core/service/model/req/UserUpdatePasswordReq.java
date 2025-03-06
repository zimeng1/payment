package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * @author Conor
 * @since 2024/6/4 上午10:48
 */
@Data
public class UserUpdatePasswordReq {

    @Schema(title = "账户id")
    @NotBlank(message = "[账户id]不能为空")
    private String id;

    @Schema(title = "原密码")
    @NotBlank(message = "[原密码]不能为空")
    private String oldPassword;

    @Schema(title = "新密码")
    @NotBlank(message = "[新密码]不能为空")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,30}$",
            message = "密码必须包含至少一个大写字母、一个小写字母、一个数字和一个特殊字符（如 !@#$%^&*），并且长度在8到30个字符之间")
    private String password;
}
