package com.base.ai.dto.image;

import lombok.Data;

import java.io.Serializable;

/**
 * AI 作图专用提示词润色响应。
 *
 * @author base
 */
@Data
public class DedicatedAiImagePolishResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 原始图片描述。
     */
    private String prompt;

    /**
     * 润色后的图片提示词。
     */
    private String polishedPrompt;
}
