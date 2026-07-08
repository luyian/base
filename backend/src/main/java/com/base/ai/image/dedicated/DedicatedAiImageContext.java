package com.base.ai.image.dedicated;

import lombok.Data;

/**
 * AI 作图专用上下文。
 *
 * @author base
 */
@Data
public class DedicatedAiImageContext {

    /**
     * 原始提示词。
     */
    private String originalPrompt;

    /**
     * 实际发送给供应商的提示词。
     */
    private String prompt;

    /**
     * 负向提示词。
     */
    private String negativePrompt;

    /**
     * 风格要求。
     */
    private String style;

    /**
     * 图片尺寸。
     */
    private String size;

    /**
     * 图片数量。
     */
    private Integer count;

    /**
     * 图片模型。
     */
    private String model;

    /**
     * 图片适配器。
     */
    private String adapter;

    /**
     * API 基础地址。
     */
    private String baseUrl;

    /**
     * API Key。
     */
    private String apiKey;

    /**
     * 请求超时时间。
     */
    private Integer timeout;

    /**
     * 随机种子。
     */
    private Integer seed;
}
