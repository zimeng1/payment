package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.model.enums.AssetTypeEnum;
import com.mc.payment.core.service.model.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 平台资产
 *
 * @TableName mcp_platform_asset
 */
@TableName(value = "mcp_platform_asset")
@Data
public class PlatformAssetEntity extends BaseNoLogicalDeleteEntity implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 资产类型,[0:加密货币,1:法币]
     */
    @Schema(title = "资产类型,[0:加密货币,1:法币]")
    @TableField(value = "asset_type")
    private Integer assetType;
    /**
     * 资产名称,[如:BTC]
     */
    @Schema(title = "资产名称,[如:BTC]")
    @TableField(value = "asset_name")
    private String assetName;
    /**
     * 资产状态,[0:禁用,1:激活]
     */
    @Schema(title = "资产状态,[0:禁用,1:激活]")
    @TableField(value = "status")
    private Integer status;
    /**
     * 图标数据,[base64编码]
     */
    @Schema(title = "图标数据,[base64编码]")
    @TableField(value = "icon_data")
    private String iconData;

    // ================== 以下为非数据库字段 ==================
    @Schema(title = "资产状态-描述")
    public String getStatusDesc() {
        return StatusEnum.getEnumDesc(status);
    }

    @Schema(title = "资产类型-描述")
    public String getAssetTypeDesc() {
        return AssetTypeEnum.getEnumDesc(assetType);
    }
}