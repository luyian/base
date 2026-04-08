package com.base.system.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传请求
 *
 * @author base
 */
@Data
public class FileUploadRequest {

    /**
     * 上传的文件
     */
    private MultipartFile file;

    /**
     * 文件分组
     */
    private String fileGroup = "default";

    /**
     * 文件描述
     */
    private String fileDesc;
}
