package com.base.system.export.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.result.Result;
import com.base.system.export.dto.task.ExportTaskCreateRequest;
import com.base.system.export.dto.task.ExportTaskQueryRequest;
import com.base.system.export.dto.task.ExportTaskResponse;
import com.base.system.export.service.ExportTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 导出任务控制器
 *
 * @author base
 * @since 2026-02-04
 */
@Api(tags = "导出任务管理")
@RestController
@RequestMapping("/system/export/task")
public class ExportTaskController {

    @Autowired
    private ExportTaskService exportTaskService;

    @ApiOperation("分页查询导出任务")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:export:task:list')")
    public Result<Page<ExportTaskResponse>> page(ExportTaskQueryRequest request) {
        return Result.success(exportTaskService.pageTasks(request));
    }

    @ApiOperation("获取任务详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:export:task:query')")
    public Result<ExportTaskResponse> getById(@PathVariable Long id) {
        return Result.success(exportTaskService.getTaskById(id));
    }

    @ApiOperation("创建导出任务")
    @PostMapping
    @PreAuthorize("hasAuthority('system:export:task:add')")
    public Result<String> create(@Validated @RequestBody ExportTaskCreateRequest request) {
        return Result.success(exportTaskService.createTask(request));
    }

    @ApiOperation("取消任务")
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('system:export:task:edit')")
    public Result<Void> cancel(@PathVariable Long id) {
        exportTaskService.cancelTask(id);
        return Result.success();
    }

    @ApiOperation("删除任务")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:export:task:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        exportTaskService.deleteTask(id);
        return Result.success();
    }

    @ApiOperation("获取任务进度")
    @GetMapping("/{id}/progress")
    public Result<Integer> getProgress(@PathVariable Long id) {
        return Result.success(exportTaskService.getTaskProgress(id));
    }

    @ApiOperation("下载文件")
    @GetMapping("/{taskNo}/download")
    public ResponseEntity<Resource> download(@PathVariable String taskNo) {
        String filePath = exportTaskService.getFilePath(taskNo);
        File file = new File(filePath);

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 增加下载次数
        exportTaskService.incrementDownloadCount(taskNo);

        Resource resource = new FileSystemResource(file);
        String fileName = file.getName();

        // URL 编码文件名
        String encodedFileName;
        try {
            encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            encodedFileName = fileName;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName)
                .body(resource);
    }
}
