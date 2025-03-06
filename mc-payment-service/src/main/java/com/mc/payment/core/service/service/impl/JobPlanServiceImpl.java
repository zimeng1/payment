package com.mc.payment.core.service.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.core.service.entity.JobPlanEntity;
import com.mc.payment.core.service.mapper.JobPlanMapper;
import com.mc.payment.core.service.model.enums.JobPlanHandlerEnum;
import com.mc.payment.core.service.model.enums.JobPlanStatusEnum;
import com.mc.payment.core.service.service.IJobPlanService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 定时任务计划表 服务实现类
 * </p>
 *
 * @author conor
 * @since 2024-05-11 13:42:52
 */
@Service
public class JobPlanServiceImpl extends ServiceImpl<JobPlanMapper, JobPlanEntity> implements IJobPlanService {
    /**
     * 添加任务计划
     *
     * @param jobPlanHandlerEnum
     * @param paramObject
     * @return
     */
    @Override
    public boolean addJobPlan(JobPlanHandlerEnum jobPlanHandlerEnum, Object paramObject) {
        JobPlanEntity jobPlanEntity = new JobPlanEntity();
        jobPlanEntity.setJobHandler(jobPlanHandlerEnum.getJobHandler());
        if (paramObject != null) {
            jobPlanEntity.setParam(JSONUtil.toJsonStr(paramObject));
        } else {
            jobPlanEntity.setParam("{}");
        }
        jobPlanEntity.setStatus(JobPlanStatusEnum.AWAIT.getCode());
        return this.save(jobPlanEntity);
    }

    @Override
    public List<JobPlanEntity> listByLimit(JobPlanHandlerEnum jobPlanHandlerEnum, int status, int limit) {
        return this.list(Wrappers.lambdaQuery(JobPlanEntity.class)
                .eq(JobPlanEntity::getJobHandler, jobPlanHandlerEnum.getJobHandler())
                .eq(JobPlanEntity::getStatus, status)
                .orderByAsc(JobPlanEntity::getCreateTime)
                .last("limit " + limit));
    }

    /**
     * 更新任务执行情况
     * 会根据任务状态记录任务开始时间和结束时间
     * 执行中且开始时间为null:记录开始时间
     * 已完成或者失败:记录结束时间
     *
     * @param id
     * @param jobPlanStatusEnum
     * @param logText           追加日志
     * @return
     */
    @Override
    public boolean update(String id, JobPlanStatusEnum jobPlanStatusEnum, String logText) {
        JobPlanEntity jobPlanEntity = this.getById(id);
        jobPlanEntity.setStatus(jobPlanStatusEnum.getCode());
        if (jobPlanStatusEnum.equals(JobPlanStatusEnum.ING) && jobPlanEntity.getJobStartTime() == null) {
            jobPlanEntity.setJobStartTime(new Date());
        } else if (jobPlanStatusEnum.equals(JobPlanStatusEnum.FINISH) || jobPlanStatusEnum.equals(JobPlanStatusEnum.FAIL)) {
            jobPlanEntity.setJobEndTime(new Date());
            jobPlanEntity.setExecuteTime(jobPlanEntity.getJobEndTime().getTime() - jobPlanEntity.getJobStartTime().getTime());
        }
        if (jobPlanEntity.getLogText() == null) {
            jobPlanEntity.setLogText(logText);
        } else {
            jobPlanEntity.setLogText(jobPlanEntity.getLogText() + logText);
        }
        return this.updateById(jobPlanEntity);
    }
}
