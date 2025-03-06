package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.core.service.base.BaseEntity;
import com.mc.payment.core.service.model.req.DictSaveReq;
import com.mc.payment.core.service.model.req.DictUpdateReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author conor
 * @since 2024-04-23 11:18:13
 */
@Getter
@Setter
@TableName("mcp_dict")
@Schema(title = "DictEntity对象", description = "")
public class DictEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(title = "父级编码,[结构:父级分类编码:父级编码]")
    @TableField("parent_code")
    private String parentCode;

    @Schema(title = "编码")
    @TableField("dict_code")
    private String dictCode;

    @Schema(title = "名称")
    @TableField("dict_desc")
    private String dictDesc;

    @Schema(title = "分类编码")
    @TableField("category_code")
    private String categoryCode;

    @Schema(title = "分类说明")
    @TableField("category_desc")
    private String categoryDesc;

    @Schema(title = "排序编号")
    @TableField("sort_no")
    private Integer sortNo;

    @Schema(title = "附加属性")
    @TableField("attributes")
    private String attributes;

    //============
    public static DictEntity valueOf(DictSaveReq req) {
        DictEntity entity = new DictEntity();
        entity.setParentCode(req.getParentCode());
        entity.setDictCode(req.getDictCode());
        entity.setDictDesc(req.getDictDesc());
        entity.setCategoryCode(req.getCategoryCode());
        entity.setCategoryDesc(req.getCategoryDesc());
        entity.setSortNo(req.getSortNo());
        entity.setAttributes(req.getAttributes());
        return entity;
    }

    public static DictEntity valueOf(DictUpdateReq req) {
        DictEntity entity = new DictEntity();
        entity.setId(req.getId());
        entity.setParentCode(req.getParentCode());
        entity.setDictDesc(req.getDictDesc());
        entity.setCategoryDesc(req.getCategoryDesc());
        entity.setSortNo(req.getSortNo());
        entity.setAttributes(req.getAttributes());
        return entity;
    }


}
