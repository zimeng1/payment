package com.mc.payment.core.service.model.enums;

import lombok.Getter;

/**
 * 最新报价数据来源,[0:MT5,1:币安]
 */
@Getter
public enum LastQuoteDataSourceEnum {
    //最新报价数据来源,[0:MT5,1:币安]
    MT5(0, "MT5"),
    BINANCE(1, "币安");

    private final int code;
    private final String desc;

    LastQuoteDataSourceEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (LastQuoteDataSourceEnum anEnum : LastQuoteDataSourceEnum.values()) {
            if (anEnum.getCode() == code) {
                return anEnum.getDesc();
            }
        }
        return "";
    }
}