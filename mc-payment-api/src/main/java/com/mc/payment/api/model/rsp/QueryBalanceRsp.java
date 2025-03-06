package com.mc.payment.api.model.rsp;

import lombok.Data;

@Data
public class QueryBalanceRsp{
    /**
     * 余额
     */
    private String balance;
}
