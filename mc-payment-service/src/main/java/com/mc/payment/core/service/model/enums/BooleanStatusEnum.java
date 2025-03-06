package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 是否状态,[0:否,1:是]
 */
@Getter
public enum BooleanStatusEnum {
    ITEM_0(0, "否"),
    ITEM_1(1, "是");

    private final int code;
    private final String desc;

    BooleanStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (BooleanStatusEnum anEnum : BooleanStatusEnum.values()) {
            if (anEnum.getCode() == code) {
                return anEnum.getDesc();
            }
        }
        return "";
    }

}