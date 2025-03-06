package com.mc.payment.gateway.channels.ofapay.model.req;

import lombok.Data;

/**
 * 交易回调
 */
@Data
public class OfaPayDepositCallbackReq extends OfaPayBaseReq {


    /**
     * 订单ID
     * 字段长度: 50
     * 必填字段
     * 描述: 订单的唯一ID
     */
    private String orderid;
    /**
     * 订单的唯一编号
     * 字段长度: 50
     * 必填字段
     * 描述: 订单的唯一编号
     */
    private String orderno;

    /**
     * 支付类型
     * 字段长度: 10
     * 必填字段
     * 描述: 参考附录2：支付类型列表
     */
    private String paytype;

    /**
     * 金额
     * 必填字段
     * 描述: 格式为00.00，保留两位小数
     */
    private String amount;

    /**
     * 产品名称
     * 字段长度: 100
     * 必填字段
     */
    private String productname;

    /**
     * 货币类型
     * 字段长度: 3
     * 必填字段
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
     * 必填字段
     * 描述: 1: 成功，-1: 失败
     */
    private int status;

    /**
     * 结果代码
     * 字段长度: 2
     * 必填字段
     * 描述: 结果代码，参考附录1
     */
    private String respcode;

    /**
     * 交易ID
     */
    private String txid;

    /**
     * 信用额度
     * 描述: 如有此字段，请根据此金额计算费用
     */
    private String credit_amount;

}
