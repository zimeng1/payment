package com.mc.payment.core.service.model.rsp;

import lombok.Data;

@Data
public class AssetBankDto {

    /**
     * 银行代码
     */
    private String bankCode;

    /**
     * 银行名称
     */
    private String bankName;
}
