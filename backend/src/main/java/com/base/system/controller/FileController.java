package com.base.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.result.Result;
import com.base.system.dto.FilePageRequest;
import com.base.system.dto.FileUploadRequest;
import com.base.system.entity.SysFile;
import com.base.system.entity.SysFileLog;
import com.base.system.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件管理控制器
 */
@Api(tags = "文件管理")
@RestController
@RequestMapping("/system/file")
public class FileController {

    @Resource
    private FileService fileService;

    /**
     * 上传文件
     */
    @ApiOperation("上传文件")
    @PostMapping("/upload")
    public Result<SysFile> uploadFile(FileUploadRequest uploadRequest, HttpServletRequest request) {
        if (uploadRequest.getFile() == null || uploadRequest.getFile().isEmpty()) {
            return Result.error("文件不能为空");
        }
        try {
            SysFile sysFile = fileService.uploadFile(
                    uploadRequest.getFile(),
                    uploadRequest.getFileGroup(),
                    uploadRequest.getFileDesc(),
                    request
            );
            return Result.success(sysFile);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据ID获取文件信息
     */
    @ApiOperation("根据ID获取文件信息")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('file:query')")
    public Result<SysFile> getFileById(@PathVariable Long id) {
        SysFile sysFile = fileService.getFileById(id);
        return Result.success(sysFile);
    }

    /**
     * 分页查询文件列表
     */
    @ApiOperation("分页查询文件列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('file:list')")
    public Result<Page<SysFile>> pageFiles(FilePageRequest pageRequest) {
        Page<SysFile> page = fileService.pageFiles(
                pageRequest.getPageNum(),
                pageRequest.getPageSize(),
                pageRequest.getFileName(),
                pageRequest.getFileGroup()
        );
        return Result.success(page);
    }

    /**
     * 删除文件
     */
    @ApiOperation("删除文件")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('file:delete')")
    public Result<Void> deleteFile(@PathVariable Long id) {
        fileService.deleteFile(id);
        return Result.success();
    }

    /**
     * 批量删除文件
     */
    @ApiOperation("批量删除文件")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('file:delete')")
    public Result<Void> batchDeleteFiles(@RequestBody List<Long> ids) {
        fileService.batchDeleteFiles(ids);
        return Result.success();
    }

    /**
     * 下载文件
     */
    @ApiOperation("下载文件")
    @GetMapping("/download/{id}")
    public void downloadFile(@PathVariable Long id, HttpServletResponse response, HttpServletRequest request) {
        fileService.downloadFile(id, response, request);
    }

    /**
     * 获取文件访问URL
     */
    @ApiOperation("获取文件访问URL")
    @GetMapping("/url/{id}")
    public Result<Map<String, String>> getFileUrl(@PathVariable Long id) {
        SysFile sysFile = fileService.getFileById(id);
        if (sysFile == null) {
            return Result.error("文件不存在");
        }
        Map<String, String> result = new HashMap<>();
        result.put("url", sysFile.getFileUrl());
        result.put("fileName", sysFile.getOriginalName());
        return Result.success(result);
    }

    /**
     * 获取文件分组列表
     */
    @ApiOperation("获取文件分组列表")
    @GetMapping("/groups")
    @PreAuthorize("hasAuthority('file:list')")
    public Result<List<String>> getFileGroups() {
        // 返回常用的文件分组
        List<String> groups = java.util.Arrays.asList("default", "images", "documents", "videos", "audio", "others", "open");
        return Result.success(groups);
    }
}