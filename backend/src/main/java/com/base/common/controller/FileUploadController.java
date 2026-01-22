package com.base.common.controller;

import com.base.common.dto.FileUploadResponse;
import com.base.common.result.Result;
import com.base.common.service.FileUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 *
 * @author base
 * @since 2026-01-13
 */
@Api(tags = "文件上传")
@RestController
@RequestMapping("/common/file")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * 上传文件
     */
    @ApiOperation(value = "上传文件")
    @PostMapping("/upload")
    public Result<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        FileUploadResponse response = fileUploadService.uploadFile(file);
        return Result.success(response);
    }

    /**
     * 上传头像
     */
    @ApiOperation(value = "上传头像")
    @PostMapping("/upload/avatar")
    public Result<FileUploadResponse> uploadAvatar(@RequestParam("file") MultipartFile file) {
        FileUploadResponse response = fileUploadService.uploadAvatar(file);
        return Result.success(response);
    }
}
