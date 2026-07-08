package com.base.ai.dto.image;

import lombok.Data;

import java.io.Serializable;

/**
 * AI 作图单张图片响应。
 *
 * @author base
 */
@Data
public class DedicatedAiImageItemResponse implements Serializable {

    private static final long serialVersionUID = 1L;

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
