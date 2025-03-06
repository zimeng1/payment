package com.mc.payment.core.service.model.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mc.payment.core.service.base.BasePageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * ChannelWalletQueryLogPageReq
 *
 * @author GZM
 * @since 2024/10/12 下午2:50
 */
@Data
@Schema(title = "通道钱包-分页查询日志参数实体")
public class ChannelWalletQueryLogPageReq extends BasePageReq {

    @Schema(title = "钱包id")
    private String walletId;

    @Schema(title = "钱包创建时间-起始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date walletCreateTimeLeft;

    @Schema(title = "钱包创建时间-结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date walletCreateTimeRight;

    @Schema(title = "钱包更新时间-起始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date walletUpdateTimeLeft;

    @Schema(title = "钱包更新时间-结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date walletUpdateTimeRight;

    @Schema(title = "变动余额")
    private BigDecimal changeBalance;

    @Schema(title = "变动冻结")
    private BigDecimal changeFreezeAmount;

}
