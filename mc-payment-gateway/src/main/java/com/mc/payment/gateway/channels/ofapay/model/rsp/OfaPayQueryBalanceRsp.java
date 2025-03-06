package com.mc.payment.gateway.channels.ofapay.model.rsp;

import lombok.Data;

@Data
public class OfaPayQueryBalanceRsp {
    /**
     * 状态码
     * 必填字段
     * 描述: 1-请求成功, -1-请求失败
     */
    private String prc;

    /**
     * 请求代码
     * 必填字段
     * 描述: 00:成功，其他参考附件1
     */
    private String errcode;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 可用余额
     * 描述: 格式为 00.00，保留两位小数
     * 例如：\$10，提交的金额应为 10.00
     */
    private String balance;
}
