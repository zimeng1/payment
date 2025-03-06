package com.mc.payment.core.service.model.dto;

import com.mc.payment.core.service.entity.MerchantEntity;
import lombok.Data;

/**
 * @author Conor
 * @since 2024/6/3 下午6:07
 */
@Data
public class MerchantSimpleVo {
    private String merchantId;
    private String merchantName;

    public static MerchantSimpleVo valueOf(MerchantEntity merchantEntity) {
        MerchantSimpleVo merchantSimpleVo = new MerchantSimpleVo();
        merchantSimpleVo.setMerchantId(merchantEntity.getId());
        merchantSimpleVo.setMerchantName(merchantEntity.getName());
        return merchantSimpleVo;

    }
}
