package com.mc.payment.gateway.channels.passtopay.model.req;

import lombok.Data;

@Data
public class PassToPayDepositCallBackReq {
    /**
     * 付款成功状态码
     */
    private static final int SUCCESS = 2;
    /**
     * 付款失敗狀態碼
     */
    private static final int FAIL = 3;


    /**
     * 返回PassToPay生成的唯一訂單編號
     * 範例: P12021022311124442600
     */
    private String payOrderId;

    /**
     * 商家號碼
     * 範例: M1621873433953
     */
    private String mchNo;

    /**
     * 應用 ID
     * 範例: 60cc09bce4b0f1c0b83761c9
     */
    private String appId;

    /**
     * 返回商家傳入的訂單編號
     * 範例: 20160427210604000490
     */
    private String mchOrderNo;
    /**
     * 支付接口
     */
    private String ifCode;

    /**
     * 付款方式，如：CHANNEL_CASHIER
     * 範例: CHANNEL_CASHIER
     */
    private String wayCode;

    /**
     * 付款金額，两位小数位，*100取整
     * 範例: 10000
     */
    private Integer amount;

    /**
     * 客户支付的貨幣代碼
     * 範例: cny
     */
    private String currency;

    /**
     * 付款訂單狀態
     * 0-訂單已產生
     * 1-付款中
     * 2-付款成功
     * 3-付款失敗
     * 4-已取消
     * 5-已退款
     * 6-訂單已關閉
     * 範例: 2
     */
    private Integer state;

    /**
     * 用戶端 IPV4 位址
     * 範例: 210.73.10.148
     */
    private String clientIp;

    /**
     * 產品標題
     * 範例: PassToPay product title test
     */
    private String subject;

    /**
     * 產品說明
     * 範例: PassToPay Product Description Test
     */
    private String body;

    /**
     * 對應通道的訂單編號
     * 範例: 20160427210604000490
     */
    private String channelOrderNo;

    /**
     * 通道訂單回傳錯誤碼
     * 範例: 1002
     */
    private Integer errCode;

    /**
     * 通道訂單回傳錯誤說明
     * 範例: 134586944573118714
     */
    private String errMsg;

    /**
     * 商家擴充參數
     * 範例: 134586944573118714
     */
    private String extParam;

    /**
     * 訂單支付成功時間，13位時間戳
     * 範例: 1622016572190
     */
    private Long successTime;

    /**
     * 訂單建立時間，13位時間戳
     * 範例: 1622016572190
     */
    private Long createdAt;
    /**
     * 商户手续费费率快照
     */
    private String mchFeeRate;
    /**
     * 商户手续费,单位分
     */
    private Integer mchFeeAmount;
    /**
     * 渠道用户标识
     */
    private String channelUser;

    /**
     * 簽名值，詳見簽名演算法
     * 範例: C380BEC2BFD727A4B6845133519F3AD6
     */
    private String sign;

    public boolean isSuccess() {
        return SUCCESS == state;
    }

    public boolean isFail() {
        return FAIL == state;
    }

    public boolean isClose() {
        return 6 == state;
    }
}
