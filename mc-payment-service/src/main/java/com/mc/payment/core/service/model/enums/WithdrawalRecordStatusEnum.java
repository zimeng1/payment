package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 出金记录状态,[0:已提交,1:待审核,2:余额不足,3:出金中,4:出金完成,5:已拒绝,6:出金错误,7,终止出金]
 *
 * @author Marty
 * @since 2024/04/13 17:16
 */
@Getter
public enum WithdrawalRecordStatusEnum {
    ITEM_0(0, "已提交"),
    ITEM_1(1, "待审核"),
    ITEM_2(2, "余额不足"),
    ITEM_3(3, "出金中"),
    ITEM_4(4, "出金完成"),
    ITEM_5(5, "审核不通过"),
    ITEM_6(6, "出金错误"),
    ITEM_7(7, "终止出金"),
    ;

    private final int code;
    private final String desc;

    WithdrawalRecordStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (WithdrawalRecordStatusEnum withdrawalRecordStatusEnum : WithdrawalRecordStatusEnum.values()) {
            if (withdrawalRecordStatusEnum.getCode() == code) {
                return withdrawalRecordStatusEnum.getDesc();
            }
        }
        return "";
    }
}
