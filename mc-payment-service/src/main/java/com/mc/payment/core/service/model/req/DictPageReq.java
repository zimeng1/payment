package com.mc.payment.core.service.model.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mc.payment.core.service.base.BasePageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author Marty
 * @since 2024/4/23 17:41
 */
@Data
public class DictPageReq extends BasePageReq {

    private static final long serialVersionUID = -1345665519167107878L;

    @Schema(title = "分类编码")
    private String categoryCode;

    @Schema(title = "父级编码")
    private String parentCode;

    @Schema(title = "编码")
    private String dictCode;

    @Schema(title = "修改时间-开始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTimeStart;

    @Schema(title = "修改时间-结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTimeEnd;

}
