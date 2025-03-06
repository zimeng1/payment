package com.mc.payment.core.service.model.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mc.crm.common.dto.authority.RoleRequest;
import com.mc.payment.core.service.base.BasePageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.ZoneId;
import java.util.Date;

@Data
public class SysRolePageReq extends BasePageReq {
    @Schema(name = "roleName", description = "角色名称")
    private String roleName;

    @Schema(name = "deleted", description = "状态：0:启用,1禁用")
    private Integer deleted;

    @Schema(name = "createTimeStart", description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTimeStart;

    @Schema(name = "createTimeEnd", description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTimeEnd;


    public RoleRequest convert() {
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setSubSystemCode("000005");// payment 在crm中配置的子系统编号
        roleRequest.setRoleName(this.roleName);
        roleRequest.setDeleted(this.deleted);
        roleRequest.setCreateTimeStart(this.createTimeStart.toInstant().atZone(ZoneId.of("Asia/Shanghai")).toLocalDate());
        roleRequest.setCreateTimeEnd(this.createTimeEnd.toInstant().atZone(ZoneId.of("Asia/Shanghai")).toLocalDate());
        roleRequest.setPageCurrent(Long.valueOf(super.current).intValue());
        roleRequest.setPageSize(Long.valueOf(super.size).intValue());
        return roleRequest;
    }
}
