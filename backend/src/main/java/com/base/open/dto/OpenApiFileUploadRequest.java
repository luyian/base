package com.base.open.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 开放接口文件上传请求
 *
 * @author base
 */
@Data
public class OpenApiFileUploadRequest {

    /**
     * 上传的文件
     */
    private MultipartFile file;

    /**
     * 文件描述
     */
    private String fileDesc;
}
