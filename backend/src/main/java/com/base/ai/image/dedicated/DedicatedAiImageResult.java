package com.base.ai.image.dedicated;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * AI 作图专用多图结果。
 *
 * @author base
 */
@Data
public class DedicatedAiImageResult {

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
    private String adapterName;

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
    private List<DedicatedAiImageItem> images = new ArrayList<>();

    /**
     * 原始文本响应。
     */
    private String rawText;
}
