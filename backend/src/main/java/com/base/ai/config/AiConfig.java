package com.base.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * AI 服务配置（OpenAI 兼容接口）
 * apiKey 建议仅通过环境变量注入，如：ai.api-key=${AI_API_KEY:}
 *
 * @author base
 * @since 2026-03-16
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ai")
public class AiConfig {

    /**
     * 是否启用 AI 助手
     */
    private Boolean enabled = false;

    /**
     * 第三方基础地址（如 https://api.openai.com 或 https://xxx.com/v1）
     */
    private String baseUrl;

    /**
     * chat 接口路径（可选）。不配置时默认：baseUrl 以 /v1 结尾则拼 /chat/completions，否则拼 /v1/chat/completions。
     * 若服务商路径不同可单独指定，如：/chat/completions 或 /api/v1/chat/completions
     */
    private String chatPath;

    /**
     * 第三方 API 密钥（建议仅通过环境变量注入）
     */
    private String apiKey;

    /**
     * 默认模型名称（可选）
     */
    private String model = "gpt-3.5-turbo";

    /**
     * 请求超时时间（毫秒）
     */
    private Integer timeout = 30000;

    /**
     * 失败重试次数
     */
    private Integer retry = 2;

    /**
     * 用户消息最大长度（字符）
     */
    private Integer maxMessageLength = 2000;

    /**
     * 上下文最大长度（字符）
     */
    private Integer maxContextLength = 5000;

    /**
     * 是否已配置并可调用（不暴露 apiKey）
     */
    public boolean isConfigured() {
        return Boolean.TRUE.equals(enabled)
                && baseUrl != null && !baseUrl.trim().isEmpty()
                && apiKey != null && !apiKey.trim().isEmpty();
    }

    /**
     * 获取 chat completions 完整 URL。
     * 若配置了 chatPath 则使用 baseUrl + chatPath；否则按 baseUrl 是否含 /v1 自动拼接。
     */
    public String getChatCompletionsUrl() {
        String url = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        if (chatPath != null && !chatPath.trim().isEmpty()) {
            String path = chatPath.trim();
            return path.startsWith("/") ? url + path : url + "/" + path;
        }
        if (url.endsWith("/v1")) {
            return url + "/chat/completions";
        }
        if (url.endsWith("/chat/completions")) {
            return url;
        }
        return url + "/v1/chat/completions";
    }
}
