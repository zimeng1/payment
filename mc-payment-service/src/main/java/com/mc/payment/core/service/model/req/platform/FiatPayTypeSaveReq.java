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
public class FiatPayTypeSaveReq {

    @Schema(title = "法币-支付类型")
    @NotBlank(message = "[法币-支付类型]不能为空")
    @Length(max = 20, message = "[法币-支付类型]长度不能超过20")
    private String netProtocol;

    @Schema(title = "图标数据,[base64编码]")
    @NotBlank(message = "[图标数据]不能为空")
    @Length(max = 20000, message = "[图标数据]长度不能超过20000")
    private String iconData;

    @Schema(title = "资产状态,[0:禁用,1:激活]")
    @NotNull(message = "[资产状态]不能为空")
    @Range(min = 0, max = 1, message = "[资产状态]必须为[0:禁用,1:激活]")
    private Integer status;

    public PayProtocolEntity convert() {
        PayProtocolEntity entity = new PayProtocolEntity();
        entity.setAssetType(AssetTypeEnum.FIAT_CURRENCY.getCode());
        entity.setNetProtocol(this.netProtocol);
        entity.setIconData(this.iconData);
        entity.setStatus(this.status);
        return entity;
    }
}
