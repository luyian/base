package com.base.system.export.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.export.constant.ExportStatusEnum;
import com.base.common.export.engine.ExportContext;
import com.base.common.export.engine.ExportEngine;
import com.base.common.export.engine.ExportExecutor;
import com.base.system.export.dto.task.ExportTaskCreateRequest;
import com.base.system.export.dto.task.ExportTaskQueryRequest;
import com.base.system.export.dto.task.ExportTaskResponse;
import com.base.system.export.entity.ExportConfig;
import com.base.system.export.entity.ExportField;
import com.base.system.export.entity.ExportTask;
import com.base.system.mapper.ExportFieldMapper;
import com.base.system.mapper.ExportTaskMapper;
import com.base.system.export.service.ExportConfigService;
import com.base.system.export.service.ExportTaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 导出任务服务实现
 *
 * @author base
 * @since 2026-02-04
 */
@Service
public class ExportTaskServiceImpl implements ExportTaskService {

    @Autowired
    private ExportTaskMapper exportTaskMapper;

    @Autowired
    private ExportFieldMapper exportFieldMapper;

    @Autowired
    private ExportConfigService exportConfigService;

    @Autowired
    private ExportExecutor exportExecutor;

    @Autowired
    private ExportEngine exportEngine;

    @Override
    public Page<ExportTaskResponse> pageTasks(ExportTaskQueryRequest request) {
        Page<ExportTask> page = new Page<>(request.getPageNum(), request.getPageSize());

        LambdaQueryWrapper<ExportTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(request.getTaskNo()), ExportTask::getTaskNo, request.getTaskNo())
                .like(StringUtils.hasText(request.getConfigCode()), ExportTask::getConfigCode, request.getConfigCode())
                .like(StringUtils.hasText(request.getConfigName()), ExportTask::getConfigName, request.getConfigName())
                .eq(request.getStatus() != null, ExportTask::getStatus, request.getStatus())
                .eq(StringUtils.hasText(request.getCreateBy()), ExportTask::getCreateBy, request.getCreateBy())
                .ge(StringUtils.hasText(request.getStartTimeBegin()), ExportTask::getStartTime, request.getStartTimeBegin())
                .le(StringUtils.hasText(request.getStartTimeEnd()), ExportTask::getStartTime, request.getStartTimeEnd())
                .orderByDesc(ExportTask::getCreateTime);

        Page<ExportTask> resultPage = exportTaskMapper.selectPage(page, wrapper);

