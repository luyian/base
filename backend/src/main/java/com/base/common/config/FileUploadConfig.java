package com.base.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件上传配置
 *
 * @author base
 * @since 2026-01-13
 */
@Data
@Component
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig {

    /**
     * 文件上传路径
     */
    private String path = "D:/upload/";

    /**
     * 文件访问前缀
     */
    private String prefix = "/upload";

    /**
     * 允许上传的文件类型
     */
    private String[] allowedTypes = {
            "image/jpeg", "image/jpg", "image/png", "image/gif",
            "application/pdf", "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    };

    /**
     * 最大文件大小（字节）默认10MB
     */
    private Long maxSize = 10 * 1024 * 1024L;
}
