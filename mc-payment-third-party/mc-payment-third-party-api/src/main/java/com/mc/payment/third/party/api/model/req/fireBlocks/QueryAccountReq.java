package com.mc.payment.third.party.api.model.req.fireBlocks;

import com.mc.payment.common.base.BaseReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 列出保险库帐户
 * @author Marty
 * @since 2024/04/13 15:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
//List vault accounts (Paginated)")
public class QueryAccountReq extends BaseReq {
    //可选 - 名字前缀
    //Optional - namePrefix")
    private String namePrefix;

    //可选 - 名字后缀
    //Optional - nameSuffix")
    private String nameSuffix;

    //可选 - 指定 minAmountThreshold 将过滤余额大于此值的账户，否则将返回所有账户
    //Optional - Specifying minAmountThreshold will filter accounts with balances greater than this value, otherwise, it will return all accounts")
    private BigDecimal minAmountThreshold;

    //资产ID
    //The ID of the asset")
    private String assetId;

    // 排序， 默认desc
    //Optional - orderBy default to DESC")
    private String orderBy;

    //Optional - before")
    private String before;

    //Optional - after")
    private String after;

    //分页
    //Optional - default to 200")
    private BigDecimal limit;

}
