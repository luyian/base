package com.base.common.service;

import com.base.common.config.FileUploadConfig;
import com.base.common.dto.FileUploadResponse;
import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import com.base.common.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

/**
 * 文件上传服务
 *
 * @author base
 * @since 2026-01-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final FileUploadConfig fileUploadConfig;
    private final CosService cosService;

    /**
     * 上传文件
     */
    public FileUploadResponse uploadFile(MultipartFile file) {
        // 验证文件是否为空
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "文件不能为空");
        }

        // 验证文件大小
        if (file.getSize() > fileUploadConfig.getMaxSize()) {
            throw new BusinessException(ResultCode.PARAM_ERROR,
                    "文件大小超过限制，最大允许" + FileUtils.formatFileSize(fileUploadConfig.getMaxSize()));
        }

        // 验证文件类型（按扩展名校验）
        String contentType = file.getContentType();
        String fileExt = getFileExt(file.getOriginalFilename());
        if (!isAllowedExt(fileExt, fileUploadConfig.getAllowedTypes())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "不支持的文件类型：" + fileExt);
        }

        try {
            // 上传到 COS
            String cosKey = cosService.uploadFile(file.getBytes(), "common", fileExt);
            String fileUrl = cosService.getFileUrl(cosKey);

            // 返回响应
            FileUploadResponse response = new FileUploadResponse();
            response.setFilename(file.getOriginalFilename());
            response.setFilePath(cosKey);
            response.setFileUrl(fileUrl);
            response.setFileSize(file.getSize());
            response.setContentType(contentType);

            log.info("文件上传成功：{}", fileUrl);
            return response;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 上传图片（头像）
     */
    public FileUploadResponse uploadAvatar(MultipartFile file) {
        // 验证文件是否为空
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "图片不能为空");
        }

        // 验证文件类型（只允许图片）
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "只能上传图片文件");
        }

        // 验证文件大小（头像限制2MB）
        long maxAvatarSize = 2 * 1024 * 1024L;
        if (file.getSize() > maxAvatarSize) {
            throw new BusinessException(ResultCode.PARAM_ERROR,
                    "图片大小超过限制，最大允许" + FileUtils.formatFileSize(maxAvatarSize));
        }

        return uploadFile(file);
    }

    /**
     * 删除文件
     */
    public boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        try {
            cosService.deleteFile(filePath);
            log.info("文件删除成功：{}", filePath);
            return true;
        } catch (Exception e) {
            log.warn("文件删除失败：{}", filePath, e);
            return false;
        }
    }

    private String getFileExt(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isAllowedExt(String ext, String[] allowedTypes) {
        if (ext == null || ext.isEmpty() || allowedTypes == null) {
            return false;
        }
        for (String allowed : allowedTypes) {
            if (ext.equalsIgnoreCase(allowed.trim())) {
                return true;
            }
        }
        return false;
    }
}
