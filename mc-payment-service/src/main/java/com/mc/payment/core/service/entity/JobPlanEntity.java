package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.model.enums.JobPlanStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.Date;

/**
 * <p>
 * 定时任务计划表
 * </p>
 *
 * @author conor
 * @since 2024-05-11 13:42:52
 */
@Getter
@Setter
@TableName("mcp_job_plan")
@Schema(title = "JobPlanEntity对象", description = "定时任务计划表")
public class JobPlanEntity extends BaseNoLogicalDeleteEntity {


    @Serial
    private static final long serialVersionUID = 3822494629716848991L;

    @Schema(title = "任务标识")
    @TableField("job_handler")
    private String jobHandler;

    @Schema(title = "任务所需参数")
    @TableField("param")
    private String param;
    /**
     * 任务状态,[0:待执行,1:执行中,2:已完成,3:失败]
     *
     * @see JobPlanStatusEnum
     */
    @Schema(title = "任务状态,[0:待执行,1:执行中,2:已完成,3:失败]")
    @TableField("`status`")
    private Integer status;

    @Schema(title = "任务日志")
    @TableField("log_text")
    private String logText;

    @Schema(title = "任务开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("job_start_time")
    protected Date jobStartTime;

    @Schema(title = "任务结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("job_end_time")
    protected Date jobEndTime;

    @Schema(title = "执行耗时ms")
    @TableField("execute_time")
    private Long executeTime;
}
