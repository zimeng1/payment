package com.mc.payment.core.service.model.rsp;

import com.mc.payment.core.service.entity.MerchantWalletEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "商户钱包-分页返回实体")
public class MerchantWalletRsp extends MerchantWalletEntity {

    @Schema(title = "钱包所属商户")
    private String merchantName;

}
