package com.mc.payment.third.party.api.model.vo.fireBlocks;

import com.mc.payment.third.party.api.model.vo.fireBlocks.nested.NetworkFeeVo;
import lombok.Data;

/**
 * @author Marty
 * @since 2024/4/20 10:57
 */

@Data
//EstimatedNetworkFee
public class EstimatedNetworkFeeVo {

    //low
    private NetworkFeeVo low;

    //medium
    private NetworkFeeVo medium;

    //high
    private NetworkFeeVo high;
}
