package com.base.system.controller;

import com.base.common.result.Result;
import com.base.system.service.ImageWatermarkService;
import com.base.system.service.impl.ImageWatermarkServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 图片去水印控制器
 *
 * @author base
 * @since 2026-09-17
 */
@Api(tags = "图片去水印")
@Slf4j
@RestController
@RequestMapping("/system/image")
public class ImageWatermarkController {

    @Resource
    private ImageWatermarkService imageWatermarkService;

    /**
     * 去除图片水印
     */
    @ApiOperation("去除图片水印")
    @PostMapping("/remove-watermark")
    @PreAuthorize("hasAuthority('system:imageWatermark:use')")
    public Result<Map<String, Object>> removeWatermark(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "region", required = false) String region,
            @RequestParam(value = "backend", defaultValue = "cv2") String backend) {
        String errorMsg = validateImageFile(file);
        if (errorMsg != null) {
            return Result.error(errorMsg);
        }
        try {
            Map<String, Object> result = imageWatermarkService.removeWatermark(file, region, backend);
            return Result.success(result);
        } catch (Exception e) {
            log.error("图片去水印失败", e);
            return Result.error("去水印失败，请稍后重试");
        }
    }

    /**
     * 校验上传图片文件
     *
     * @param file 上传文件
     * @return 错误信息，null 表示校验通过
     */
    private String validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "文件不能为空";
        }
        if (!ImageWatermarkServiceImpl.isImageFile(file.getOriginalFilename())) {
            return "仅支持 JPG/PNG/BMP/WebP 图片文件";
        }
        if (file.getSize() > 50 * 1024 * 1024) {
            return "文件大小不能超过 50MB";
        }
        return null;
    }
}