        Page<ExportTaskResponse> responsePage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        responsePage.setRecords(resultPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));

        return responsePage;
    }

    @Override
    public ExportTaskResponse getTaskById(Long id) {
        ExportTask task = exportTaskMapper.selectById(id);
        if (task == null) {
            return null;
        }
        return convertToResponse(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createTask(ExportTaskCreateRequest request) {
        // 获取配置
        ExportConfig config = exportConfigService.getConfigByCode(request.getConfigCode());
        if (config == null) {
            throw new RuntimeException("导出配置不存在或已禁用");
        }

        // 生成任务编号
        String taskNo = generateTaskNo();

        // 创建任务
        ExportTask task = new ExportTask();
        task.setTaskNo(taskNo);
        task.setConfigId(config.getId());
        task.setConfigCode(config.getConfigCode());
        task.setConfigName(config.getConfigName());
        task.setQueryParams(request.getQueryParams());
        task.setStatus(ExportStatusEnum.PENDING.getCode());
        task.setTotalCount(0);
        task.setProcessedCount(0);
        task.setProgress(0);
        task.setDownloadCount(0);

        exportTaskMapper.insert(task);

        // 获取字段配置
        LambdaQueryWrapper<ExportField> fieldWrapper = new LambdaQueryWrapper<>();
        fieldWrapper.eq(ExportField::getConfigId, config.getId())
                .eq(ExportField::getStatus, 1)
                .orderByAsc(ExportField::getSort);
        List<ExportField> fields = exportFieldMapper.selectList(fieldWrapper);

        if (fields.isEmpty()) {
            throw new RuntimeException("导出配置没有可用的字段");
        }

        // 构建导出上下文
        ExportContext context = new ExportContext();
        context.setConfig(config);
        context.setFields(fields);
        context.setTask(task);

        // 解析查询参数
        if (StringUtils.hasText(request.getQueryParams())) {
          try {
                Map<String, Object> queryParamMap = JSON.parseObject(request.getQueryParams(), Map.class);
                context.setQueryParamMap(queryParamMap);

                // 如果配置了查询参数类，尝试转换
                if (StringUtils.hasText(config.getQueryParamClass())) {
                    try {
                        Class<?> paramClass = Class.forName(config.getQueryParamClass());
                        Object queryParam = JSON.parseObject(request.getQueryParams(), paramClass);
                        context.setQueryParam(queryParam);
                    } catch (ClassNotFoundException e) {
                        // 忽略，使用 Map
                    }
                }
            } catch (Exception e) {
                // 忽略解析错误
            }
        }

        // 提交异步执行，传入任务更新回调函数
        exportExecutor.submit(context, updatedTask -> {
            exportTaskMapper.updateById(updatedTask);
        });

        return taskNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelTask(Long id) {
        ExportTask task = exportTaskMapper.selectById(id);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }

        // 只能取消待处理或处理中的任务
        if (task.getStatus() != ExportStatusEnum.PENDING.getCode()
                && task.getStatus() != ExportStatusEnum.PROCESSING.getCode()) {
            throw new RuntimeException("只能取消待处理或处理中的任务");
        }

        LambdaUpdateWrapper<ExportTask> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ExportTask::getId, id)
                .set(ExportTask::getStatus, ExportStatusEnum.CANCELLED.getCode())
                .set(ExportTask::getEndTime, LocalDateTime.now());

        exportTaskMapper.update(null, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTask(Long id) {
        ExportTask task = exportTaskMapper.selectById(id);
        if (task == null) {
            return;
        }

        // 不能删除处理中的任务
        if (task.getStatus() == ExportStatusEnum.PROCESSING.getCode()) {
            throw new RuntimeException("不能删除处理中的任务");
        }

        exportTaskMapper.deleteById(id);
    }

    @Override
    public Integer getTaskProgress(Long id) {
        ExportTask task = exportTaskMapper.selectById(id);
        if (task == null) {
            return null;
        }

        // 优先从 Redis 获取实时进度
        Integer redisProgress = exportEngine.getProgress(task.getTaskNo());
        if (redisProgress != null) {
            return redisProgress;
        }

        return task.getProgress();
    }

    @Override
    public String getFilePath(String taskNo) {
        LambdaQueryWrapper<ExportTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExportTask::getTaskNo, taskNo);
        ExportTask task = exportTaskMapper.selectOne(wrapper);

        if (task == null) {
            throw new RuntimeException("任务不存在");
        }

        if (task.getStatus() != ExportStatusEnum.COMPLETED.getCode()) {
            throw new RuntimeException("任务未完成");
        }

        // 检查文件是否过期
        if (task.getExpireTime() != null && task.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("文件已过期");
        }

        return task.getFilePath();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementDownloadCount(String taskNo) {
        LambdaUpdateWrapper<ExportTask> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ExportTask::getTaskNo, taskNo)
                .setSql("download_count = download_count + 1");
        exportTaskMapper.update(null, wrapper);
    }

    /**
     * 生成任务编号
     */
    private String generateTaskNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "EXP" + timestamp + uuid;
    }

    /**
     * 转换为响应对象
     */
    private ExportTaskResponse convertToResponse(ExportTask task) {
        ExportTaskResponse response = new ExportTaskResponse();
        BeanUtils.copyProperties(task, response);

        // 设置状态描述
        ExportStatusEnum status = ExportStatusEnum.getByCode(task.getStatus());
        if (status != null) {
            response.setStatusDesc(status.getDesc());
        }

        // 设置文件大小描述
        if (task.getFileSize() != null && task.getFileSize() > 0) {
            response.setFileSizeDesc(formatFileSize(task.getFileSize()));
        }

        return response;
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }
}
