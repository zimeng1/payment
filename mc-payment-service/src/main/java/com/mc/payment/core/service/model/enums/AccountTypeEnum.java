package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 账户类型,[0:入金账户,1:出金账户,2:出入金账户]
 * 和PurposeTypeEnum 保持一致
 */
@Getter
public enum AccountTypeEnum {
    //账户类型,[0:入金账户,1:出金账户]
    DEPOSIT(0, "入金账户"),
    WITHDRAWAL(1, "出金账户"),
    DEPOSIT_WITHDRAWAL(2, "出入金账户");

    private final int code;
    private final String desc;

    AccountTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (AccountTypeEnum anEnum : AccountTypeEnum.values()) {
            if (anEnum.getCode() == code) {
                return anEnum.getDesc();
            }
        }
        return "";
    }
}