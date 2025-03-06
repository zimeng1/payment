package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 出金审核状态,[1:审核通过,2:审核不通过,3:终止执行,4:重新执行]
 *
 * @author chenyaoyuan
 * @since 2024/08/20 15:39
 */
@Getter
public enum WithdrawalAuditStatusEnum {
    ITEM_0(0, "待审核"),
    ITEM_1(1, "审核通过"),
    ITEM_2(2, "审核不通过"),
    ITEM_3(3, "终止执行");

    private final int code;
    private final String desc;

    WithdrawalAuditStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (WithdrawalAuditStatusEnum statusEnum : WithdrawalAuditStatusEnum.values()) {
            if (statusEnum.getCode() == code) {
                return statusEnum.getDesc();
            }
        }
        return "";
    }
}
