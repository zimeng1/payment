package com.mc.payment.gateway.channels.ofapay.model.req;


import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.mc.payment.gateway.model.req.GatewayWithdrawalReq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfaPayWithdrawalReq extends OfaPayBaseReq {


    /**
     * 订单ID
     * 字段长度: 50
     * 必填字段
     * 描述: 唯一订单ID
     */
    private String orderid;

    /**
     * 金额
     * 字段长度: 12
     * 必填字段
     * 描述: 格式为 00.00，保留两位小数
     * 例如：\$10，提交的金额应为 10.00
     */
    private String money;

    /**
     * 银行名称
     * 字段长度: 50
     * 必填字段
     * 描述: 固定值：KRW
     */
    private String bankname;

    /**
     * 用户ID
     * 字段长度: 20
     * 必填字段
     * 描述: 例如：User001
     */
    private String accountno;

    /**
     * 账户持有人
     * 字段长度: 64
     * 必填字段
     * 描述: 例如：박하준
     */
    private String accountname;

    /**
     * 银行代码
     * 字段长度: 10
     * 必填字段
     * 描述: 固定值：KRW
     */
    private String bankno;
    /**
     * IFSC CODE
     * fixed value = NA
     */
    private String banknum;

    /**
     * 回调URL
     * 字段长度: 255
     * 必填字段
     * 描述: 接收订单状态
     */
    private String notifyurl;


    public static OfaPayWithdrawalReq valueOf(GatewayWithdrawalReq req) {
        OfaPayWithdrawalReq ofaPayWithdrawalReq = new OfaPayWithdrawalReq();
        ofaPayWithdrawalReq.setOrderid(req.getTransactionId());
        ofaPayWithdrawalReq.setMoney(NumberUtil.decimalFormat("#0.00", new BigDecimal(req.getAmount())));
        ofaPayWithdrawalReq.setAccountno(req.getAddress());
        ofaPayWithdrawalReq.setAccountname(StrUtil.isBlank(req.getAccountName())?"NA":req.getAccountName());// todo 不想传,但是又必须传,所以试试能不能写死,如果银行要校验就得加上这个字段,从出金收银台页面获取
        ofaPayWithdrawalReq.setNotifyurl(req.getCallbackUrl());
        ofaPayWithdrawalReq.setScode(req.getChannelId());
        ofaPayWithdrawalReq.setBankno(req.getBankCode());
        ofaPayWithdrawalReq.setBankname(StrUtil.isBlank(req.getBankName())?"NA":req.getBankName());
        ofaPayWithdrawalReq.setBanknum(StrUtil.isBlank(req.getBankNum())?"NA":req.getBankNum());
        return ofaPayWithdrawalReq;
    }
}
