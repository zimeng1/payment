package com.mc.payment.gateway.channels.ofapay.model.rsp;

import lombok.Data;

@Data
public class OfaPayQueryWithdrawalRsp {
    /**
     * 交易结果
     * 必填字段
     * 描述: 1-请求成功, -1-请求失败
     */
    private String prc;

    /**
     * 结果代码
     * 必填字段
     * 描述: 00:成功，其他参考附件1
     */
    private String errcode;

    /**
     * 商户ID
     * 字段长度: 20
     * 必填字段
     * 描述: 系统中的唯一商户ID
     */
    private String scode;

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
     * 描述: 后端的唯一订单号，在交易成功时反馈，失败时为空
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
     * 提款请求状态
     * 描述: I:处理中, S:提款成功, F:提款失败
     * 如果此字段显示 I，请再次检查，直到出现最终状态
     */
    private String status;

    /**
     * 提款结果消息
     */
    private String msg;

    /**
     * 交易时间
     * 字段长度: 255
     * 必填字段
     * 描述: 格式为 yyyy/MM/dd HH:mm:ss
     */
    private String resptime;

    /**
     * 验证码
     * 必填字段
     * 描述: 参考第4节：加密规则
     */
    private String sign;
}
