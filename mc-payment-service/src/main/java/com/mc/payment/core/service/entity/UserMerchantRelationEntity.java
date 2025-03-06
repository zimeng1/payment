package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;

/**
 * 系统账号所属商户表
 *
 * @TableName mcp_user_merchant_relation
 */
@TableName(value = "mcp_user_merchant_relation")
@Data
public class UserMerchantRelationEntity extends BaseNoLogicalDeleteEntity {
    @Serial
    private static final long serialVersionUID = 8071079341606849975L;
    /**
     * 系统账号id
     */
    @TableField(value = "user_id")
    @Schema(title = "系统账号id")
    private String userId;

    /**
     * 商户id
     */
    @TableField(value = "merchant_id")
    @Schema(title = "商户id")
    private String merchantId;

}