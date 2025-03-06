package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 出入金记录类型,[0:入金,1:出金]
 */
@Getter
public enum RecordTypeEnum {
    ITEM_0(0, "入金"),
    ITEM_1(1, "出金");

    private final int code;
    private final String desc;

    RecordTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (RecordTypeEnum anEnum : RecordTypeEnum.values()) {
            if (anEnum.getCode() == code) {
                return anEnum.getDesc();
            }
        }
        return "";
    }

}