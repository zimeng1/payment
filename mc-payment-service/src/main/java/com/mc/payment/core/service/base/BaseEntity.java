package com.mc.payment.core.service.base;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author conor
 * @since 2024/01/24 18:41
 */
@Data
public class BaseEntity extends BaseNoLogicalDeleteEntity{


    @Schema(title = "逻辑删除,[0:未删除,1已删除]")
    @TableField("deleted")
    protected Integer deleted;
}
