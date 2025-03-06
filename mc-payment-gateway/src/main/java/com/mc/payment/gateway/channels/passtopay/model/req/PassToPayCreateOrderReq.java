package com.mc.payment.gateway.channels.passtopay.model.req;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class PassToPayCreateOrderReq {


    /**
     * 商家產生的訂單號碼。
     * 示例值：20160427210604000490
     */
    private String mchOrderNo;

    /**
     * 付款金額，单位：分 注意只支持2位精度，乘以100后使用Integer類型。
     * 示例值：100
     */
    private Integer amount;

    /**
     * 三位數貨幣代碼
     * 示例值：cny
     */
    private String currency;

    /**
     * 請求API時間，13位時間戳。
     * 示例值：1622016572190
     */
    private Long reqTime;

    /**
     * 客戶註冊時間（V1.1.0新增）
     * 示例值：1622016572190
     */
    private Long registerTime;

    /**
     * 客戶唯一編號（V1.1.0新增）
     * 示例值：C200492312
     */
    private String custNo;

    /**
     * 版本號，當前接口支持最低版本為：1.1。
     * 示例值：1.1
     */
    private String version;


    /**
     * 指定付款方式，即在收銀台不展示其他支付方式。
     * 支付方式列表 。注意如果開通了專屬收銀檯，必須開通ALI_WAP或ALI_JSAPI支付方式。
     * 示例值：ALI_QR
     */
    private String wayCode;

    /**
     * 發起付款的用戶真實姓名。
     * 示例值：張三
     */
    private String userName;

    /**
     * 發起付款用戶的手機號碼
     * 示例值：13812341234
     */
    private String mbrTel;

    /**
     * 發起付款用戶的身份證號碼
     * 示例值：320681198603213312
     */
    private String idNo;

    /**
     * 支付結果回調通知URL，只有傳入該值才會啟動通知
     * 示例值：https://www.yourserver.com/notify.htm
     */
    private String notifyUrl;

    /**
     * 支付完成後跳轉URL
     * 示例值：https://www.yourserver.com/return.htm
     */
    private String returnUrl;

    /**
     * 訂單過期時間，單位秒。不傳或小於15分鐘將設置為15分鐘。
     * 示例值：3600
     */
    private Integer expiredTime;

    /**
     * 商家擴充參數，回調時原樣返回
     * 示例值：134586944573118714
     */
    private String extParam;


    public Map<String, String> convertMap() {
        Map<String, String> map = new HashMap<>();
        map.put("mchOrderNo", mchOrderNo);
        map.put("wayCode", wayCode);
        map.put("amount", amount.toString());
        map.put("currency", currency);
        map.put("reqTime", reqTime.toString());
        map.put("version", version);
        map.put("custNo", custNo);
        map.put("registerTime", registerTime.toString());
        map.put("userName", userName);
        map.put("mbrTel", mbrTel);
        map.put("idNo", idNo);
        map.put("notifyUrl", notifyUrl);
        map.put("returnUrl", returnUrl);
        map.put("expiredTime", expiredTime.toString());
        map.put("extParam", extParam);
        return map;
    }
}
