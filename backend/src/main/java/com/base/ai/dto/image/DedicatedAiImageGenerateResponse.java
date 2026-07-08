package com.base.ai.dto.image;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * AI 作图专用生成响应。
 *
 * @author base
 */
@Data
public class DedicatedAiImageGenerateResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 原始提示词。
     */
    private String prompt;

    /**
     * 实际模型。
     */
    private String model;

    /**
     * 实际适配器。
     */
    private String adapter;

    /**
     * 图片尺寸。
     */
    private String size;

    /**
     * 请求生成数量。
     */
    private Integer count;

    /**
     * 图片列表。
     */
    private List<DedicatedAiImageItemResponse> images = new ArrayList<>();

    /**
     * 原始文本响应。
     */
    private String rawText;
}
