package com.mc.payment.core.service.model.req;

import com.mc.payment.core.service.model.enums.ChangeEventTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class MerchantWalletLogReq {

    @Schema(title = "钱包id")
    private String walletId;

    /**
     * 变动事件类型,[0:入金,1:出金,2:fireblocks通道钱包同步]
     * @see com.mc.payment.core.service.model.enums.ChangeEventTypeEnum
     */
    @Schema(title = "变动事件类型,[0:入金,1:出金,2:fireblocks通道钱包同步]")
    private ChangeEventTypeEnum changeEventTypeEnum;


    @Schema(title = "变动余额")
    private BigDecimal changeBalance;


    @Schema(title = "变动冻结金额")
    private BigDecimal changeFreezeAmount;


    @Schema(title = "钱包更新时间")
    private Date walletUpdateTime;

    @Schema(title = "钱包更新原因")
    private String walletUpdateMsg;

    /**
     * changeEventType=0:入金记录id
     * changeEventType=1:出金记录id
     * changeEventType=2:通道钱包变更日志id
     */
    @Schema(title = "关联id,具体由变动事件类型决定")
    private String correlationId;



}
