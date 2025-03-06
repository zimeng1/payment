package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 分类编码枚举
 * @author Marty
 * @since 2024/4/23 11:57
 */
@Getter
public enum CategoryCodeEnum {
    ASSET_TYPE("ASSET_TYPE", "资产类型"),
    ASSET_NET("ASSET_NET", "资产网络"),
    NET_PROTOCOL("NET_PROTOCOL", "网络协议"),
    CHANNEL_TYPE("CHANNEL_TYPE", "通道类型"),
    CHANNEL_SUB_TYPE("CHANNEL_SUB_TYPE", "通道子类型"),
    ACCOUNT_TYPE("ACCOUNT_TYPE", "账户类型"),
    BUSINESS_SCOPE("BUSINESS_SCOPE", "业务范围");

    private final String code;
    private final String desc;

    CategoryCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public static CategoryCodeEnum getEnum(String code) {
        for (CategoryCodeEnum anEnum : CategoryCodeEnum.values()) {
            if (anEnum.getCode().equals(code)) {
                return anEnum;
            }
        }
        return null;
    }

    public static String getEnumDesc(String code) {
        for (CategoryCodeEnum anEnum : CategoryCodeEnum.values()) {
            if (anEnum.getCode().equals(code)) {
                return anEnum.getDesc();
            }
        }
        return "";
    }
}
