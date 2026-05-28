package com.base.open.controller;

import com.base.common.result.Result;
import com.base.common.service.CosService;
import com.base.open.context.OpenApiContext;
import com.base.open.dto.OpenApiFileUploadRequest;
import com.base.open.dto.OpenApiFileUploadResponse;
import com.base.system.entity.SysFile;
import com.base.system.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 开放接口文件管理控制器
 *
 * @author base
 */
@Slf4j
@Api(tags = "开放接口-文件管理")
@RestController
@RequestMapping("/open/file")
public class OpenApiFileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private CosService cosService;

    /**
     * 上传文件
     */
    @ApiOperation("上传文件")
    @PostMapping("/upload")
    public Result<OpenApiFileUploadResponse> uploadFile(OpenApiFileUploadRequest uploadRequest, HttpServletRequest request) {
        if (uploadRequest.getFile() == null || uploadRequest.getFile().isEmpty()) {
            return Result.error("文件不能为空");
        }

        try {
            // 构建上传者名称标识
            String uploaderName = "OPEN:" + OpenApiContext.getAppName();
            SysFile sysFile = fileService.uploadFile(
                    uploadRequest.getFile(), "open", uploadRequest.getFileDesc(), uploaderName, request
            );

            OpenApiFileUploadResponse response = OpenApiFileUploadResponse.builder()
                    .fileId(sysFile.getId())
                    .fileName(sysFile.getFileName())
                    .originalName(sysFile.getOriginalName())
                    .fileSize(sysFile.getFileSize())
                    .fileUrl(cosService.getFileUrl(sysFile.getFileUrl()))
                    .build();

            return Result.success(response);
        } catch (Exception e) {
            log.error("开放接口文件上传失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 下载文件
     */
    @ApiOperation("下载文件")
    @GetMapping("/download/{id}")
    public void downloadFile(@PathVariable Long id, HttpServletResponse response, HttpServletRequest request) {
        fileService.downloadFile(id, response, request);
    }
}
