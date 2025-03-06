package com.mc.payment.core.service.model.req.platform;

import com.mc.payment.core.service.entity.PayProtocolEntity;
import com.mc.payment.core.service.model.enums.AssetTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Data
public class CryptoProtocolSaveReq {

    @Schema(title = "加密货币-网络协议")
    @NotBlank(message = "[加密货币-网络协议]不能为空")
    @Length(max = 20, message = "[加密货币-网络协议]长度不能超过20")
    private String netProtocol;

    @Schema(title = "加密货币-资产网络")
    @NotBlank(message = "[加密货币-资产网络]不能为空")
    @Length(max = 50, message = "[加密货币-资产网络]长度不能超过20")
    private String assetNet;

    @Schema(title = "图标数据,[base64编码]")
    @NotBlank(message = "[图标数据]不能为空")
    @Length(max = 20000, message = "[图标数据]长度不能超过20000")
    private String iconData;

    @Schema(title = "资产状态,[0:禁用,1:激活]")
    @NotNull(message = "[资产状态]不能为空")
    @Range(min = 0, max = 1, message = "[资产状态]必须为[0:禁用,1:激活]")
    private Integer status;

    @Schema(title = "正则表达式")
    @NotBlank(message = "[正则表达式]不能为空")
    @Length(max = 255, message = "[正则表达式]长度不能超过255")
    private String regularExpression;


    public PayProtocolEntity convert() {
        PayProtocolEntity entity = new PayProtocolEntity();
        entity.setAssetType(AssetTypeEnum.CRYPTO_CURRENCY.getCode());
        entity.setNetProtocol(this.netProtocol);
        entity.setAssetNet(this.assetNet);
        entity.setIconData(this.iconData);
        entity.setStatus(this.status);
        entity.setRegularExpression(this.regularExpression);
        return entity;
    }
}
