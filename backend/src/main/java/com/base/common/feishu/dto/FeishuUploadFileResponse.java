package com.base.common.feishu.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 飞书上传文件响应 DTO
 *
 * @author base
 * @since 2026-02-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FeishuUploadFileResponse extends FeishuBaseResponse {

    /**
     * 响应数据
     */
    private FileData data;

    /**
     * 文件数据
     */
    @Data
    public static class FileData {

        /**
         * 文件 Key
         */
        private String fileKey;
    }
}
