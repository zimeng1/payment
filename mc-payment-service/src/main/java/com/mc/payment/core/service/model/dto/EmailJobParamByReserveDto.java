package com.mc.payment.core.service.model.dto;

import lombok.Data;

/**
 * @author Conor
 * @since 2024/5/23 上午10:25
 */
@Data
public class EmailJobParamByReserveDto extends EmailJobParamDto {
    /**
     * 商户id
     */
    private String merchantId;
    /**
     * 资产名称
     */
    private String assetNames;

    public EmailJobParamByReserveDto(String recipientMail, String subject, String content, String merchantId, String assetNames) {
        super(recipientMail, subject, content);
        this.merchantId = merchantId;
        this.assetNames = assetNames;
    }

    public EmailJobParamByReserveDto() {
    }
}
