package com.base.open.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 开放接口文件上传响应
 *
 * @author base
 */
@Data
@Builder
public class OpenApiFileUploadResponse {

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 存储文件名
     */
    private String fileName;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件访问URL
     */
    private String fileUrl;
}
