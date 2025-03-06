package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 结算周期,[0:按日,1:按周,2:按月]
 */
@Getter
public enum BillingCycleEnum {
    DAY(0, "按日"),
    WEEK(1, "按周"),
    MOON(2, "按月");

    private final int code;
    private final String desc;

    BillingCycleEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (BillingCycleEnum anEnum : BillingCycleEnum.values()) {
            if (anEnum.getCode() == code) {
                return anEnum.getDesc();
            }
        }
        return "";
    }
}