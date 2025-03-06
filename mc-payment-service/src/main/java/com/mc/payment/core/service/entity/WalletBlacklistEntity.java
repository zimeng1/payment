package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author conor
 * @since 2024-05-16 15:40:12
 */
@Getter
@Setter
@TableName("mcp_wallet_blacklist")
@Schema(title = "WalletBlacklistEntity对象", description = "")
public class WalletBlacklistEntity extends BaseNoLogicalDeleteEntity {

    private static final long serialVersionUID = 1L;

    @Schema(title = "钱包地址")
    @TableField("wallet_address")
    private String walletAddress;

    @Schema(title = "加入黑名单的原因")
    @TableField("reason")
    private String reason;


}
