package com.mc.payment.third.party.api.model.vo.fireBlocks;

import com.mc.payment.third.party.api.model.vo.fireBlocks.nested.TransactionFeeVo;
import lombok.Data;

/**
 * @author Marty
 * @since 2024/04/17 18:33
 */
@Data
//Estimates the transaction fee for a transaction request. vo
public class EstimatedTransactionFeeVo {
    //low fee.
    private TransactionFeeVo low;

    //medium fee.
    private TransactionFeeVo medium;

    //Thigh fee.
    private TransactionFeeVo high;
}
