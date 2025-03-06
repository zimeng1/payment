package com.mc.payment.api.model.rsp;

import com.mc.payment.api.model.dto.WithdrawalDetailDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryWithdrawalRsp {
    @Schema(title = "商户跟踪id")
    private String trackingId;

    @Schema(title = "出金状态,[0:已提交,1:待审核,2:余额不足,3:出金中,4:出金完成,5:已拒绝,6:出金错误,7,终止出金]", example = "1")
    private Integer status;

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

    @Schema(title = "商户名称")
    private String merchantName;

    @Schema(title = "金额")
    private BigDecimal amount;

    @Schema(title = "txHash")
    private String txHash;

    @Schema(title = "审核状态,[1:通过,2:不通过,3:终止执行,4:重新执行]")
    private Integer auditStatus;

    @Schema(title = "备注说明")
    private String remark;

    @Schema(title = "资产类型,[0:加密货币,1:法币]")
    private Integer assetType;

    @Schema(title = "通知回调地址/webhook url")
    private String webhookUrl;

    @Schema(title = "银行代码", description = "某些币种的支付类型需要")
    private String bankCode;

    @Schema(title = "银行名称", description = "某些币种的支付类型需要")
    private String bankName;

    @Schema(title = "持卡人姓名", description = "某些币种的支付类型需要")
    private String accountName;

    @Schema(title = "IFSCcode", description = "INR币种需要,叫印度金融系统代码")
    private String bankNum;

    @Schema(title = "用户id")
    private String userId;

    @Schema(title = "用户ip地址")
    private String userIp;

    @Schema(title = "停留原因")
    private String stayReason;

    @Schema(title = "出金记录明细")
    private List<WithdrawalDetailDto> withdrawalDetailDtoList;


    //虚拟货币参数
    @Schema(title = "商户id")
    private String merchantId;

    @Schema(title = "Gas费")
    private BigDecimal gasFee;

    @Schema(title = "通道费")
    private BigDecimal channelFee;

    @Schema(title = "通道子类型,[0:BlockATM,1:FireBlocks]")
    private Integer channelSubType;

    @Schema(title = "是否自动审核,[0:否,1:是]")
    private Integer autoAudit;

}
