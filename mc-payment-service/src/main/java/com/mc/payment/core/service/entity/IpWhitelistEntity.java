package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.core.service.base.BaseEntity;
import com.mc.payment.core.service.model.req.IpWhitelistSaveReq;
import com.mc.payment.core.service.model.req.IpWhitelistUpdateReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * ip白名单
 * @TableName mcp_ip_whitelist
 */
@TableName(value ="mcp_ip_whitelist")
@Data
public class IpWhitelistEntity extends BaseEntity implements Serializable {

    @TableField(value = "ip_addr")
    @Schema(description = "IP地址")
    private String ipAddr;

    @TableField(value = "remark")
    @Schema(description = "IP地址备注")
    private String remark;

    @TableField(value = "status")
    @Schema(description = "IP状态,[0:禁用,1:激活]")
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public static IpWhitelistEntity valueOf(IpWhitelistSaveReq req) {
        IpWhitelistEntity ipWhitelistEntity = new IpWhitelistEntity();
        ipWhitelistEntity.setIpAddr(req.getIpAddr());
        ipWhitelistEntity.setRemark(req.getRemark());
        ipWhitelistEntity.setStatus(req.getStatus());
        return ipWhitelistEntity;
    }

    public static IpWhitelistEntity valueOf(IpWhitelistUpdateReq req) {
        IpWhitelistEntity ipWhitelistEntity = new IpWhitelistEntity();
        ipWhitelistEntity.setRemark(req.getRemark());
        ipWhitelistEntity.setStatus(req.getStatus());
        ipWhitelistEntity.setId(req.getId());
        return ipWhitelistEntity;
    }

}