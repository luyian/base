package com.base.common.export.engine;

import com.base.common.export.constant.ExportStatusEnum;
import com.base.system.export.entity.ExportTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * 导出执行器
 * 负责异步执行导出任务
 *
 * @author base
 * @since 2026-02-04
 */
@Component
public class ExportExecutor {

    private static final Logger log = LoggerFactory.getLogger(ExportExecutor.class);

    @Autowired
    @Qualifier("exportTaskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private ExportEngine exportEngine;

    /**
     * 提交导出任务
     *
     * @param context 导出上下文
     * @param taskUpdater 任务更新回调函数
     */
    public void submit(ExportContext context, Consumer<ExportTask> taskUpdater) {
        taskExecutor.execute(() -> {
            try {
                log.info("开始执行导出任务: {}", context.getTask().getTaskNo());
                exportEngine.execute(context);
                log.info("导出任务执行完成: {}", context.getTask().getTaskNo());
            } catch (Exception e) {
                log.error("导出任务执行异常: {}", context.getTask().getTaskNo(), e);
                ExportTask task = context.getTask();
                task.setStatus(ExportStatusEnum.FAILED.getCode());
                task.setErrorMessage(e.getMessage());
            } finally {
                // 通过回调函数更新任务状态到数据库
                if (taskUpdater != null) {
                    taskUpdater.accept(context.getTask());
                }
            }
        });
    }

    /**
     * 获取线程池活跃线程数
     */
    public int getActiveCount() {
        return taskExecutor.getActiveCount();
    }

    /**
     * 获取线程池队列大小
     */
    public int getQueueSize() {
        return taskExecutor.getThreadPoolExecutor().getQueue().size();
    }
}
