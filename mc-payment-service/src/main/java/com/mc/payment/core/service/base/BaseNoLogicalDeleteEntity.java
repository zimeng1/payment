package com.mc.payment.core.service.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author conor
 * @since 2024/01/24 18:41
 */
@Data
public class BaseNoLogicalDeleteEntity implements Serializable {
    /**
     * 这里使用String不使用Long是因为JavaScript中数字的精度是有限的，Java的Long类型的数字超出了JavaScript的处理范围
     */
    @TableId(value = "id")
    protected String id;

    @Schema(title = "创建者")
    @TableField(fill = FieldFill.INSERT)
    protected String createBy;

    @Schema(title = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date createTime;

    @Schema(title = "更新者")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected String updateBy;

    @Schema(title = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date updateTime;

}
