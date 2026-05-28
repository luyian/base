package com.base.system.util;

import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import com.base.common.service.CosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 文件上传工具类
 */
@Slf4j
@Component
public class FileUploadUtil {

    private final CosService cosService;

    @Value("${file.upload.max-size}")
    private Long maxSize;

    @Value("${file.upload.allowed-types}")
    private List<String> allowedTypes;

    public FileUploadUtil(CosService cosService) {
        this.cosService = cosService;
    }

    /**
     * 上传头像文件
     *
     * @param file 文件
     * @return 文件访问URL
     */
    public String uploadAvatar(MultipartFile file) {
        // 验证文件
        validateFile(file);

        // 验证文件类型（头像只支持图片）
        List<String> avatarAllowedTypes = Arrays.asList("image/jpeg", "image/jpg", "image/png");
        String contentType = file.getContentType();
        if (!avatarAllowedTypes.contains(contentType)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "头像只支持jpg、jpeg、png格式");
        }

        // 验证文件大小（头像限制2MB）
        long avatarMaxSize = 2 * 1024 * 1024; // 2MB
        if (file.getSize() > avatarMaxSize) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "头像大小不能超过2MB");
        }

        // 保存文件到avatar子目录
        return saveFile(file, "avatar");
    }

    /**
     * 上传普通文件
     *
     * @param file 文件
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file) {
        // 验证文件
        validateFile(file);

        // 验证文件类型
        String contentType = file.getContentType();
        if (!allowedTypes.contains(contentType)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "不支持的文件类型");
        }

        // 验证文件大小
        if (file.getSize() > maxSize) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(),
                "文件大小不能超过" + (maxSize / 1024 / 1024) + "MB");
        }

        // 保存文件到files子目录
        return saveFile(file, "files");
    }

    /**
     * 验证文件
     *
     * @param file 文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "文件名不能为空");
        }
    }

    /**
     * 保存文件到 COS
     *
     * @param file   文件
     * @param subDir 子目录（作为 COS fileGroup）
     * @return 文件访问URL
     */
    private String saveFile(MultipartFile file, String subDir) {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileExt = getFileExt(originalFilename);
            String cosKey = cosService.uploadFile(file.getBytes(), subDir, fileExt);
            String fileUrl = cosService.getFileUrl(cosKey);

            log.info("文件上传成功，原始文件名: {}, COS Key: {}, 访问URL: {}",
                    originalFilename, cosKey, fileUrl);
            return fileUrl;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ResultCode.ERROR.getCode(), "文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL（COS 完整 URL 或 COS key）
     * @return 是否删除成功
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            return false;
        }
        try {
            // 如果是完整 URL，提取 key 部分
            String cosKey = fileUrl;
            if (fileUrl.startsWith("https://") || fileUrl.startsWith("http://")) {
                int idx = fileUrl.indexOf(".com/");
                if (idx > 0) {
                    cosKey = fileUrl.substring(idx + 5);
                }
            }
            cosService.deleteFile(cosKey);
            log.info("文件删除成功，COS Key: {}", cosKey);
            return true;
        } catch (Exception e) {
            log.error("文件删除失败，文件URL: {}", fileUrl, e);
            return false;
        }
    }

    private String getFileExt(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
}
