package com.mc.payment.core.service.model.req;

import com.mc.payment.common.base.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author Marty
 * @since 2024/4/23 11:57
 */
@Data
@Schema(title = "字典创建实体")
public class DictSaveReq extends BaseReq {

    private static final long serialVersionUID = -752572728500682088L;

    @Schema(title = "父级编码,[结构:父级分类编码:父级编码]")
    private String parentCode;

    @NotBlank(message = "[编码]不能为空")
    @Length(max = 64, message = "[编码]长度不能超过64")
    @Schema(title = "编码")
    private String dictCode;

    @Length(max = 64, message = "[名称]长度不能超过64")
    @Schema(title = "名称")
    private String dictDesc;

    @NotBlank(message = "[分类编码]不能为空")
    @Length(max = 64, message = "[编码]长度不能超过64")
    @Schema(title = "分类编码")
    private String categoryCode;

    @Length(max = 64, message = "[分类说明]长度不能超过64")
    @Schema(title = "分类说明")
    private String categoryDesc;

    @Schema(title = "排序编号")
    private Integer sortNo = 0;

    @Schema(title = "附加属性")
    private String attributes;
}
