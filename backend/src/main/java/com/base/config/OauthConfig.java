package com.base.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * OAuth 配置属性类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "oauth.github")
public class OauthConfig {

    /**
     * GitHub OAuth App 的 Client ID
     */
    private String clientId;

    /**
     * GitHub OAuth App 的 Client Secret
     */
    private String clientSecret;

    /**
     * 授权回调地址
     */
    private String redirectUri;
}
