package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 记录状态,[0:待确认,1:已确认,2:失败]
 */
@Getter
public enum RecordStatusEnum {
    ITEM_0(0, "待确认"),
    ITEM_1(1, "已确认"),
    ITEM_2(2, "失败");

    private final int code;
    private final String desc;

    RecordStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (RecordStatusEnum anEnum : RecordStatusEnum.values()) {
            if (anEnum.getCode() == code) {
                return anEnum.getDesc();
            }
        }
        return "";
    }

}