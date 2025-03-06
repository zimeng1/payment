package com.mc.payment.core.service.model.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mc.payment.common.base.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Marty
 * @since 2024/5/8 10:32
 */
@Data
public class MerchantQueryReq extends BaseReq {

    @Schema(title = "商户id")
    private List<String> merchantIdList;

    @Schema(title = "账户类型,[0:入金账户,1:出金账户]")
    private List<Integer> accountTypeList;

    @Schema(title = "账户id")
    private List<String> accountIdList;

    @Schema(title = "资产名称,[如:BTC]")
    private List<String> assetNameList;

    @Schema(title = "钱包地址(这里是钱包id)")
    private List<String> addrList;

    @Schema(title = "用户标识")
    private List<String> userIdList;

    @Schema(title = "金额类型,[0:入金, 1:出金, 2:链上交易费, 3:通道费]")
    private Integer amountType;

    @Schema(title = "柱状图需要展示多少天的数据.默认7天")
    private Integer showDayNum = 7;

    @Schema(title = "查询时间-开始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date timeStart;

    @Schema(title = "查询时间-结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date timeEnd;

}
