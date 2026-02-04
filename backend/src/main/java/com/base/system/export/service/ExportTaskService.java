package com.base.system.export.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.system.export.dto.task.ExportTaskCreateRequest;
import com.base.system.export.dto.task.ExportTaskQueryRequest;
import com.base.system.export.dto.task.ExportTaskResponse;

/**
 * 导出任务服务接口
 *
 * @author base
 * @since 2026-02-04
 */
public interface ExportTaskService {

    /**
     * 分页查询导出任务
     *
     * @param request 查询请求
     * @return 分页结果
     */
    Page<ExportTaskResponse> pageTasks(ExportTaskQueryRequest request);

    /**
     * 根据ID获取任务详情
     *
     * @param id 任务ID
     * @return 任务详情
     */
    ExportTaskResponse getTaskById(Long id);

    /**
     * 创建导出任务
     *
     * @param request 创建请求
     * @return 任务编号
     */
    String createTask(ExportTaskCreateRequest request);

    /**
     * 取消任务
     *
     * @param id 任务ID
     */
    void cancelTask(Long id);

    /**
     * 删除任务
     *
     * @param id 任务ID
     */
    void deleteTask(Long id);

    /**
     * 获取任务进度
     *
     * @param id 任务ID
     * @return 进度百分比
     */
    Integer getTaskProgress(Long id);

    /**
     * 获取文件路径
     *
     * @param taskNo 任务编号
     * @return 文件路径
     */
    String getFilePath(String taskNo);

    /**
     * 增加下载次数
     *
     * @param taskNo 任务编号
     */
    void incrementDownloadCount(String taskNo);
}
