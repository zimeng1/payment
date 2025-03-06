package com.mc.payment.third.party.api.model.vo.fireBlocks;

import com.mc.payment.third.party.api.model.vo.fireBlocks.nested.SystemMessageInfoVo;
import lombok.Data;

/**
 * @author Marty
 * @since 2024/04/17 18:27
 */
@Data
//Creates a new transaction. This endpoint can be used for regular Transfers, Contract Calls, Raw & Typed message signing.
public class CreateTransactionVo {
    //The ID of the transaction.
    private String id;

    //The primary status of the transaction. For details, see [Primary transaction statuses.] https://developers.fireblocks.com/reference/primary-transaction-statuses
    private String status;

    //SystemMessageInfoVo
    private SystemMessageInfoVo systemMessages;


}
