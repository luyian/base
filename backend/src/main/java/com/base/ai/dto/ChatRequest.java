package com.base.ai.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * AI 对话请求
 *
 * @author base
 * @since 2026-03-16
 */
@Data
public class ChatRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户问题（必填）
     */
    @NotBlank(message = "问题不能为空")
    @Size(min = 1, max = 2000, message = "问题长度应在 1~2000 字符之间")
    private String message;

    /**
     * 上下文（选填，如 Dashboard 摘要）
     */
    @Size(max = 5000, message = "上下文长度不能超过 5000 字符")
    private String context;

    /**
     * 是否启用技能（Function Calling），默认 true
     */
    private Boolean enableSkills = true;

    /**
     * 图片尺寸，如 1024x1024、1536x1024、1024x1536。
     */
    @Pattern(regexp = "^\\d{3,4}x\\d{3,4}$", message = "图片尺寸格式必须为 宽x高，例如 1024x1024")
    private String imageSize;

    /**
     * 参考图片地址，用于基于上一张 AI 图片继续调整。
     */
    @Size(max = 2048, message = "参考图片地址长度不能超过 2048 字符")
    private String referenceImageUrl;

    /**
     * 参考图片原始提示词。
     */
    @Size(max = 3000, message = "参考图片提示词长度不能超过 3000 字符")
    private String referenceImagePrompt;

    /**
     * 参考图片优化提示词。
     */
    @Size(max = 3000, message = "参考图片优化提示词长度不能超过 3000 字符")
    private String referenceImageRevisedPrompt;
}
