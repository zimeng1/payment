package com.mc.payment.fireblocksapi.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author Marty
 * @since 2024/04/15 17:22
 */
public enum TransferPeerPathTypeEnum {
    VAULT_ACCOUNT("VAULT_ACCOUNT"),

    EXCHANGE_ACCOUNT("EXCHANGE_ACCOUNT"),

    INTERNAL_WALLET("INTERNAL_WALLET"),

    EXTERNAL_WALLET("EXTERNAL_WALLET"),

    CONTRACT("CONTRACT"),

    NETWORK_CONNECTION("NETWORK_CONNECTION"),

    FIAT_ACCOUNT("FIAT_ACCOUNT"),

    COMPOUND("COMPOUND"),

    GAS_STATION("GAS_STATION"),

    ONE_TIME_ADDRESS("ONE_TIME_ADDRESS"),

    UNKNOWN("UNKNOWN"),

    END_USER_WALLET("END_USER_WALLET");

    private String value;

    TransferPeerPathTypeEnum(String value) {
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
    public static TransferPeerPathTypeEnum fromValue(String value) {
        for (TransferPeerPathTypeEnum b : TransferPeerPathTypeEnum.values()) {
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
