package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 成本限额,[0:最低/元,1:最高/元]
 */
@Getter
public enum CostLimitEnum {
    ITEM_0("0", "最低/U"),
    ITEM_1("1", "最高/U");

    private final String code;
    private final String desc;

    CostLimitEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(String code) {
        if (code == null) {
            return "";
        }
        for (CostLimitEnum anEnum : CostLimitEnum.values()) {
            if (anEnum.getCode().equals(code)) {
                return anEnum.getDesc();
            }
        }
        return "";
    }
}