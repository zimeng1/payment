package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 状态,[0:禁用,1:激活]
 */
@Getter
public enum StatusEnum {
    DISABLE(0,"禁用"),
    ACTIVE(1,"激活");

    private final int code;
    private final String desc;

    StatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(Integer code) {
        if (code!=null) {
            for (StatusEnum anEnum : StatusEnum.values()) {
                if (anEnum.getCode() == code) {
                    return anEnum.getDesc();
                }
            }
        }
        return "";
    }
}
