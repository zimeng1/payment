package com.mc.payment.gateway.channels.fireblocks.util;


import com.fireblocks.sdk.model.TransactionOperation;
import com.fireblocks.sdk.model.TransactionRequest;
import com.fireblocks.sdk.model.TransferPeerPathSubType;
import com.fireblocks.sdk.model.TransferPeerPathType;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * @author Marty
 * @since 2024/04/16 15:57
 */
public class FireBlocksUtil {

    public static UUID getUuidFromString(String uuidString) {
        return StringUtils.isBlank(uuidString) ? null : UUID.fromString(uuidString);
    }

    public static TransferPeerPathSubType getPeerPathSubTypeFromString(String subType) {
        return StringUtils.isBlank(subType) ? null : TransferPeerPathSubType.fromValue(subType);
    }

    public static TransferPeerPathType getPeerPathTypFromString(String type) {
        return StringUtils.isBlank(type) ? null : TransferPeerPathType.fromValue(type);
    }

    public static TransactionOperation getOperationFromString(String operation) {
        return StringUtils.isBlank(operation) ? null : TransactionOperation.fromValue(operation);
    }

    public static TransactionRequest.FeeLevelEnum getFeeLevelEnumString(String feeLevel) {
        return StringUtils.isBlank(feeLevel) ? null : TransactionRequest.FeeLevelEnum.valueOf(feeLevel);
    }


}

