package com.base.ai.config;

import com.base.ai.service.AiConfigProvider;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 大模型实例持有者：按当前配置构建并缓存 ChatLanguageModel，配置变更后需 clearCache
 *
 * @author base
 * @since 2026-03-17
 */
@Slf4j
@Component
public class AiChatModelHolder {

    private volatile ChatLanguageModel cached;

    /**
     * 获取当前配置对应的 ChatLanguageModel，未配置或未启用时返回 null
     */
    public ChatLanguageModel getModel(AiConfigProvider provider) {
        if (provider == null || !provider.isConfigured()) {
            return null;
        }
        if (cached != null) {
            return cached;
        }
        synchronized (this) {
            if (cached != null) {
                return cached;
            }
            String baseUrl = normalizeBaseUrl(provider.getBaseUrl());
            cached = OpenAiChatModel.builder()
                    .apiKey(provider.getApiKey())
                    .modelName(provider.getModel() != null ? provider.getModel() : "gpt-3.5-turbo")
                    .baseUrl(baseUrl)
                    .build();
            log.info("AI ChatLanguageModel 已按当前配置创建");
            return cached;
        }
    }

    /** 配置保存后调用，使下次对话使用新配置 */
    public void clearCache() {
        this.cached = null;
        log.info("AI ChatLanguageModel 缓存已清除");
    }

    private static String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            return baseUrl;
        }
        String url = baseUrl.trim();
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        if (!url.endsWith("/v1")) {
            url = url + "/v1";
        }
        return url;
    }
}
