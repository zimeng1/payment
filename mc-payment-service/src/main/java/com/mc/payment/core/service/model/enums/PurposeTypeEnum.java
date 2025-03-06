package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 用途类型,[0:入金,1:出金,2:出入金]
 */
@Getter
public enum PurposeTypeEnum {
    DEPOSIT(0, "入金"),
    WITHDRAWAL(1, "出金"),
    DEPOSIT_WITHDRAWAL(2, "出入金");

    private final int code;
    private final String desc;

    PurposeTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (PurposeTypeEnum anEnum : PurposeTypeEnum.values()) {
            if (anEnum.getCode() == code) {
                return anEnum.getDesc();
            }
        }
        return "";
    }
}