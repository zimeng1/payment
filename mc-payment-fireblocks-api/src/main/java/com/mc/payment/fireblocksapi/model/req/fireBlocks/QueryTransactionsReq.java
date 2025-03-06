package com.mc.payment.fireblocksapi.model.req.fireBlocks;

import com.mc.payment.common.base.BaseReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 列出交易历史
 * @author Marty
 * @since 2024/04/15 16:21
 */
@EqualsAndHashCode(callSuper = true)
@Data
//Lists the transaction history for your workspace.")
public class QueryTransactionsReq extends BaseReq {

    //Optional - before")
    private String before;

    //Optional - after")
    private String after;

    //Optional - status")
    private String status;

    // 排序， 默认desc
    //Optional - orderBy default to DESC")
    private String orderBy;

    //Optional - sort")
    private String sort;

    //分页
    //Optional - limit to 200")
    private Integer limit;

    //Optional - sourceType")
    private String sourceType;

    //Optional - sourceId")
    private String sourceId;

    //Optional - destType")
    private String destType;

    //Optional - destId")
    private String destId;

    //Optional - assets")
    private String assets;

    //Optional - txHash")
    private String txHash;

    //资源钱包id
    //Optional - sourceWalletId")
    private String sourceWalletId;

    //Optional - destWalletId")
    private String destWalletId;
}
