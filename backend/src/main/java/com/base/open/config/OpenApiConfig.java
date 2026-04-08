package com.base.open.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 开放接口配置类
 *
 * @author base
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "open.api")
public class OpenApiConfig {

    /**
     * 是否启用开放接口
     */
    private Boolean enabled = true;

    /**
     * accessToken 有效期（秒），默认 2 小时
     */
    private Long tokenExpire = 7200L;

    /**
     * 注册的外部系统应用列表
     */
    private List<AppInfo> apps;

    /**
     * 根据 appId 查找应用配置
     *
     * @param appId 应用ID
     * @return 应用配置，未找到返回 null
     */
    public AppInfo getAppByAppId(String appId) {
        if (apps == null || appId == null) {
            return null;
        }
        return apps.stream()
                .filter(app -> appId.equals(app.getAppId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 应用信息
     */
    @Data
    public static class AppInfo {

        /**
         * 应用ID
         */
        private String appId;

        /**
         * 应用密钥
         */
        private String appSecret;

        /**
         * 应用名称
         */
        private String name;
    }
}
