package com.base.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.result.Result;
import com.base.system.dto.log.OperationLogQueryRequest;
import com.base.system.dto.log.OperationLogResponse;
import com.base.system.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 操作日志控制器
 */
@Api(tags = "操作日志管理")
@RestController
@RequestMapping("/system/log/operation")
public class OperationLogController {

    @Resource
    private OperationLogService operationLogService;

    /**
     * 分页查询操作日志列表
     */
    @ApiOperation("分页查询操作日志列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:log:operation:list')")
    public Result<Page<OperationLogResponse>> pageOperationLogs(OperationLogQueryRequest request) {
        Page<OperationLogResponse> page = operationLogService.pageOperationLogs(request);
        return Result.success(page);
    }

    /**
     * 根据ID获取操作日志详情
     */
    @ApiOperation("根据ID获取操作日志详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:log:operation:query')")
    public Result<OperationLogResponse> getOperationLogById(@PathVariable Long id) {
        OperationLogResponse response = operationLogService.getOperationLogById(id);
        return Result.success(response);
    }

    /**
     * 删除操作日志
     */
    @ApiOperation("删除操作日志")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:log:operation:delete')")
    public Result<Void> deleteOperationLog(@PathVariable Long id) {
        operationLogService.deleteOperationLog(id);
        return Result.success();
    }

    /**
     * 批量删除操作日志
     */
    @ApiOperation("批量删除操作日志")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('system:log:operation:delete')")
    public Result<Void> batchDeleteOperationLogs(@RequestBody List<Long> ids) {
        operationLogService.batchDeleteOperationLogs(ids);
        return Result.success();
    }

    /**
     * 清空操作日志
     */
    @ApiOperation("清空操作日志")
    @DeleteMapping("/clear")
    @PreAuthorize("hasAuthority('system:log:operation:delete')")
    public Result<Void> clearOperationLogs() {
        operationLogService.clearOperationLogs();
        return Result.success();
    }
}
