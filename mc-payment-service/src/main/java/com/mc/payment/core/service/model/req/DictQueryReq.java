package com.mc.payment.core.service.model.req;

import com.mc.payment.core.service.base.BasePageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Marty
 * @since 2024/4/23 11:57
 */
@Data
@Schema(title = "字典查询-查询参数实体")
public class DictQueryReq extends BasePageReq {

    private static final long serialVersionUID = -1462791052838987981L;

//    @NotBlank(message = "[分类编码]不能为空")
    @Schema(title = "分类编码")
    private String categoryCode;

    @Schema(title = "父级编码,[结构:父级分类编码:父级编码]")
    private String parentCode;

    @Schema(title = "编码")
    private String dictCode;

}
