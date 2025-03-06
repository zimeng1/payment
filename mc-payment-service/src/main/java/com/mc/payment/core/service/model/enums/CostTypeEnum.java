package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 成本类型,[0:按笔收费/U,1:按费率收费/%]
 */
@Getter
public enum CostTypeEnum {
    ITEM_0(0, "按笔收费/U"),
    ITEM_1(1, "按费率收费/%");

    private final int code;
    private final String desc;

    CostTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (CostTypeEnum anEnum : CostTypeEnum.values()) {
            if (anEnum.getCode() == code) {
                return anEnum.getDesc();
            }
        }
        return "";
    }
}
