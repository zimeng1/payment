package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 入金状态,[0:待入金,1:部分入金,2:完全入金,3:撤销入金,4:请求失败]
 *
 * @author Marty
 * @since 2024/05/23 14:53
 */
@Getter
public enum DepositRecordStatusEnum {
    ITEM_0(0, "待入金"),
    ITEM_1(1, "部分入金"),
    ITEM_2(2, "完全入金"),
    ITEM_3(3, "撤销入金"),
    ITEM_4(4, "请求失效"),
    ITEM_5(5, "待审核"),
    ITEM_6(6, "审核不通过");

    private final int code;
    private final String desc;

    DepositRecordStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (DepositRecordStatusEnum statusEnum : DepositRecordStatusEnum.values()) {
            if (statusEnum.getCode() == code) {
                return statusEnum.getDesc();
            }
        }
        return "";
    }
}
