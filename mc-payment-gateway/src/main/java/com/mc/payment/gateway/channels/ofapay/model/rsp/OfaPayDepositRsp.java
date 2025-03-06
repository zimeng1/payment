package com.mc.payment.gateway.channels.ofapay.model.rsp;

import lombok.Data;

@Data
public class OfaPayDepositRsp {
    /**
     * 请求状态
     * 必填字段
     * 描述: 1: 请求成功，-1: 请求失败
     */
    private String status;

    /**
     * 响应代码
     * 字段长度: 2
     * 必填字段
     * 描述: 00: 成功，其余参考附录1
     */
    private String respcode;

    /**
     * 响应消息
     * 字段长度: 50
     * 必填字段
     */
    private String respmsg;

    /**
     * 系统中的唯一商户ID
     * 字段长度: 20
     * 描述: 系统中的唯一商户ID
     */
    private String scode;

    /**
     * 订单的唯一ID
     * 字段长度: 50
     * 描述: 订单的唯一ID
     */
    private String orderid;

    /**
     * 订单的唯一编号
     * 字段长度: 50
     * 描述: 订单的唯一编号
     */
    private String orderno;

    /**
     * 支付类型
     * 字段长度: 10
     * 描述: 参考附录2：支付类型列表
     */
    private String paytype;

    /**
     * 金额
     * 描述: 格式为00.00，保留两位小数
     */
    private String amount;

    /**
     * 产品名称
     * 字段长度: 100
     */
    private String productname;

    /**
     * 货币类型
     * 字段长度: 3
     * 描述: “KRW”
     */
    private String currency;

    /**
     * 备注
     * 字段长度: 255
     */
    private String memo;

    /**
     * URL
     * 描述: 如果包含此字段，请重定向
     */
    private String url;

    /**
     * 浮动金额
     * 描述: 如有此字段，需支付该金额
     */
    private String floatingamount;

    /**
     * 加密规则
     * 描述: 参考第4节：加密规则
     */
    private String sign;

}
