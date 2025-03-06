package com.mc.payment.core.service.entity;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.api.model.req.WithdrawalReq;
import com.mc.payment.core.service.base.BaseEntity;
import com.mc.payment.core.service.constant.AssetConstants;
import com.mc.payment.core.service.model.enums.AssetTypeEnum;
import com.mc.payment.core.service.model.enums.BooleanStatusEnum;
import com.mc.payment.core.service.model.enums.WithdrawalAuditStatusEnum;
import com.mc.payment.core.service.model.enums.WithdrawalRecordStatusEnum;
import com.mc.payment.core.service.model.req.ProcessWithdrawalReq;
import com.mc.payment.core.service.model.req.WithdrawalRequestReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * <p>
 * 出金记录表
 * </p>
 *
 * @author conor
 * @since 2024-04-18 15:54:30
 */
@Data
@TableName("mcp_withdrawal_record")
@Schema(title = "WithdrawalRecordEntity对象", description = "出金记录表")
public class WithdrawalRecordEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

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

    @Schema(title = "txHash")
    @TableField("tx_hash")
    private String txHash;

    @Schema(title = "Gas费")
    @TableField("gas_fee")
    private BigDecimal gasFee;

    @Schema(title = "通道费")
    @TableField("channel_fee")
    private BigDecimal channelFee;

    @Schema(title = "出金状态,[0:已提交,1:待审核,2:余额不足,3:出金中,4:出金完成,5:已拒绝,6:出金错误,7终止出金]")
    @TableField("status")
    private Integer status;

    @Schema(title = "审核状态,[1:通过,2:不通过,3:终止执行,4:重新执行]")
    @TableField("audit_status")
    private Integer auditStatus;

    @Schema(title = "跟踪id,申请方提供唯一跟踪ID以查询处理结果")
    @TableField("tracking_id")
    private String trackingId;

    @Schema(title = "备注说明")
    @TableField("remark")
    private String remark;

    @Schema(title = "通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    private Integer channelSubType;

    @Schema(title = "是否自动审核,[0:否,1:是]")
    private Integer autoAudit;

    @Schema(title = "账号id")
    @TableField("account_id")
    private String accountId;

    @Schema(title = "钱包id")
    @TableField("wallet_id")
    private String walletId;

    @Schema(title = "冻结的预估手续费+平台费")
    @TableField("freeze_es_fee")
    private BigDecimal freezeEsFee;

    @Schema(title = "冻结的钱包id(如果同种, freeze_wallet_id = wallet_id)")
    @TableField("freeze_wallet_id")
    private String freezeWalletId;

    /**
     * txid 可以用于调用fireblocks的fireblocks.transactions().getTransaction(txId)查询交易详情接口
     */
    @Schema(title = "fireblocks返回的交易id")
    @TableField("transaction_id")
    private String transactionId;

    @Schema(title = "来源地址-余额")
    @TableField("addr_balance")
    private BigDecimal addrBalance;

    @Schema(title = "通道资产名称")
    @TableField("channel_asset_name")
    private String channelAssetName;

    @Deprecated
    @Schema(title = "手续费的通道资产名称")
    @TableField("fee_channel_asset_name")
    private String feeChannelAssetName;

    @Schema(title = "资产名称,[如:BTC]")
    @TableField("fee_asset_name")
    private String feeAssetName;

    @Schema(title = "当时币种转换为USDT的汇率")
    @TableField("rate")
    private BigDecimal rate;

    @Schema(title = "当时手续费币种转换为USDT的汇率")
    @TableField("fee_rate")
    private BigDecimal feeRate;

    @Schema(title = "汇率目标货币", description = "汇率转换的目标货币")
    @TableField("target_currency")
    private String targetCurrency;

    @Schema(title = "资产类型,[0:加密货币,1:法币]")
    @TableField("asset_type")
    private Integer assetType;

    @Schema(title = "通知回调地址/webhook url")
    @TableField("webhook_url")
    private String webhookUrl;

    @Schema(title = "银行代码", description = "某些币种的支付类型需要")
    @TableField("bank_code")
    private String bankCode;

    @Schema(title = "银行名称", description = "某些币种的支付类型需要")
    @TableField("bank_name")
    private String bankName;

    @Schema(title = "持卡人姓名", description = "某些币种的支付类型需要")
    @TableField("account_name")
    private String accountName;

    @Schema(title = "IFSCcode", description = "INR币种需要,叫印度金融系统代码,其余币种默认:NA")
    @TableField("bank_num")
    private String bankNum;

    @Schema(title = "用户id")
    @TableField("user_id")
    private String userId;

    @Schema(title = "用户ip地址")
    @TableField("user_ip")
    private String userIp;

    @Schema(title = "停留原因")
    @TableField("stay_reason")
    private String stayReason;

    @Schema(title = "额外json格式参数")
    @TableField("extra_map")
    private String extraMap;

    public static WithdrawalRecordEntity valueOf(WithdrawalRequestReq req) {
        WithdrawalRecordEntity entity = new WithdrawalRecordEntity();
        entity.setAssetName(req.getAssetName());
        entity.setDestinationAddress(req.getAddress());
        entity.setAmount(req.getAmount());
        entity.setRemark(req.getRemark());
        entity.setTrackingId(req.getTrackingId());
        entity.setChannelSubType(req.getChannelSubType());
        entity.setAutoAudit(req.getAutoAudit());
        entity.setNetProtocol(req.getNetProtocol());
        entity.setUserId(req.getUserId());
        entity.setUserIp(req.getUserIp());
        //ps: freezeEsFee和freezeWalletId是自己查的
        return entity;
    }

    public static WithdrawalRecordEntity valueOf(WithdrawalReq req) {
        WithdrawalRecordEntity entity = new WithdrawalRecordEntity();
        entity.setAssetName(req.getAssetName());
        entity.setDestinationAddress(req.getAddress());
        entity.setAmount(req.getAmount());
        entity.setRemark(req.getRemark());
        entity.setTrackingId(req.getTrackingId());

        //fireBlocks迁移之后默认自动审核
        entity.setAutoAudit(1);
        entity.setNetProtocol(req.getNetProtocol());
        entity.setUserId(req.getUserId());
        entity.setUserIp(req.getUserIp());
        //ps: freezeEsFee和freezeWalletId是自己查的
        entity.setWebhookUrl(req.getWebhookUrl());
        return entity;
    }

    public static WithdrawalRecordEntity valueOf(ProcessWithdrawalReq req) {
        return valueOf(req, req.getMerchantId(), req.getMerchantName(), req.getChannelSubTypeEnum().getCode());
    }

    public static WithdrawalRecordEntity valueOf(WithdrawalReq req, String merchantId, String merchantName, Integer channelSubType) {
        WithdrawalRecordEntity recordEntity = new WithdrawalRecordEntity();
        recordEntity.setAssetName(req.getAssetName());
        recordEntity.setNetProtocol(req.getNetProtocol());
        recordEntity.setDestinationAddress(req.getAddress());
        recordEntity.setMerchantId(merchantId);
        recordEntity.setMerchantName(merchantName);
        recordEntity.setAmount(req.getAmount());
        recordEntity.setStatus(WithdrawalRecordStatusEnum.ITEM_0.getCode());
        recordEntity.setAuditStatus(WithdrawalAuditStatusEnum.ITEM_0.getCode());
        recordEntity.setTrackingId(req.getTrackingId());
        recordEntity.setRemark(req.getRemark());
        recordEntity.setChannelSubType(channelSubType);
        // 默认自动审核
        recordEntity.setAutoAudit(BooleanStatusEnum.ITEM_1.getCode());
        recordEntity.setTargetCurrency(req.getAssetType() == AssetTypeEnum.CRYPTO_CURRENCY.getCode() ? AssetConstants.AN_USDT : AssetConstants.AN_USD);
        recordEntity.setAssetType(req.getAssetType());
        recordEntity.setWebhookUrl(req.getWebhookUrl());
        recordEntity.setBankCode(req.getBankCode());
        recordEntity.setBankName(req.getBankName());
        recordEntity.setAccountName(req.getAccountName());
        recordEntity.setBankNum(req.getBankNum());
        recordEntity.setUserId(req.getUserId());
        recordEntity.setUserIp(req.getUserIp());
        if (!Objects.isNull(req.getExtraMap())) {
            recordEntity.setExtraMap(JSONUtil.toJsonStr(req.getExtraMap()));
        }
        // 以下字段需后续流程填充
//        recordEntity.setSourceAddress();
//        recordEntity.setAccountId();
//        recordEntity.setWalletId();
//        recordEntity.setFreezeEsFee();
//        recordEntity.setFreezeWalletId();
//        recordEntity.setTransactionId();
//        recordEntity.setAddrBalance();
//        recordEntity.setChannelAssetName();
//        recordEntity.setFeeChannelAssetName();
//        recordEntity.setFeeAssetName();
//        recordEntity.setRate();
//        recordEntity.setFeeRate();
//        recordEntity.setTxHash();
//        recordEntity.setGasFee();
//        recordEntity.setChannelFee();
//        recordEntity.setStayReason();
        //无需填充
//        recordEntity.setAssetNet(); 废弃字段
//        withdrawalRecordEntity.setDeleted();
//        withdrawalRecordEntity.setId();
//        withdrawalRecordEntity.setCreateBy();
//        withdrawalRecordEntity.setCreateTime();
//        withdrawalRecordEntity.setUpdateBy();
//        withdrawalRecordEntity.setUpdateTime();
        return recordEntity;
    }
}
