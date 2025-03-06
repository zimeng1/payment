package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 变动事件类型,[0:入金,1:出金,2:fireblocks通道钱包同步]
 */
@Getter
public enum ChangeEventTypeEnum {
    DEPOSIT(0, "入金成功"),
    WITHDRAWAL_FREEZE(1, "出金冻结金额"),
    FIREBLOCKS_SYNC(2, "fireblocks通道钱包同步"),
    DEPOSIT_RISK(3, "入金风控冻结"),
    WITHDRAWAL_SUCCESS(4, "出金成功"),
    WITHDRAWAL_FAIL(5, "出金失败");

    private final int code;
    private final String desc;

    ChangeEventTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (ChangeEventTypeEnum anEnum : ChangeEventTypeEnum.values()) {
            if (anEnum.getCode() == code) {
                return anEnum.getDesc();
            }
        }
        return "";
    }
}