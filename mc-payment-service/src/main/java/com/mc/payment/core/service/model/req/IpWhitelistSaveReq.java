package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

/**
 * @author Conor
 * @since 2024/6/3 下午6:56
 */
@Data
public class IpWhitelistSaveReq {

    @Schema(description = "IP地址")
    @NotBlank(message = "[IP地址]不能为空")
    @Length(max = 50, message = "[IP地址备注]长度不能超过50")
    private String ipAddr;

    @Schema(description = "IP地址备注")
    @Length(max = 64, message = "[IP地址备注]长度不能超过64")
    private String remark;

    @Schema(description = "IP状态,[0:禁用,1:激活]")
    @NotNull(message = "[IP状态]不能为空")
    @Range(min = 0, max = 1, message = "[IP状态]必须为0或1, 0:禁用,1:激活")
    private Integer status;
}
