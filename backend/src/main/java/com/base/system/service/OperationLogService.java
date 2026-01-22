package com.base.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.system.dto.log.OperationLogQueryRequest;
import com.base.system.dto.log.OperationLogResponse;
import com.base.system.entity.OperationLog;

import java.util.List;

/**
 * 操作日志服务接口
 */
public interface OperationLogService {

    /**
     * 分页查询操作日志列表
     */
    Page<OperationLogResponse> pageOperationLogs(OperationLogQueryRequest request);

    /**
     * 根据ID获取操作日志详情
     */
    OperationLogResponse getOperationLogById(Long id);

    /**
     * 删除操作日志
     */
    void deleteOperationLog(Long id);

    /**
     * 批量删除操作日志
     */
    void batchDeleteOperationLogs(List<Long> ids);

    /**
     * 清空操作日志
     */
    void clearOperationLogs();

    /**
     * 保存操作日志
     */
    void saveOperationLog(OperationLog operationLog);

    /**
     * 保存操作日志（简化方法）
     */
    void save(OperationLog operationLog);
}
