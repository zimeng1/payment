package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.api.model.rsp.QueryAssetSupportedBankRsp;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.model.rsp.AssetBankDto;
import lombok.Data;

import java.io.Serializable;

/**
 * 资产支持的银行
 *
 * @TableName mcp_asset_bank
 */
@TableName(value = "mcp_asset_bank")
@Data
public class AssetBankEntity extends BaseNoLogicalDeleteEntity implements Serializable {
    /**
     * 支付类型,[0:入金,1:出金]
     */
    @TableField(value = "payment_type")
    private String paymentType;

    /**
     * 资产名称/币种
     */
    @TableField(value = "asset_name")
    private String assetName;

    /**
     * 网络协议/支付类型
     */
    @TableField(value = "net_protocol")
    private String netProtocol;

    /**
     * 银行代码
     */
    @TableField(value = "bank_code")
    private String bankCode;

    /**
     * 银行名称
     */
    @TableField(value = "bank_name")
    private String bankName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public QueryAssetSupportedBankRsp convert() {
        QueryAssetSupportedBankRsp rsp = new QueryAssetSupportedBankRsp();
        rsp.setBankCode(this.bankCode);
        rsp.setBankName(this.bankName);
        return rsp;
    }
    public AssetBankDto convertToAssetBankDto() {
        AssetBankDto dto = new AssetBankDto();
        dto.setBankCode(this.bankCode);
        dto.setBankName(this.bankName);
        return dto;
    }
}