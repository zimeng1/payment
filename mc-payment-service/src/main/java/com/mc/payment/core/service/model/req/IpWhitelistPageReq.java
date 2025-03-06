package com.mc.payment.core.service.model.req;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mc.payment.core.service.base.BasePageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author Conor
 * @since 2024/6/3 下午6:56
 */
@Data
public class IpWhitelistPageReq extends BasePageReq {
    @TableField(value = "ip_addr")
    @Schema(description = "IP地址")
    private String ipAddr;

    @TableField(value = "status")
    @Schema(description = "IP状态,[0:禁用,1:激活]")
    private Integer status;

    @Schema(title = "创建时间-开始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTimeStart;

    @Schema(title = "创建时间-结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTimeEnd;
}
