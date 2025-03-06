package com.mc.payment.gateway.model.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class GatewayQueryWithdrawalRsp {
    /**
     * 交易状态,-1:失败,0:处理中,1:成功
     *
     * 如果显示处理中,请再次检查，直到出现最终状态
     */
    private int status;
    /**
     * 交易完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date completeTime;
}
