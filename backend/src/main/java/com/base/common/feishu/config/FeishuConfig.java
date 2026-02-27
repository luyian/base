package com.base.common.feishu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 飞书应用配置
 *
 * @author base
 * @since 2026-02-11
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "feishu")
public class FeishuConfig {

    /**
     * 是否启用飞书集成
     */
    private Boolean enabled = false;

    /**
     * 飞书应用 App ID
     */
    private String appId;

    /**
     * 飞书应用 App Secret
     */
    private String appSecret;

    /**
     * 飞书开放平台 API 基础地址
     */
    private String baseUrl = "https://open.feishu.cn/open-apis";

    /**
     * HTTP 请求超时时间（毫秒）
     */
    private Integer timeout = 30000;

    /**
     * 请求失败重试次数
     */
    private Integer retry = 3;

    /**
     * OAuth 授权回调地址
     */
    private String redirectUri;
}
