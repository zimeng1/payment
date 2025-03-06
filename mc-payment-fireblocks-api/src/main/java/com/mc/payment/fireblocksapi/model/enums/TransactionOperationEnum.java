package com.mc.payment.fireblocksapi.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author Marty
 * @since 2024/04/15 17:57
 */
public enum TransactionOperationEnum {

    TRANSFER("TRANSFER"),

    BURN("BURN"),

    CONTRACT_CALL("CONTRACT_CALL"),

    MINT("MINT"),

    RAW("RAW"),

    TYPED_MESSAGE("TYPED_MESSAGE");

    private String value;

    TransactionOperationEnum(String value) {
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
    public static TransactionOperationEnum fromValue(String value) {
        for (TransactionOperationEnum b : TransactionOperationEnum.values()) {
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
