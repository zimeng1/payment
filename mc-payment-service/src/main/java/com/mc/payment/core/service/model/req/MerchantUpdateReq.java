package com.mc.payment.core.service.model.req;

import com.mc.payment.core.service.entity.MerchantEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(title = "商户修改参数实体")
public class MerchantUpdateReq extends MerchantSaveReq {
    private static final long serialVersionUID = -6200304811032124199L;

    @Schema(title = "商户id")
    @NotBlank(message = "[商户id]不能为空")
    private String id;

    @Override
    public MerchantEntity convert() {
        MerchantEntity entity = super.convert();
        entity.setId(id);
        return entity;
    }
}
