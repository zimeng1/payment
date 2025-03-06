package com.mc.payment.core.service.model.req.platform;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Data
public class PlatformAssetUpdateReq {
    @Schema(title = "id")
    @NotBlank(message = "[id]不能为空")
    private String id;

    /**
     * 资产状态,[0:禁用,1:激活]
     */
    @Schema(title = "资产状态,[0:禁用,1:激活]")
    @NotNull(message = "[资产状态]不能为空")
    @Range(min = 0, max = 1, message = "[资产状态]必须为[0:禁用,1:激活]")
    private Integer status;

    /**
     * 图标数据,[base64编码]
     */
    @Schema(title = "图标数据,[base64编码]")
    @NotBlank(message = "[图标数据]不能为空")
    @Length(max = 20000, message = "[图标数据]长度不能超过20000")
    private String iconData;
}
