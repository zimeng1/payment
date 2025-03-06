package com.mc.payment.core.service.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.api.model.req.DepositReq;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.model.req.ProcessDepositReq;
import lombok.Data;

import java.io.Serializable;

/**
 * 支付页面信息
 *
 * @TableName mcp_payment_page
 */
@TableName(value = "mcp_payment_page")
@Data
public class PaymentPageEntity extends BaseNoLogicalDeleteEntity implements Serializable {
    /**
     * 支付类型,[0:入金,1:出金]
     */
    @TableField(value = "paymet_type")
    private Integer paymetType;

    /**
     * 商户id
     */
    @TableField(value = "merchant_id")
    private String merchantId;

    /**
     * 跟踪id,申请方提供唯一跟踪ID以查询处理结果
     */
    @TableField(value = "tracking_id")
    private String trackingId;

    /**
     * 页面文本
     */
    @TableField(value = "page_text_json")
    private String pageTextJson;

    /**
     * 支付通道返回的url
     */
    @TableField(value = "channel_page_url")
    private String channelPageUrl;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public static PaymentPageEntity valueOf(DepositReq req, String merchantId) {
        PaymentPageEntity paymentPageEntity = new PaymentPageEntity();
        paymentPageEntity.setTrackingId(req.getTrackingId());
        paymentPageEntity.setPaymetType(0);
        paymentPageEntity.setMerchantId(merchantId);
        JSONObject pageTextJson = new JSONObject();
        pageTextJson.set("text1", req.getText1());
        pageTextJson.set("text2", req.getText2());
        pageTextJson.set("text3", req.getText3());
        pageTextJson.set("text4", req.getText4());
        paymentPageEntity.setPageTextJson(pageTextJson.toString());
        return paymentPageEntity;
    }

    public static PaymentPageEntity valueOf(ProcessDepositReq req) {
        return valueOf(req, req.getMerchantId());
    }

}