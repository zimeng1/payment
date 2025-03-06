package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * @author Marty
 * @since 2024/6/19 11:35
 */
@Data
@Schema(title = "汇率查询参数实体")
public class QueryAssetLastQuoteReq implements Serializable {
    private static final long serialVersionUID = 5687284091170306523L;

    @Schema(title = "钱包指定资产名称")
    @NotBlank(message = "[资产名称]不能为空")
    @Length(max = 20, message = "[资产名称]长度不能超过20")
    private String assetName;

}
