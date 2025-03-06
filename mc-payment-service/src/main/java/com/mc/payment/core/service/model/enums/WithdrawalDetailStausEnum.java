package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 入金明细状态
 */
@Getter
public enum WithdrawalDetailStausEnum {

    ITEM_1(1, "未确认"),
    ITEM_2(2, "确认中"),
    ITEM_3(3, "已确认"),
    ITEM_4(4, "已取消"),
    ITEM_5(5, "已出金"),
    ITEM_6(6, "出金失败");

    private int code;
    private String desc;

    WithdrawalDetailStausEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (WithdrawalDetailStausEnum anEnum : WithdrawalDetailStausEnum.values()) {
            if (anEnum.getCode() == code) {
                return anEnum.getDesc();
            }
        }
        return "";
    }

}
