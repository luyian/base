package com.base.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.result.Result;
import com.base.system.dto.FileLogPageRequest;
import com.base.system.entity.SysFileLog;
import com.base.system.mapper.SysFileLogMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.Arrays;
import java.util.List;

/**
 * 文件操作日志控制器
 */
@Api(tags = "文件操作日志管理")
@RestController
@RequestMapping("/system/file/log")
public class FileLogController {

    @Resource
    private SysFileLogMapper sysFileLogMapper;

    /**
     * 分页查询文件操作日志列表
     */
    @ApiOperation("分页查询文件操作日志列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('file:log:list')")
    public Result<Page<SysFileLog>> pageFileLogs(FileLogPageRequest pageRequest) {
        Page<SysFileLog> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        LambdaQueryWrapper<SysFileLog> wrapper = new LambdaQueryWrapper<>();

        if (pageRequest.getFileName() != null && !pageRequest.getFileName().isEmpty()) {
            wrapper.like(SysFileLog::getFileName, pageRequest.getFileName());
        }
        if (pageRequest.getOperationType() != null) {
            wrapper.eq(SysFileLog::getOperationType, pageRequest.getOperationType());
        }
        if (pageRequest.getOperatorName() != null && !pageRequest.getOperatorName().isEmpty()) {
            wrapper.like(SysFileLog::getOperatorName, pageRequest.getOperatorName());
        }
        if (pageRequest.getStatus() != null) {
            wrapper.eq(SysFileLog::getStatus, pageRequest.getStatus());
        }
        
        wrapper.orderByDesc(SysFileLog::getCreateTime);
        Page<SysFileLog> result = sysFileLogMapper.selectPage(page, wrapper);
        
        // 转换操作类型名称
        result.getRecords().forEach(log -> {
            log.setOperationTypeName(getOperationTypeName(log.getOperationType()));
        });
        
        return Result.success(result);
    }

    /**
     * 根据ID获取文件操作日志详情
     */
    @ApiOperation("根据ID获取文件操作日志详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('file:log:query')")
    public Result<SysFileLog> getFileLogById(@PathVariable Long id) {
        SysFileLog log = sysFileLogMapper.selectById(id);
        if (log != null) {
            log.setOperationTypeName(getOperationTypeName(log.getOperationType()));
        }
        return Result.success(log);
    }

    /**
     * 删除文件操作日志
     */
    @ApiOperation("删除文件操作日志")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('file:log:delete')")
    public Result<Void> deleteFileLog(@PathVariable Long id) {
        sysFileLogMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除文件操作日志
     */
    @ApiOperation("批量删除文件操作日志")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('file:log:delete')")
    public Result<Void> batchDeleteFileLogs(@RequestBody List<Long> ids) {
        sysFileLogMapper.deleteBatchIds(ids);
        return Result.success();
    }

    /**
     * 清空文件操作日志
     */
    @ApiOperation("清空文件操作日志")
    @DeleteMapping("/clear")
    @PreAuthorize("hasAuthority('file:log:delete')")
    public Result<Void> clearFileLogs() {
        sysFileLogMapper.delete(null);
        return Result.success();
    }

    /**
     * 获取操作类型列表
     */
    @ApiOperation("获取操作类型列表")
    @GetMapping("/operationTypes")
    public Result<List<DictItem>> getOperationTypes() {
        List<DictItem> items = Arrays.asList(
            new DictItem(1, "上传"),
            new DictItem(2, "下载"),
            new DictItem(3, "删除"),
            new DictItem(4, "预览")
        );
        return Result.success(items);
    }

    /**
     * 获取操作类型名称
     */
    private String getOperationTypeName(Integer operationType) {
        if (operationType == null) return "";
        switch (operationType) {
            case 1: return "上传";
            case 2: return "下载";
            case 3: return "删除";
            case 4: return "预览";
            default: return "未知";
        }
    }

    /**
     * 字典项
     */
    static class DictItem {
        private Integer value;
        private String label;

        public DictItem(Integer value, String label) {
            this.value = value;
            this.label = label;
        }

        public Integer getValue() { return value; }
        public String getLabel() { return label; }
    }
}