package com.mc.payment.core.service.model.rsp;

import cn.hutool.core.util.NumberUtil;
import com.mc.payment.core.service.entity.MerchantWalletEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Conor
 * @since 2024/4/17 下午1:41
 */
@Data
public class WalletBalanceRsp {

    @Schema(title = "账户类型,[0:入金账户,1:出金账户]")
    private String accountType;

    @Schema(title = "资产名称")
    private String assetName;

    @Schema(title = "资产网络")
    private String assetNet;

    @Schema(title = "网络协议")
    private String netProtocol;

    @Schema(title = "钱包地址")
    private String walletAddress;

    @Schema(title = "可用余额,[availableBalance=balance-freezeAmount]")
    private BigDecimal availableBalance;

    @Schema(title = "余额")
    private BigDecimal balance;

    @Schema(title = "冻结金额")
    private  BigDecimal freezeAmount;

    public static WalletBalanceRsp valueOf(MerchantWalletEntity walletEntity){
        WalletBalanceRsp walletBalanceRsp = new WalletBalanceRsp();
        if (walletEntity.getPurposeType() == 0) {
            walletBalanceRsp.setAccountType("0");
        } else {
            walletBalanceRsp.setAccountType("1");
        }
        walletBalanceRsp.setAssetName(walletEntity.getAssetName());
        walletBalanceRsp.setAssetNet("");
        walletBalanceRsp.setNetProtocol(walletEntity.getNetProtocol());
        walletBalanceRsp.setWalletAddress(walletEntity.getWalletAddress());
        walletBalanceRsp.setAvailableBalance(NumberUtil.sub(walletEntity.getBalance(),walletEntity.getFreezeAmount()));
        walletBalanceRsp.setBalance(walletEntity.getBalance());
        walletBalanceRsp.setFreezeAmount(walletEntity.getFreezeAmount());
        return walletBalanceRsp;

    }

}
