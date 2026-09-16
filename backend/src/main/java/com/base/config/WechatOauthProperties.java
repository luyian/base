package com.base.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 微信小程序登录配置属性类
 * <p>
 * 支持多小程序：默认 appId/appSecret + 可选的 appId → appSecret 映射。
 * 未命中映射时回退默认 appId/appSecret（兼容旧的小程序）。
 * </p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "oauth.wechat")
public class WechatOauthProperties {

    /**
     * 微信登录是否启用
     */
    private Boolean enabled;

    /**
     * 默认小程序 AppID
     */
    private String appId;

    /**
     * 默认小程序 AppSecret
     */
    private String appSecret;

    /**
     * 多小程序 appId → appSecret 映射（用于微信登录路由）
     */
    private Map<String, String> appSecrets;
}