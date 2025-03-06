package com.mc.payment.core.service.model.req.platform;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 和 PlatformAssetUpdateReq 参数一样所以直接继承
 *
 * @author Conor
 * @since 2024-11-04 17:28:20.287
 */
@Data
public class CryptoProtocolUpdateReq extends PlatformAssetUpdateReq {

    @Schema(title = "正则表达式")
    @NotBlank(message = "[正则表达式]不能为空")
    @Length(max = 255, message = "[正则表达式]长度不能超过255")
    private String regularExpression;
}
