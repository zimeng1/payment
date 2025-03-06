package com.mc.payment.common.rpc.model.fireBlocks.nested;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Marty
 * @since 2024/04/17 11:32
 */
@Data
public class AmlScreeningVo implements Serializable {

    // The AML service provider.
    private String provider;

    // The response of the AML service provider.
    private Object payload;

    // The status of the AML screening request.
    private String screeningStatus;

    // The reason the transaction bypassed AML screening.
    private String bypassReason;

    // The date of the AML screening request in timestamp format.
    private Long timestamp;


}
