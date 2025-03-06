package com.mc.payment.gateway.channels.ofapay.model.rsp;

import lombok.Data;

@Data
public class OfaPayQueryDepositRsp {
    /**
     * 商户ID
     * 字段长度: 20
     * 必填字段
     * 描述: 商户ID
     */
    private String scode;

    /**
     * 唯一订单ID
     * 字段长度: 50
     * 必填字段
     * 描述: 唯一订单ID
     */
    private String orderid;

    /**
     * 系统中的订单唯一编号
     * 字段长度: 50
     * 描述: 系统中的订单唯一编号
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
     * 描述: 商户填写的备注值
     */
    private String memo;

    /**
     * 交易完成时间
     * 字段长度: 20
     * 描述: 交易完成时间
     */
    private String resptime;

    /**
     * 状态
     * 描述: 0: 处理中，1: 成功，-1: 失败
     */
    private int status;

    /**
     * 结果代码
     * 字段长度: 2
     * 描述: 结果代码，参考附录1
     */
    private String respcode;

    /**
     * 加密规则
     * 描述: 参考第4节：加密规则
     */
    private String sign;
}
