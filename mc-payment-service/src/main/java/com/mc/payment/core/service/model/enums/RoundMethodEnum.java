package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 取整方式,[0:向上取整,1:向下取整,2:四舍五入,9:无]
 */
@Getter
public enum RoundMethodEnum {
    ITEM_0(0, "向上取整"),
    ITEM_1(1, "向下取整"),
    ITEM_2(2, "四舍五入"),
    // 无任何取整方式
    NONE(9, "");

    private final int code;
    private final String desc;

    RoundMethodEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (RoundMethodEnum anEnum : RoundMethodEnum.values()) {
            if (anEnum.getCode() == code) {
                return anEnum.getDesc();
            }
        }
        return "";
    }
}