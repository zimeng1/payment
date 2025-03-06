package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.core.service.base.BaseEntity;
import com.mc.payment.core.service.model.req.WalletSaveReq;
import com.mc.payment.core.service.model.req.WalletUpdateReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author conor
 * @since 2024-04-15 11:07:58
 */
@Getter
@Setter
@TableName("mcp_wallet")
@Schema(title = "WalletEntity对象", description = "")
public class WalletEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(title = "账户签约的商户的ID")
    @TableField("merchant_id")
    private String merchantId;

    @Schema(title = "账号id")
    @TableField("account_id")
    private String accountId;

    /**
     * todo 考虑删除该状态，使用balance和freezeAmount来判断，锁定则是根据入金申请记录来判断
     * <p>
     * 转入时 会锁定钱包一段时间不能给其他的转入业务使用
     * 转出时 会冻结一部分金额,如果可用金额(余额-冻结金额)仍然满足转出金额,则还能给转出业务使用
     */
    @Schema(title = "状态,[0:可用,1:锁定,2:冻结]")
    protected Integer status;

    @Schema(title = "资产名称,[如:BTC]")
    @TableField("asset_name")
    private String assetName;

    @Schema(title = "网络协议")
    @TableField("net_protocol")
    private String netProtocol;

    @Schema(title = "通道资产名称")
    @TableField("channel_asset_name")
    private String channelAssetName;

    @Schema(title = "钱包地址")
    @TableField("wallet_address")
    private String walletAddress;

    @Schema(title = "余额")
    @TableField("balance")
    private BigDecimal balance;

    @Schema(title = "冻结金额= 出金金额 或者 =(预估手续费estimateFee+平台费channelCost)")
    @TableField("freeze_amount")
    private BigDecimal freezeAmount;

    @Schema(title = "私钥")
    @TableField("private_key")
    private String privateKey;

    @Schema(title = "备注")
    @TableField("remark")
    private String remark;
    @Schema(title = "资产类型,[0:加密货币,1:法币]")
    @TableField("asset_type")
    private Integer assetType;
    @Schema(title = "外部系统钱包id", description = "比如fireblocks创建去钱包返回的钱包id就存这儿")
    @TableField("external_id")
    private String externalId;

    public static WalletEntity valueOf(WalletSaveReq req) {
        WalletEntity entity = new WalletEntity();
        entity.setAccountId(req.getAccountId());
        entity.setAssetName(req.getAssetName());
        entity.setWalletAddress(req.getWalletAddress());
        entity.setBalance(req.getBalance());
        entity.setPrivateKey(req.getPrivateKey());
        entity.setRemark(req.getRemark());
        entity.setExternalId(req.getExternalId());
        return entity;
    }

    public static WalletEntity valueOf(WalletUpdateReq req) {
        WalletEntity entity = new WalletEntity();
        entity.setAccountId(req.getAccountId());
        entity.setAssetName(req.getAssetName());
        entity.setWalletAddress(req.getWalletAddress());
        entity.setBalance(req.getBalance());
        entity.setPrivateKey(req.getPrivateKey());
        entity.setExternalId(req.getExternalId());
        entity.setId(req.getId());
        return entity;
    }
}
