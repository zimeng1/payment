package com.mc.payment.core.service.model.rsp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ConfirmRsp {
    @Schema(title = "重定向页面地址", description = "币种为法币时有值")
    private String redirectUrl;
    @Schema(title = "钱包地址", description = "币种为加密货币时有值")
    private String walletAddress;
    @Schema(title = "钱包地址二维码", description = "币种为加密货币时有值,base64编码")
    private String walletQRCode;

    public ConfirmRsp() {
    }

    public ConfirmRsp(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public ConfirmRsp(String walletAddress, String walletQRCode) {
        this.walletAddress = walletAddress;
        this.walletQRCode = walletQRCode;
    }


    public static ConfirmRsp valueOf(ConfirmRsp ConfirmRsp) {
        ConfirmRsp confirmRsp = new ConfirmRsp();
        confirmRsp.setRedirectUrl(ConfirmRsp.getRedirectUrl());
        confirmRsp.setWalletAddress(ConfirmRsp.getWalletAddress());
        confirmRsp.setWalletQRCode(ConfirmRsp.getWalletQRCode());
        return confirmRsp;
    }
}
