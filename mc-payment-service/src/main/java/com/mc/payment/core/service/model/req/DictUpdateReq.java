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
public class DictUpdateReq extends BaseReq {

    private static final long serialVersionUID = -9221382334443516997L;

    @NotBlank(message = "[id]不能为空")
    @Schema(title = "id")
    private String id;

    @Schema(title = "父级编码")
    private String parentCode;

    @Length(max = 64, message = "[名称]长度不能超过64")
    @Schema(title = "名称")
    private String dictDesc;

    @Length(max = 64, message = "[分类说明]长度不能超过64")
    @Schema(title = "分类说明")
    private String categoryDesc;

    @Schema(title = "排序编号")
    private Integer sortNo;

    @Length(max = 255, message = "[附加属性]长度不能超过255")
    @Schema(title = "附加属性")
    private String attributes;
}
