package com.mc.payment.gateway.channels.ofapay.model.req;

import cn.hutool.core.util.NumberUtil;
import com.mc.payment.gateway.model.req.GatewayDepositReq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfaPayDepositReq extends OfaPayBaseReq {


    /**
     * 订单的唯一自生成ID
     * 字段长度: 50
     * 必填字段
     * 描述: 订单的唯一自生成ID
     */
    private String orderid;

    /**
     * 支付类型
     * 字段长度: 10
     * 必填字段
     * 描述: 参考附录2：支付类型列表
     */
    private String paytype;

    /**
     * 金额
     * 字段长度: 12
     * 必填字段
     * 描述: 格式为00.00，保留两位小数
     * <p>
     * 最小入金金额:
     * 韩元:100000.00
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
     * 用户ID
     * 字段长度: 24
     * 必填字段
     */
    private String userid;

    /**
     * 账户名称
     * 必填字段
     * 描述: 例如：박하준
     */
    private String accountname;

    /**
     * 备注
     * 字段长度: 255
     */
    private String memo;
    /**
     * 银行代码
     */
    private String bankno;

    /**
     * 重定向页面
     * 字段长度: 50
     * 必填字段
     * 描述: 固定值："0"
     */
    private String redirectpage;

    /**
     * 通知地址
     * 字段长度: 255
     * 必填字段
     * 描述: 成功交易后的服务回调地址
     */
    private String noticeurl;

    /**
     * 回调地址
     * 必填字段
     * 描述: 存款完成后的返回页面
     */
    private String callbackurl;

    public static OfaPayDepositReq valueOf(GatewayDepositReq req) {
        OfaPayDepositReq ofaPayDepositReq = new OfaPayDepositReq();
        ofaPayDepositReq.setOrderid(req.getTransactionId());
        ofaPayDepositReq.setPaytype(req.getPayType());
        ofaPayDepositReq.setAmount(NumberUtil.decimalFormat("#0.00", new BigDecimal(req.getAmount())));
        ofaPayDepositReq.setProductname(req.getBusinessName());
        ofaPayDepositReq.setCurrency(req.getCurrency());
        ofaPayDepositReq.setUserid("userId");
        ofaPayDepositReq.setAccountname("accountName");
        ofaPayDepositReq.setBankno(req.getBankCode());
        ofaPayDepositReq.setMemo(req.getRemark());
        ofaPayDepositReq.setRedirectpage("0");
        ofaPayDepositReq.setNoticeurl(req.getCallbackUrl());
        ofaPayDepositReq.setCallbackurl(req.getSuccessPageUrl());
        ofaPayDepositReq.setScode(req.getChannelId());
        return ofaPayDepositReq;
    }
}
