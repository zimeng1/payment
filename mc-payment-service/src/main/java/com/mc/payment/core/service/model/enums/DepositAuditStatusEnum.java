package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 入金审核状态,[0:待审核,1:审核通过,2:审核不通过]
 *
 * @author chenyaoyuan
 * @since 2024/08/20 15:39
 */
@Getter
public enum DepositAuditStatusEnum {
    ITEM_0(0, "待审核"),
    ITEM_1(1, "审核通过"),
    ITEM_2(2, "审核不通过");

    private final int code;
    private final String desc;

    DepositAuditStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (DepositAuditStatusEnum statusEnum : DepositAuditStatusEnum.values()) {
            if (statusEnum.getCode() == code) {
                return statusEnum.getDesc();
            }
        }
        return "";
    }
}
