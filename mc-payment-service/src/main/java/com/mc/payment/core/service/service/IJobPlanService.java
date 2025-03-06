package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.core.service.entity.JobPlanEntity;
import com.mc.payment.core.service.model.enums.JobPlanHandlerEnum;
import com.mc.payment.core.service.model.enums.JobPlanStatusEnum;

import java.util.List;

/**
 * <p>
 * 定时任务计划表 服务类
 * </p>
 *
 * @author conor
 * @since 2024-05-11 13:42:52
 */
public interface IJobPlanService extends IService<JobPlanEntity> {
    /**
     * 添加任务计划
     *
     * @param jobPlanHandlerEnum
     * @param paramObject 任意对象,会转为json字符串保存
     * @return
     */
    boolean addJobPlan(JobPlanHandlerEnum jobPlanHandlerEnum, Object paramObject);

    List<JobPlanEntity> listByLimit(JobPlanHandlerEnum jobPlanHandlerEnum, int status, int limit);

    /**
     * 更新任务执行情况
     * 会根据任务状态记录任务开始时间和结束时间
     * 执行中:记录开始时间
     * 已完成或者失败:记录结束时间
     *
     * @param id
     * @param jobPlanStatusEnum
     * @param logText
     * @return
     */
    boolean update(String id, JobPlanStatusEnum jobPlanStatusEnum, String logText);
}
