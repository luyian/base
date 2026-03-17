package com.base.ai.service;

import com.base.ai.dto.ChatRequest;
import com.base.ai.dto.ChatResponse;

/**
 * AI 对话服务
 *
 * @author base
 * @since 2026-03-16
 */
public interface AiService {

    /**
     * 一次性对话（非流式）
     *
     * @param request 对话请求
     * @return 对话响应
     */
    ChatResponse chat(ChatRequest request);
}
