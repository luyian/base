package com.base.weather.config;

import com.base.system.service.ConfigService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 天气数据源配置
 * <p>
 * source/fallback 等结构化配置从 yml 读取，
 * 各平台 API Key 从 sys_config 表读取（支持运行时动态修改）。
 * </p>
 */
@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "weather")
public class WeatherSourceConfig {

    /**
     * 主数据源：amap / hefeng / seniverse
     */
    private String source = "amap";

    /**
     * 是否启用降级（主数据源失败时切换到备用）
     */
    private boolean fallbackEnabled = false;

    /**
     * 降级数据源
     */
    private String fallbackSource = "hefeng";

    private ConfigService configService;

    /**
     * 注入 ConfigService（通过构造器注入会和 @ConfigurationProperties 冲突，用 setter 注入）
     */
    @javax.annotation.Resource
    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * 获取高德天气 Key（从 sys_config 表读取，configKey = weather.amap.key）
     */
    public String getAmapKey() {
        return getKeyFromConfig("weather.amap.key");
    }

    /**
     * 获取和风天气 Key（从 sys_config 表读取，configKey = weather.hefeng.key）
     */
    public String getHefengKey() {
        return getKeyFromConfig("weather.hefeng.key");
    }

    /**
     * 获取心知天气 Key（从 sys_config 表读取，configKey = weather.seniverse.key）
     */
    public String getSeniverseKey() {
        return getKeyFromConfig("weather.seniverse.key");
    }

    private String getKeyFromConfig(String configKey) {
        if (configService == null) {
            return "";
        }
        String value = configService.getConfigValueByKey(configKey);
        return value != null ? value : "";
    }
}
