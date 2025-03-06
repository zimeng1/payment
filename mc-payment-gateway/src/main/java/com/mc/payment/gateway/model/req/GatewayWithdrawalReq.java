package com.mc.payment.gateway.model.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GatewayWithdrawalReq extends BaseGatewayReq {
    /**
     * 交易操作的唯一标识
     */
    @NotBlank(message = "[transactionId] cannot be null")
    private String transactionId;
    /**
     * 支付通道标识/币种标识/加密货币标识
     */
    @NotBlank(message = "[transactionId] cannot be null")
    private String channelId;

    /**
     * 金额
     */
    @NotBlank(message = "[transactionId] cannot be null")
    private String amount;
    /**
     * 出金地址/银行卡号/帐户
     */
    @NotBlank(message = "[transactionId] cannot be null")
    private String address;

    /**
     * 通知回调地址 webhook
     */
    private String callbackUrl;

    /**
     * 资产名称
     */
    private String assetName;

    /**
     * 网络协议
     */
    private String netProtocol;

    //region 法币特有字段 考虑放到父类的扩展字段中
    /**
     * 银行代码
     */
    private String bankCode;
    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 持卡人姓名
     */
    private String accountName;

    /**
     * IFSCcode
     */
    private String bankNum;
    //endregion

    //region 加密货币特有字段

    //endregion


}
