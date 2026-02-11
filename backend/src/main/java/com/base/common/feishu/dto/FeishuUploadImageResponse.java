package com.base.common.feishu.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 飞书上传图片响应 DTO
 *
 * @author base
 * @since 2026-02-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FeishuUploadImageResponse extends FeishuBaseResponse {

    /**
     * 响应数据
     */
    private ImageData data;

    /**
     * 图片数据
     */
    @Data
    public static class ImageData {

        /**
         * 图片 Key
         */
        private String imageKey;
    }
}
