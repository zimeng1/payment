package com.mc.payment.gateway.channels.passtopay.model.rsp;

import lombok.Data;

@Data
public class PassToPayCreateOrderRsp {
    /*
     * 返回付款系統訂單編號
     * 範例：U12021022311124442600
     * 必填字段
     */
    private String payOrderId;

    /*
     * 返回商家傳入的訂單編號
     * 範例：20160427210604000490
     * 必填字段
     */
    private String mchOrderNo;

    /*
     * 付款訂單狀態
     * 0-已產生訂單
     * 1-付款中
     * 2-付款成功
     * 3-付款失敗
     * 4-已取消
     * 5-已退款
     * 6-訂單已關閉
     * 範例：2
     * 必填字段
     */
    private Integer orderState;

    /*
     * 付款參數類型
     * payUrl-跳轉連結方式
     * form-表單方式
     * codeUrl-二維碼位址
     * codeImgUrl-二維碼圖片位址
     * none-null支付參數
     * 範例：payUrl
     * 必填字段
     */
    private String payDataType;

    /*
     * 根据payDataType返回对应的支付数据
     * 範例：https://pay.pass2pay.io/api/scan/imgs/aa.png
     */
    private String payData;

    /*
     * 通道傳回的錯誤碼
     * 範例：ACQ.PAYMENT_AUTH_CODE_INVALID
     */
    private String errCode;

    /*
     * 通道傳回的錯誤描述
     * 範例：Business Failed
     */
    private String errMsg;
}
