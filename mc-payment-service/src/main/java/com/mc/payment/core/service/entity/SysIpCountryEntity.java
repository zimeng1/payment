package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * ip归属地记录表
 * @TableName mcp_sys_ip_country
 */
@TableName(value ="mcp_sys_ip_country")
@Data
public class SysIpCountryEntity extends BaseNoLogicalDeleteEntity implements Serializable {
    /**
     * IP地址
     */
    @TableField(value = "ip_addr")
    private String ipAddr;

    /**
     * 归属地
     */
    @TableField(value = "country_name")
    private String countryName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}