package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 账号资产关系表
 * </p>
 *
 * @author conor
 * @since 2024-02-19 16:39:16
 */
@Getter
@Setter
@TableName("mcp_account_asset_relation")
@Schema(title = "AccountAssetRelationEntity对象", description = "账号资产关系表")
public class AccountAssetRelationEntity extends BaseNoLogicalDeleteEntity {

    private static final long serialVersionUID = 1L;
    @Schema(title = "账号id")
    @TableField("account_id")
    private String accountId;

    @Schema(title = "资产id")
    @TableField("asset_id")
    private String assetId;


}
