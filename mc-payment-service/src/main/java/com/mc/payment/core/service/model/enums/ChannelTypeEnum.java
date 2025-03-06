package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 通道类型,[0:虚拟货币支付,1:法币]
 */
@Getter
public enum ChannelTypeEnum {
    CRYPTO_CURRENCY(0, "虚拟货币支付"),
    LEGAL_CURRENCY(1, "法币"),

    ;

    private final int code;
    private final String desc;

    ChannelTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ChannelTypeEnum getEnum(int code) {
        for (ChannelTypeEnum anEnum : ChannelTypeEnum.values()) {
            if (anEnum.getCode() == code) {
                return anEnum;
            }
        }
        return null;
    }

    public static String getEnumDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (ChannelTypeEnum anEnum : ChannelTypeEnum.values()) {
            if (anEnum.getCode() == code) {
                return anEnum.getDesc();
            }
        }
        return "";
    }
}
