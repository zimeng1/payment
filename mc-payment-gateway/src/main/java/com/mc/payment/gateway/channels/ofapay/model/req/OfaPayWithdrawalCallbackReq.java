package com.mc.payment.gateway.channels.ofapay.model.req;

import lombok.Data;

@Data
public class OfaPayWithdrawalCallbackReq extends OfaPayBaseReq {


    /**
     * 订单ID
     * 字段长度: 50
     * 必填字段
     * 描述: 订单的唯一ID
     */
    private String orderid;

    /**
     * 订单号
     * 字段长度: 50
     * 描述: 订单的唯一号码，在请求失败时将为空
     */
    private String orderno;

    /**
     * 金额
     * 字段长度: 12
     * 必填字段
     * 描述: 格式为 00.00，保留两位小数
     * 例如：\$10，提交的金额应为 10.00
     */
    private String money;

    /**
     * 结果状态
     * 必填字段
     * 描述: S:成功，F:失败。这个字段表示最终状态
     */
    private String status;

    /**
     * 交易消息代码
     * 字段长度: 2
     * 必填字段
     * 描述: 交易消息代码，参考附件1
     */
    private String respcode;

    /**
     * 交易时间
     * 描述: 仅在交易成功时回调
     * 在失败时将为空
     */
    private String resptime;

}
