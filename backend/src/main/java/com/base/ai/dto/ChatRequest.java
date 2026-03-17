package com.base.ai.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
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
}
