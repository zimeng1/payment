package com.mc.payment.core.service.model.rsp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CryptoWithdrawWalletRsp {
    private String walletId;
    private String freezeWalletId;
    private String walletAddress;
    private String accountId;
    private BigDecimal balance;
}
