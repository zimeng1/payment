package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 资产类型,[0:加密货币,1:法币]
 */
@Getter
public enum AssetTypeEnum {

    CRYPTO_CURRENCY(0, "加密货币"),
    FIAT_CURRENCY(1, "法币");

    private final int code;
    private final String desc;

    AssetTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (AssetTypeEnum anEnum : AssetTypeEnum.values()) {
            if (anEnum.getCode() == code) {
                return anEnum.getDesc();
            }
        }
        return "";
    }
}