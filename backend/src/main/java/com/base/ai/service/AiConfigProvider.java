package com.base.ai.service;

/**
 * 大模型配置提供者（优先读 DB，无则用 yml）
 *
 * @author base
 * @since 2026-03-17
 */
public interface AiConfigProvider {

    Boolean getEnabled();

    String getBaseUrl();

    String getApiKey();

    String getModel();

    Integer getTimeout();

    Integer getRetry();

    Integer getMaxMessageLength();

    Integer getMaxContextLength();

    /** 是否已配置并可调用（enabled 且 baseUrl、apiKey 非空） */
    boolean isConfigured();
}
