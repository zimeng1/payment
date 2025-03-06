package com.mc.payment.core.service.model.rsp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Marty
 * @since 2024/4/23 14:13
 */
@Data
public class DictCategoryDescRsp implements Serializable {

    private static final long serialVersionUID = -2778355619981917878L;

    @Schema(title = "分类编码")
    private String categoryCode;

    @Schema(title = "分类说明")
    private String categoryDesc;

}
