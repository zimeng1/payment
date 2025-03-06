package com.mc.payment.core.service.model.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mc.payment.core.service.base.BasePageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author conor
 * @since 2024/01/26 14:56
 */
@Data
public class UserPageReq extends BasePageReq {

    @Schema(title = "账号名称")
    private String userName;

    @Schema(title = "登录账号")
    private String userAccount;

    @Schema(title = "角色编码")
    private String roleCode;

    @Schema(title = "账号状态, 0:启动, 1:禁用")
    private Integer status;

    @Schema(title = "创建时间-开始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTimeStart;

    @Schema(title = "创建时间-结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTimeEnd;

}
