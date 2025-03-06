package com.mc.payment.core.service.model.rsp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Marty
 * @since 2024/4/23 14:13
 */
@Data
public class DictRsp implements Serializable {

    private static final long serialVersionUID = -2778355619981917878L;

    @Schema(title = "父级编码,[结构:父级分类编码:父级编码]")
    private String parentCode;

    @Schema(title = "编码")
    private String dictCode;

    @Schema(title = "名称")
    private String dictDesc;

    @Schema(title = "分类编码")
    private String categoryCode;

    @Schema(title = "分类说明")
    private String categoryDesc;

    @Schema(title = "附加属性")
    private String attributes;

}
