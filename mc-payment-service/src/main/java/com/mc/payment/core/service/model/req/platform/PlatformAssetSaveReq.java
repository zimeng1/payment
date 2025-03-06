package com.mc.payment.core.service.model.req.platform;

import com.mc.payment.core.service.entity.PlatformAssetEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Data
public class PlatformAssetSaveReq {
    /**
     * 资产类型,[0:加密货币,1:法币]
     */
    @Schema(title = "资产类型,[0:加密货币,1:法币]")
    @NotNull(message = "[资产类型]不能为空")
    @Range(min = 0, max = 1, message = "[资产类型]必须为[0:加密货币,1:法币]")
    private Integer assetType;

    /**
     * 资产名称,[如:BTC]
     */
    @Schema(title = "资产名称,[如:BTC]", example = "BTC")
    @NotBlank(message = "[资产名称]不能为空")
    @Length(max = 20, message = "[资产名称]长度不能超过20")
    private String assetName;

    /**
     * 资产状态,[0:禁用,1:激活]
     */
    @Schema(title = "资产状态,[0:禁用,1:激活]", example = "1")
    @NotNull(message = "[资产状态]不能为空")
    @Range(min = 0, max = 1, message = "[资产状态]必须为[0:禁用,1:激活]")
    private Integer status;

    /**
     * 图标数据,[base64编码]
     */
    @Schema(title = "图标数据,[base64编码]")
    @NotBlank(message = "[图标数据]不能为空")
    @Length(max = 20000, message = "[图标数据]长度不能超过20000")
    private String iconData;

    public PlatformAssetEntity convert() {
        PlatformAssetEntity entity = new PlatformAssetEntity();
        entity.setAssetType(this.assetType);
        entity.setAssetName(this.assetName);
        entity.setStatus(this.status);
        entity.setIconData(this.iconData);
        return entity;
    }
}
