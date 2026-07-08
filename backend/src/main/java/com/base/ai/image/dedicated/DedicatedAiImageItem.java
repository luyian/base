package com.base.ai.image.dedicated;

import lombok.Data;

/**
 * AI 作图专用单张图片结果。
 *
 * @author base
 */
@Data
public class DedicatedAiImageItem {

    /**
     * 图片序号。
     */
    private Integer index;

    /**
     * 图片 URL。
     */
    private String url;

    /**
     * 图片 base64 内容。
     */
    private String base64;

    /**
     * 图片 MIME 类型。
     */
    private String mimeType;

    /**
     * 供应商优化后的提示词。
     */
    private String revisedPrompt;
}
