package com.base.ai.service;

/**
 * AI 图片生成服务。
 *
 * @author base
 */
public interface AiImageService {

    /**
     * 根据用户输入生成图片，并返回可直接在 Markdown 中展示的内容。
     *
     * @param userMessage 用户原始输入
     * @return Markdown 图片内容
     */
    String generateImage(String userMessage);
}
