package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.mc.payment.core.service.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 邮件模板表
 */
@Data
@TableName("mcp_email_template")
@Schema(title = "EmailTemplateEntity对象", description = "")
public class EmailTemplateEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField("type")
    private String type;

    @TableField("content")
    private String content;
}
