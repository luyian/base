package com.base.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传响应
 *
 * @author base
 * @since 2026-01-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {

    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件路径（相对路径）
     */
    private String filePath;

    /**
     * 文件访问URL
     */
    private String fileUrl;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String contentType;
}
