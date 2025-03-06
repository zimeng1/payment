package com.mc.payment.fireblocksapi.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author Marty
 * @since 2024/04/15 17:23
 */
public enum TransferPeerPathSubTypeEnum {
    BINANCE("BINANCE"),

    BINANCEUS("BINANCEUS"),

    BITFINEX("BITFINEX"),

    BITHUMB("BITHUMB"),

    BITMEX("BITMEX"),

    BITSO("BITSO"),

    BITSTAMP("BITSTAMP"),

    BITTREX("BITTREX"),

    BLINC("BLINC"),

    BYBIT("BYBIT"),

    CIRCLE("CIRCLE"),

    COINBASEEXCHANGE("COINBASEEXCHANGE"),

    COINBASEPRO("COINBASEPRO"),

    COINMETRO("COINMETRO"),

    COINSPRO("COINSPRO"),

    CRYPTOCOM("CRYPTOCOM"),

    DERIBIT("DERIBIT"),

    GEMINI("GEMINI"),

    HITBTC("HITBTC"),

    HUOBI("HUOBI"),

    INDEPENDENTRESERVE("INDEPENDENTRESERVE"),

    KORBIT("KORBIT"),

    KRAKEN("KRAKEN"),

    KRAKENINTL("KRAKENINTL"),

    KUCOIN("KUCOIN"),

    LIQUID("LIQUID"),

    OKCOIN("OKCOIN"),

    OKEX("OKEX"),

    PAXOS("PAXOS"),

    POLONIEX("POLONIEX"),

    EXTERNAL("External"),

    INTERNAL("Internal");

    private String value;

    TransferPeerPathSubTypeEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static TransferPeerPathSubTypeEnum fromValue(String value) {
        for (TransferPeerPathSubTypeEnum b : TransferPeerPathSubTypeEnum.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    /**
     * Convert the instance into URL query string.
     *
     * @param prefix prefix of the query string
     * @return URL query string
     */
    public String toUrlQueryString(String prefix) {
        if (prefix == null) {
            prefix = "";
        }

        return String.format("%s=%s", prefix, this.toString());
    }
}
