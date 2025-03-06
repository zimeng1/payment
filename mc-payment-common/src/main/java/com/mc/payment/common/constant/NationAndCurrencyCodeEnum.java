package com.mc.payment.common.constant;

public enum NationAndCurrencyCodeEnum {

    //印尼
    IDR("印尼", "indonesia", "IDR"),
    //印度
    INR("印度", "india", "INR"),
    //泰国
    THB("泰国", "thailand", "THB"),
    //巴西
    BRL("巴西", "brazil", "BRL");

    private String nationName;
    private String nationEnName;
    private String nationCurrency;

    NationAndCurrencyCodeEnum(String nationName, String nationEnName, String nationCurrency) {
        this.nationName = nationName;
        this.nationEnName = nationEnName;
        this.nationCurrency = nationCurrency;
    }

    public String getNationName() {
        return nationName;
    }

    public String getNationEnName() {
        return nationEnName;
    }

    public String getNationCurrency() {
        return nationCurrency;
    }
}
