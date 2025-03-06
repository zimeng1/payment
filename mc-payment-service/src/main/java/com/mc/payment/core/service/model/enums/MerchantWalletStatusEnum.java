package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 状态,[0:待生成,1:生成中,2:生成失败,3:待使用,4:锁定中,5:冷却中]
 */
@Getter
public enum MerchantWalletStatusEnum {
    //状态,[0:待生成,1:生成中,2:生成失败,3:待使用,4:锁定中,5:冷却中]
    GENERATE_WAIT(0, "待生成"),
    GENERATE_ING(1, "生成中"),
    GENERATE_FAIL(2, "生成失败"),
    USED_WAIT(3, "待使用"),
    LOCK_ING(4, "锁定中"),
    COOL_ING(5, "冷却中");

    private final int code;
    private final String desc;

    MerchantWalletStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (MerchantWalletStatusEnum anEnum : MerchantWalletStatusEnum.values()) {
            if (anEnum.getCode() == code) {
                return anEnum.getDesc();
            }
        }
        return "";
    }
}