package com.base.system.util;

import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传工具类
 */
@Slf4j
@Component
public class FileUploadUtil {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.prefix}")
    private String uploadPrefix;

    @Value("${file.upload.max-size}")
    private Long maxSize;

    @Value("${file.upload.allowed-types}")
    private List<String> allowedTypes;

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
     * 保存文件
     *
     * @param file 文件
     * @param subDir 子目录
     * @return 文件访问URL
     */
    private String saveFile(MultipartFile file, String subDir) {
        try {
            // 获取原始文件名
            String originalFilename = file.getOriginalFilename();

            // 获取文件扩展名
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 生成唯一文件名：UUID + 扩展名
            String fileName = UUID.randomUUID().toString().replace("-", "") + extension;

            // 按日期创建子目录：yyyy/MM/dd
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

            // 完整的文件保存路径
            String fileDir = uploadPath + File.separator + subDir + File.separator + datePath;
            Path dirPath = Paths.get(fileDir);

            // 创建目录（如果不存在）
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // 保存文件
            String filePath = fileDir + File.separator + fileName;
            File dest = new File(filePath);
            file.transferTo(dest);

            // 返回文件访问URL：/upload/avatar/yyyy/MM/dd/文件名
            String fileUrl = uploadPrefix + "/" + subDir + "/" + datePath.replace("\\", "/") + "/" + fileName;

            log.info("文件上传成功，原始文件名: {}, 保存路径: {}, 访问URL: {}",
                originalFilename, filePath, fileUrl);

            return fileUrl;

        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ResultCode.ERROR.getCode(), "文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            return false;
        }

        try {
            // 将URL转换为文件路径
            String filePath = fileUrl.replace(uploadPrefix, uploadPath).replace("/", File.separator);
            File file = new File(filePath);

            if (file.exists() && file.isFile()) {
                boolean deleted = file.delete();
                if (deleted) {
                    log.info("文件删除成功，文件路径: {}", filePath);
                } else {
                    log.warn("文件删除失败，文件路径: {}", filePath);
                }
                return deleted;
            } else {
                log.warn("文件不存在，文件路径: {}", filePath);
                return false;
            }
        } catch (Exception e) {
            log.error("文件删除失败，文件URL: {}", fileUrl, e);
            return false;
        }
    }
}
