package com.mc.payment.core.service.model.dto;

import lombok.Data;

/**
 * 商户参数实体
 *
 * @author conor
 * @since 2024/2/22 17:17:54
 */
@Data
public class MerchantParamVo {

    private String publicKey;
    private String accessKey;
    private String secretKey;
    /**
     * 对应BlockATM的MerchantID
     */
    private String merchantNo;
}
