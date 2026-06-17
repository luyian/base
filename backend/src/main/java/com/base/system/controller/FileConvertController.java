package com.base.system.controller;

import com.base.common.result.Result;
import com.base.system.entity.SysFile;
import com.base.system.service.FileConvertService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 文件转换控制器
 *
 * @author base
 * @since 2026-06-10
 */
@Api(tags = "文件转换")
@Slf4j
@RestController
@RequestMapping("/system/file-convert")
public class FileConvertController {

    @Resource
    private FileConvertService fileConvertService;

    /**
     * PDF 转 Word
     */
    @ApiOperation("PDF转Word")
    @PostMapping("/pdf-to-word")
    @PreAuthorize("hasAuthority('system:fileConvert:use')")
    public Result<Map<String, Object>> pdfToWord(@RequestParam("file") MultipartFile file) {
        String errorMsg = validatePdfFile(file);
        if (errorMsg != null) {
            return Result.error(errorMsg);
        }
        try {
            Map<String, Object> result = fileConvertService.pdfToWord(file);
            return Result.success(result);
        } catch (Exception e) {
            log.error("PDF转Word失败", e);
            return Result.error("文件转换失败，请稍后重试");
        }
    }

    /**
     * PDF 转 Markdown
     */
    @ApiOperation("PDF转Markdown")
    @PostMapping("/pdf-to-markdown")
    @PreAuthorize("hasAuthority('system:fileConvert:use')")
    public Result<Map<String, Object>> pdfToMarkdown(@RequestParam("file") MultipartFile file) {
        String errorMsg = validatePdfFile(file);
        if (errorMsg != null) {
            return Result.error(errorMsg);
        }
        try {
            Map<String, Object> result = fileConvertService.pdfToMarkdown(file);
            return Result.success(result);
        } catch (Exception e) {
            log.error("PDF转Markdown失败", e);
            return Result.error("文件转换失败，请稍后重试");
        }
    }

    /**
     * 校验 PDF 上传文件
     *
     * @param file 上传文件
     * @return 错误信息，null 表示校验通过
     */
    private String validatePdfFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "文件不能为空";
        }
        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase().endsWith(".pdf")) {
            return "仅支持 PDF 文件";
        }
        if (file.getSize() > 50 * 1024 * 1024) {
            return "文件大小不能超过 50MB";
        }
        return null;
    }
}
