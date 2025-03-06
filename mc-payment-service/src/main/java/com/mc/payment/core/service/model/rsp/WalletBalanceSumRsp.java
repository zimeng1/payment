package com.mc.payment.core.service.model.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Conor
 * @since 2024/4/17 下午1:41
 */
@Data
public class WalletBalanceSumRsp {

    @Schema(title = "账户类型,[0:入金账户,1:出金账户]")
    private String accountType;

    @Schema(title = "资产名称")
    private String assetName;

    @Schema(title = "网络协议")
    private String netProtocol;

    @Schema(title = "余额合计")
    private BigDecimal balance;

    @Schema(title = "汇总时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sumTime;
}
