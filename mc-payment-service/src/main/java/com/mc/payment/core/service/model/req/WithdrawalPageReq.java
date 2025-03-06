package com.mc.payment.core.service.model.req;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.mc.payment.core.service.base.BasePageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author conor
 * @since 2024/2/2 15:33:57
 */
@Data
public class WithdrawalPageReq extends BasePageReq {
    private static final long serialVersionUID = 6655136770539955551L;

    /**
     * 这里使用String不使用Long是因为JavaScript中数字的精度是有限的，Java的Long类型的数字超出了JavaScript的处理范围
     */
    @TableId(value = "id")
    protected String id;

    @Schema(title = "出金时间-开始")
    private String createTimeStart;

    @Schema(title = "出金时间-结束")
    private String createTimeEnd;

    @Schema(title = "状态,[0:已提交,1:待审核,2:余额不足,3:出金中,4:出金完成,5:已拒绝,6:出金错误,7,终止出金]")
    @TableField("`status`")
    private Integer status;

    @Schema(title = "跟踪id,申请方提供唯一跟踪ID以查询处理结果")
    private String trackingId;


    @Schema(title = "资产名称,[如:BTC]")
    private String assetName;

    @Schema(title = "商户名称")
    private String merchantName;

    @Schema(title = "来源地址")
    private String sourceAddress;

    @Schema(title = "目标地址")
    private String destinationAddress;

    @Schema(title = "用户标识")
    private String userId;
}
