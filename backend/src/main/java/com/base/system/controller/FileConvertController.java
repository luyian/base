package com.base.system.controller;

import com.base.common.result.Result;
import com.base.system.entity.SysFile;
import com.base.system.service.FileConvertService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
        if (file == null || file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase().endsWith(".pdf")) {
            return Result.error("仅支持 PDF 文件");
        }
        if (file.getSize() > 50 * 1024 * 1024) {
            return Result.error("文件大小不能超过 50MB");
        }
        try {
            Map<String, Object> result = fileConvertService.pdfToWord(file);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("转换失败: " + e.getMessage());
        }
    }
}
