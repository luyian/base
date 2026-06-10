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

    /**
     * 带技能的对话（Function Calling）
     * AI 可自动调用注册的技能获取实时数据，结合数据生成回答
     *
     * @param request 对话请求
     * @return 对话响应
     */
    ChatResponse chatWithSkills(ChatRequest request);
}
