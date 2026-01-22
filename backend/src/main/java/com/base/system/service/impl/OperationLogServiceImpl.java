package com.base.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import com.base.system.dto.log.OperationLogQueryRequest;
import com.base.system.dto.log.OperationLogResponse;
import com.base.system.entity.OperationLog;
import com.base.system.mapper.OperationLogMapper;
import com.base.system.service.OperationLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 操作日志服务实现类
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Resource
    private OperationLogMapper operationLogMapper;

    /**
     * 操作类型映射
     */
    private static final Map<Integer, String> OPERATION_TYPE_MAP = new HashMap<>();

    static {
        OPERATION_TYPE_MAP.put(1, "新增");
        OPERATION_TYPE_MAP.put(2, "修改");
        OPERATION_TYPE_MAP.put(3, "删除");
        OPERATION_TYPE_MAP.put(4, "查询");
        OPERATION_TYPE_MAP.put(5, "导出");
        OPERATION_TYPE_MAP.put(6, "导入");
        OPERATION_TYPE_MAP.put(7, "其他");
    }

    @Override
    public Page<OperationLogResponse> pageOperationLogs(OperationLogQueryRequest request) {
        // 构建查询条件
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(request.getModule()), OperationLog::getModule, request.getModule())
                .eq(request.getOperationType() != null, OperationLog::getOperationType, request.getOperationType())
                .like(StringUtils.hasText(request.getOperatorName()), OperationLog::getOperatorName, request.getOperatorName())
                .eq(request.getStatus() != null, OperationLog::getStatus, request.getStatus());

        // 时间范围查询
        if (StringUtils.hasText(request.getStartTime())) {
            LocalDateTime startTime = LocalDateTime.parse(request.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            wrapper.ge(OperationLog::getCreateTime, startTime);
        }
        if (StringUtils.hasText(request.getEndTime())) {
            LocalDateTime endTime = LocalDateTime.parse(request.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            wrapper.le(OperationLog::getCreateTime, endTime);
        }

        // 按创建时间降序排序
        wrapper.orderByDesc(OperationLog::getCreateTime);

        // 分页查询
        Page<OperationLog> page = new Page<>(request.getCurrent(), request.getSize());
        Page<OperationLog> operationLogPage = operationLogMapper.selectPage(page, wrapper);

        // 转换为响应对象
        Page<OperationLogResponse> responsePage = new Page<>(operationLogPage.getCurrent(), operationLogPage.getSize(), operationLogPage.getTotal());
        List<OperationLogResponse> responseList = operationLogPage.getRecords().stream().map(operationLog -> {
            OperationLogResponse response = new OperationLogResponse();
            BeanUtils.copyProperties(operationLog, response);
            response.setOperationTypeName(OPERATION_TYPE_MAP.get(operationLog.getOperationType()));
            return response;
        }).collect(Collectors.toList());
        responsePage.setRecords(responseList);

        return responsePage;
    }

    @Override
    public OperationLogResponse getOperationLogById(Long id) {
        OperationLog operationLog = operationLogMapper.selectById(id);
        if (operationLog == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        OperationLogResponse response = new OperationLogResponse();
        BeanUtils.copyProperties(operationLog, response);
        response.setOperationTypeName(OPERATION_TYPE_MAP.get(operationLog.getOperationType()));
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOperationLog(Long id) {
        OperationLog operationLog = operationLogMapper.selectById(id);
        if (operationLog == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        operationLogMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteOperationLogs(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        operationLogMapper.deleteBatchIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearOperationLogs() {
        operationLogMapper.delete(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOperationLog(OperationLog operationLog) {
        operationLogMapper.insert(operationLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(OperationLog operationLog) {
        operationLogMapper.insert(operationLog);
    }
}
