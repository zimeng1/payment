package com.mc.payment.fireblocksapi.model.vo.fireBlocks.nested;

import lombok.Data;

/**
 * @author Marty
 * @since 2024/04/17 18:22
 */
@Data
//SystemMessageInfoVo
public class SystemMessageInfoVo {

    //type: WARN BLOCK
    private String type;

    //A response from Fireblocks that communicates a message about the health of the process being performed. If this object is returned with data, you should expect potential delays or incomplete transaction statuses.
    private String message;

}
