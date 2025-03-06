package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 状态,[0:待生成,1:生成中,2:生成失败,3:生成成功]
 */
@Getter
public enum AccountStatusEnum {
    GENERATE_WAIT(0, "待生成"),
    GENERATE_ING(1, "生成中"),
    GENERATE_FAIL(2, "生成失败"),
    GENERATE_SUCCESS(3, "生成成功");

    private final int code;
    private final String desc;

    AccountStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (AccountStatusEnum anEnum : AccountStatusEnum.values()) {
            if (anEnum.getCode() == code) {
                return anEnum.getDesc();
            }
        }
        return "";
    }
}