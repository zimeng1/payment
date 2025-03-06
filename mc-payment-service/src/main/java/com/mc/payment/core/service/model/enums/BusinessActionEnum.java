package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 业务动作,[0:入金,1:出金]
 */
@Getter
public enum BusinessActionEnum {
    DEPOSIT(0, "入金"),
    WITHDRAWALS(1, "出金");

    private final int code;
    private final String desc;

    BusinessActionEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (BusinessActionEnum anEnum : BusinessActionEnum.values()) {
            if (anEnum.getCode() == code) {
                return anEnum.getDesc();
            }
        }
        return "";
    }
}
