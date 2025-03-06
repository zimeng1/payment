package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.core.service.base.BaseEntity;
import com.mc.payment.core.service.model.enums.AccountStatusEnum;
import com.mc.payment.core.service.model.enums.AccountTypeEnum;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.req.AccountSaveReq;
import com.mc.payment.core.service.model.req.AccountUpdateReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 账号管理
 * </p>
 *
 * @author conor
 * @since 2024-02-02 15:30:01
 */
@Getter
@Setter
@TableName("mcp_account")
@Schema(title = "AccountEntity对象", description = "账号管理")
public class AccountEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(title = "账户名")
    @TableField("name")
    private String name;

    @Schema(title = "账户签约的商户的ID")
    @TableField("merchant_id")
    private String merchantId;

    @Schema(title = "通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    @TableField("channel_sub_type")
    private Integer channelSubType;

    // todo 优化为 purposeType 用途类型
    @Schema(title = "账户类型,[0:入金账户,1:出金账户]")
    @TableField("account_type")
    private Integer accountType;

    @Schema(title = "外部系统账号id", description = "比如fireblocks创建去账号返回的钱包id就存这儿")
    @TableField("external_id")
    private String externalId;

    /**
     * 状态,[0:待生成,1:生成中,2:生成失败,3:生成成功]
     */
    @Schema(title = "状态,[0:待生成,1:生成中,2:生成失败,3:生成成功]")
    @TableField(value = "status")
    private Integer status;

    /**
     * 状态描述
     */
    @Schema(title = "状态描述")
    @TableField(value = "status_msg")
    private String statusMsg;

    @Schema(title = "账户类型-描述")
    public String getAccountTypeDesc() {
        return AccountTypeEnum.getEnumDesc(accountType);
    }

    @Schema(title = "通道子类型-描述")
    public String getChannelSubTypeDesc() {
        return ChannelSubTypeEnum.getEnumDesc(channelSubType);
    }

    @Schema(title = "状态-描述")
    public String getStatusDesc() {
        return AccountStatusEnum.getEnumDesc(status);
    }

    //============
    public static AccountEntity valueOf(AccountSaveReq req) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setName(req.getName());
        accountEntity.setChannelSubType(req.getChannelSubType());
        accountEntity.setMerchantId(req.getMerchantId());
        accountEntity.setAccountType(req.getAccountType());
        accountEntity.setExternalId(req.getExternalId());
        return accountEntity;
    }

    public static AccountEntity valueOf(AccountUpdateReq req) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(req.getId());
        accountEntity.setName(req.getName());
        accountEntity.setChannelSubType(req.getChannelSubType());
        accountEntity.setMerchantId(req.getMerchantId());
        accountEntity.setAccountType(req.getAccountType());
        accountEntity.setExternalId(req.getExternalId());
        return accountEntity;
    }


}
