package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 账户签约的状态,[0:未签约,1:已经签约]
 */
@Getter
public enum ContractStatusEnum {
    ITEM_0(0, "未签约"),
    ITEM_1(1, "已经签约");

    private final int code;
    private final String desc;

    ContractStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (ContractStatusEnum anEnum : ContractStatusEnum.values()) {
            if (anEnum.getCode() == code) {
                return anEnum.getDesc();
            }
        }
        return "";
    }

}