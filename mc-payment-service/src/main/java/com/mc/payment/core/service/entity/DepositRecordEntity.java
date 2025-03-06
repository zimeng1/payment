package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.api.model.req.DepositReq;
import com.mc.payment.core.service.base.BaseEntity;
import com.mc.payment.core.service.constant.AssetConstants;
import com.mc.payment.core.service.model.enums.AssetTypeEnum;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.enums.DepositAuditStatusEnum;
import com.mc.payment.core.service.model.enums.DepositRecordStatusEnum;
import com.mc.payment.core.service.model.req.DepositRequestReq;
import com.mc.payment.core.service.model.req.ProcessDepositReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <p>
 * 入金记录表
 * </p>
 *
 * @author conor
 * @since 2024-04-17 18:00:15
 */
@Getter
@Setter
@TableName("mcp_deposit_record")
@Schema(title = "DepositRecordEntity对象", description = "入金记录表")
public class DepositRecordEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 9999-01-01 年的毫秒时间戳
     */
    private static final Long MAX_DATE_TIMESTAMP = 253370736000000L;


    @Schema(title = "资产名称,[如:BTC]")
    @TableField("asset_name")
    private String assetName;

    @Schema(title = "资产网络")
    @TableField("asset_net")
    private String assetNet;

    @Schema(title = "网络协议")
    @TableField("net_protocol")
    private String netProtocol;

    @Schema(title = "来源地址")
    @TableField("source_address")
    private String sourceAddress;

    @Schema(title = "目标地址")
    @TableField("destination_address")
    private String destinationAddress;


    @Schema(title = "商户id")
    @TableField("merchant_id")
    private String merchantId;

    @Schema(title = "商户名称")
    @TableField("merchant_name")
    private String merchantName;

    @Schema(title = "金额")
    @TableField("amount")
    private BigDecimal amount;

    @Schema(title = "已入金金额")
    @TableField("accumulated_amount")
    private BigDecimal accumulatedAmount;

    @Schema(title = "Gas费")
    @TableField("gas_fee")
    private BigDecimal gasFee;

    @Schema(title = "通道费")
    @TableField("channel_fee")
    private BigDecimal channelFee;

    @Schema(title = "状态,[0:待入金,1:部分入金,2:完全入金,3:撤销入金,4:请求失效]")
    @TableField("`status`")
    private Integer status;

    @Schema(title = "跟踪id,申请方提供唯一跟踪ID以查询处理结果")
    @TableField("tracking_id")
    private String trackingId;

    @Schema(title = "备注说明")
    @TableField("remark")
    private String remark;

    @Schema(title = "失效时间戳-精确毫秒")
    @TableField("expire_timestamp")
    private Long expireTimestamp;

    @Schema(title = "通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    @TableField("channel_sub_type")
    private Integer channelSubType;

    @Schema(title = "账号id")
    @TableField("account_id")
    private String accountId;

    @Schema(title = "钱包id")
    @TableField("wallet_id")
    private String walletId;

    @Schema(title = "目标地址-余额")
    @TableField("addr_balance")
    private BigDecimal addrBalance;

    @Schema(title = "手续费资产名称,[如:USDT 出金就需用到 ETH 的手续费]")
    @TableField("fee_asset_name")
    private String feeAssetName;

    @Schema(title = "当时币种转换为USDT/USD的汇率")
    @TableField("rate")
    private BigDecimal rate;

    @Schema(title = "汇率目标货币", description = "汇率转换的目标货币")
    @TableField("target_currency")
    private String targetCurrency;

    @Schema(title = "当时手续费币种转换为USDT的汇率")
    @TableField("fee_rate")
    private BigDecimal feeRate;

    @Schema(title = "资产类型,[0:加密货币,1:法币]")
    @TableField("asset_type")
    private Integer assetType;

    @Schema(title = "通知回调地址/webhook url")
    @TableField("webhook_url")
    private String webhookUrl;

    @Schema(title = "入金成功跳转页面地址")
    @TableField("success_page_url")
    private String successPageUrl;

    @Schema(title = "入金业务名称, 比如商品名称/业务名称 eg: xxx报名费")
    @TableField("business_name")
    private String businessName;

    @Schema(title = "用户是否可选,[0:否,1:是]", description = "可选时：金额,资产名称/币种，网络类型/支付类型等字段可先不填，由收银页提交时指定")
    @TableField("user_selectable")
    private Integer userSelectable;

    @Schema(title = "银行代码", description = "某些币种的支付类型需要")
    @TableField("bank_code")
    private String bankCode;

    @Schema(title = "用户id")
    @TableField("user_id")
    private String userId;

    @Schema(title = "用户ip地址")
    @TableField("user_ip")
    private String userIp;

    @Schema(title = "停留原因")
    @TableField("stay_reason")
    private String stayReason;

    @Schema(title = "入金审核状态,[1审核通过 2审核不通过]")
    @TableField("`audit_status`")
    private Integer auditStatus;

    @Schema(title = "第三方通道交易id")
    @TableField("`channel_transaction_id`")
    private String channelTransactionId;

    public static DepositRecordEntity valueOf(DepositRequestReq req) {
        DepositRecordEntity entity = new DepositRecordEntity();
        entity.setAssetName(req.getAssetName());
        entity.setNetProtocol(req.getNetProtocol());
        entity.setAmount(req.getAmount());
        entity.setRemark(req.getRemark());
        entity.setTrackingId(req.getTrackingId());
        entity.setChannelSubType(req.getChannelSubType());
        entity.setUserId(req.getUserId());
        entity.setUserIp(req.getUserIp());
        return entity;
    }

    public static DepositRecordEntity valueOf(DepositReq req, String merchantId, String merchantName,
                                              Integer channelSubType) {
        DepositRecordEntity entity = new DepositRecordEntity();
        entity.setAssetName(req.getAssetName());
        //entity.setAssetNet();
        entity.setNetProtocol(req.getNetProtocol());
        //entity.setSourceAddress("");
        //entity.setDestinationAddress("");
        entity.setMerchantId(merchantId);
        entity.setMerchantName(merchantName);
        entity.setAmount(req.getAmount());
        entity.setAccumulatedAmount(BigDecimal.ZERO);
        entity.setGasFee(BigDecimal.ZERO);
        entity.setChannelFee(BigDecimal.ZERO);
        entity.setStatus(DepositRecordStatusEnum.ITEM_0.getCode());
        entity.setTrackingId(req.getTrackingId());
        entity.setRemark(req.getRemark());
        // 0表示永久有效(9999-01-01过期),大于0的数值表示有限的有效时长(最大支持7天)
        if (req.getActiveTime() == null || req.getActiveTime() == 0) {
            entity.setExpireTimestamp(MAX_DATE_TIMESTAMP);
        } else {
            entity.setExpireTimestamp(System.currentTimeMillis() + req.getActiveTime());
        }
        entity.setChannelSubType(channelSubType);
        entity.setAccountId("0");
        entity.setWalletId("0");
        entity.setAddrBalance(BigDecimal.ZERO);
        // 默认当前币种
        // entity.setFeeAssetName(req.getAssetName());
        entity.setTargetCurrency(req.getAssetType() == AssetTypeEnum.CRYPTO_CURRENCY.getCode() ?
                AssetConstants.AN_USDT : AssetConstants.AN_USD);
        // 提现确认时记录汇率 只是方便查账时页面观看的,后续调整为实时的,这里就可以删掉了
        entity.setRate(BigDecimal.ZERO);
        entity.setFeeRate(BigDecimal.ZERO);
        entity.setAssetType(req.getAssetType());
        entity.setWebhookUrl(req.getWebhookUrl());
        entity.setSuccessPageUrl(req.getSuccessPageUrl());
        entity.setBusinessName(req.getBusinessName());
        entity.setUserSelectable(req.getUserSelectable());
        entity.setBankCode(req.getBankCode());
        entity.setUserId(req.getUserId());
        entity.setUserIp(req.getUserIp());
//        entity.setStayReason("");
        entity.setAuditStatus(DepositAuditStatusEnum.ITEM_0.getCode());
        return entity;
    }

    public static DepositRecordEntity valueOf(DepositReq req, String merchantId, String merchantName,
                                              ChannelSubTypeEnum channelSubTypeEnum) {
        return valueOf(req, merchantId, merchantName, channelSubTypeEnum.getCode());
    }

    public static DepositRecordEntity valueOf(ProcessDepositReq req) {
        return valueOf(req, req.getMerchantId(), req.getMerchantName(), req.getChannelSubTypeEnum());
    }


}
