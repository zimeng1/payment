package com.mc.payment.gateway.channels.ofapay.model.rsp;

import lombok.Data;

@Data
public class OfaPayWithdrawalRsp {
    /**
     * 状态码
     * 字段长度: 可变
     * 描述: 1:请求成功，-1:请求失败
     */
    private String prc;

    /**
     * 请求代码
     * 字段长度: 2
     * 描述: 00:成功，其他请参考附件1
     */
    private String errcode;

    /**
     * 响应信息
     * 字段长度: 50
     */
    private String msg;

    /**
     * 订单号
     * 字段长度: 50
     * 描述: 订单的唯一ID，在请求失败时将为空
     */
    private String orderno;

    /**
     * 重定向路径
     * 描述: 如果收到该字段，请重定向至指定网页
     */
    private String url;
}
