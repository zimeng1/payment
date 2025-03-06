package com.mc.payment.core.service.model.rsp;

import com.baomidou.mybatisplus.annotation.TableId;
import com.mc.payment.core.service.entity.DepositRecordEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Conor
 * @since 2024/4/13 上午11:33
 */
@Data
public class DepositQueryRsp {
    @TableId(value = "id")
    protected String id;

    @Schema(title = "资产名称,[如:BTC]")
    private String assetName;

    @Schema(title = "资产网络")
    private String assetNet;

    @Schema(title = "网络协议")
    private String netProtocol;

    @Schema(title = "来源地址")
    private String sourceAddress;

    @Schema(title = "目标地址")
    private String destinationAddress;

    @Schema(title = "商户id")
    private String merchantId;

    @Schema(title = "商户名称")
    private String merchantName;

    @Schema(title = "金额")
    private BigDecimal amount;

    @Schema(title = "已入金金额")
    private BigDecimal accumulatedAmount;

    @Schema(title = "Gas费")
    private BigDecimal gasFee;

    @Schema(title = "通道费")
    private BigDecimal channelFee;

    @Schema(title = "状态,[0:待入金,1:部分入金,2:完全入金,3:撤销入金,4:请求失效]")
    private Integer status;

    @Schema(title = "跟踪id,申请方提供唯一跟踪ID以查询处理结果")
    private String trackingId;

    @Schema(title = "备注说明")
    private String remark;

    @Schema(title = "失效时间戳-精确毫秒")
    private Long expireTimestamp;

    @Schema(title = "详情")
    private List<DepositDetailQueryRsp> details;

    public static DepositQueryRsp valueOf(DepositRecordEntity entity) {
        DepositQueryRsp rsp = new DepositQueryRsp();
        rsp.setId(entity.getId());
        rsp.setAssetName(entity.getAssetName());
        rsp.setAssetNet(entity.getAssetNet());
        rsp.setNetProtocol(entity.getNetProtocol());
        rsp.setSourceAddress(entity.getSourceAddress());
        rsp.setDestinationAddress(entity.getDestinationAddress());
        rsp.setMerchantId(entity.getMerchantId());
        rsp.setMerchantName(entity.getMerchantName());
        rsp.setAmount(entity.getAmount());
        rsp.setAccumulatedAmount(entity.getAccumulatedAmount());
        rsp.setGasFee(entity.getGasFee());
        rsp.setChannelFee(entity.getChannelFee());
        rsp.setStatus(entity.getStatus());
        rsp.setTrackingId(entity.getTrackingId());
        rsp.setRemark(entity.getRemark());
        rsp.setExpireTimestamp(entity.getExpireTimestamp());
        return rsp;
    }

}
