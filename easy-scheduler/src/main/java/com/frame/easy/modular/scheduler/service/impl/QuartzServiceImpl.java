package com.frame.easy.modular.scheduler.service.impl;

import cn.hutool.core.util.StrUtil;
import com.frame.easy.modular.scheduler.common.constant.SchedulerConst;
import com.frame.easy.modular.scheduler.common.status.SchedulerStatus;
import com.frame.easy.modular.scheduler.model.SchedulerJob;
import com.frame.easy.modular.scheduler.quartz.QuartzFactory;
import com.frame.easy.modular.scheduler.service.QuartzService;
import com.frame.easy.modular.scheduler.service.SchedulerJobService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 定时任务处理
 *
 * @author tengchong
 * @date 2019-05-11
 */
@Service
public class QuartzServiceImpl implements QuartzService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private SchedulerJobService schedulerJobService;

    @Override
    public void timingTask() {
        List<SchedulerJob> schedulerJobs = schedulerJobService.selectAll();
        if (schedulerJobs != null) {
            schedulerJobs.forEach(this::addJob);
        }
    }

    @Override
    public void addJob(SchedulerJob schedulerJob) {
        if (checkJob(schedulerJob)) {
            // 创建触发器
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(schedulerJob.getCode())
                    .withSchedule(CronScheduleBuilder.cronSchedule(schedulerJob.getCron()))
                    .startNow()
                    .build();
            // 创建任务
            JobDetail jobDetail = JobBuilder.newJob(QuartzFactory.class)
                    .withIdentity(schedulerJob.getCode())
                    .build();
            // 传入调度任务对象
            jobDetail.getJobDataMap().put(SchedulerConst.SCHEDULER_JOB_KEY, schedulerJob);
            try {
                scheduler.scheduleJob(jobDetail, trigger);
            } catch (SchedulerException e) {
                logger.warn("创建定时任务失败", e);
            }
        }
    }

    /**
     * 检查任务是否可以添加到定时任务
     *
     * @param schedulerJob 任务
     * @return true/false
     */
    private boolean checkJob(SchedulerJob schedulerJob) {
        return schedulerJob != null &&
                StrUtil.isNotBlank(schedulerJob.getCode()) &&
                StrUtil.isNotBlank(schedulerJob.getCron()) &&
                StrUtil.isNotBlank(schedulerJob.getBean()) &&
                StrUtil.isNotBlank(schedulerJob.getMethod());
    }

    @Override
    public void operateJob(SchedulerJob schedulerJob, SchedulerStatus schedulerStatus) {
        JobKey jobKey = new JobKey(schedulerJob.getCode());
        try {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            if (jobDetail == null) {
                // 如果任务里没有则添加任务
                addJob(schedulerJob);
            }
            switch (schedulerStatus) {
                case ENABLE:
                    scheduler.resumeJob(jobKey);
                    break;
                case DISABLE:
                    scheduler.pauseJob(jobKey);
                    break;
                case DELETE:
                    scheduler.deleteJob(jobKey);
                    break;
                default:
            }
        } catch (SchedulerException e) {
            logger.warn("获取定时任务[" + schedulerJob.getCode() + "]失败", e);
//            throw new EasyException("获取定时任务[" + schedulerJob.getCode() + "]失败");
        }
    }

    @Override
    public void startAll() throws SchedulerException {
        // 全部开始之前要将所有已开启任务添加到任务里
        timingTask();
        scheduler.start();
    }

    @Override
    public void pauseAll() throws SchedulerException {
        scheduler.standby();
    }
}
