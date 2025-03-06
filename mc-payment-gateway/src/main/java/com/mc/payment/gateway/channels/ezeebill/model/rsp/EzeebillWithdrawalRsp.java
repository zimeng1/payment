package com.mc.payment.gateway.channels.ezeebill.model.rsp;

import lombok.Data;

/**
 * EzeebillWithdrawalReq
 *
 * @author GZM
 * @since 2024/11/1 下午2:23
 */
@Data
public class EzeebillWithdrawalRsp {
    /**
     *调用的核心服务 API类型
     */
    private String action;
    /**
     * 支付金额
     */
    private int amount;
    /**
     * 用户名称
     */
    private String bill_to_first_name;
    /**
     * 币种
     */
    private int currency;
    /**
     * 商户应用程序使用区域代码以本地语言向持卡人显示消息
     */
    private String locale;
    /**
     * Ezeebill的商户标识符
     */
    private long merch_id;
    /**
     * 商户订单号
     */
    private long merch_order_id;
    /**
     * 商户事务ID
     */
    private long merch_txn_id;
    /**
     * 支付类型
     */
    private String pay_type;
    /**
     * 安全Hash
     */
    private String secure_hash;
    /**
     * 虚拟终端标识符
     */
    private String term_id;
    /**
     * 响应信息
     */
    private String txn_message;
    /**
     * 响应码
     */
    private int txn_response_code;
    /**
     * 响应编号
     */
    private String txn_no;
    /**
     * 响应状态
     */
    private String txn_status;
    /**
     * 版本
     */
    private String version;
}
