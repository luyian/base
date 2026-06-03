package com.base.weather.factory;

import com.base.weather.config.WeatherSourceConfig;
import com.base.weather.provider.WeatherProvider;
import com.base.weather.provider.impl.AmapWeatherProvider;
import com.base.weather.provider.impl.HefengWeatherProvider;
import com.base.weather.provider.impl.SeniverseWeatherProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 天气数据源工厂
 * <p>
 * 支持主数据源和降级逻辑，参考 QuoteProviderFactory 设计
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherProviderFactory {

    private final WeatherSourceConfig config;
    private final AmapWeatherProvider amapWeatherProvider;
    private final HefengWeatherProvider hefengWeatherProvider;
    private final SeniverseWeatherProvider seniverseWeatherProvider;

    private final Map<String, WeatherProvider> providerMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        providerMap.put("amap", amapWeatherProvider);
        providerMap.put("hefeng", hefengWeatherProvider);
        providerMap.put("seniverse", seniverseWeatherProvider);
        log.info("天气数据源工厂初始化完成，支持的数据源: {}", providerMap.keySet());
    }

    /**
     * 获取主数据源
     */
    public WeatherProvider getPrimaryProvider() {
        String source = config.getSource();
        WeatherProvider provider = providerMap.get(source);
        if (provider == null) {
            log.warn("未找到天气数据源: {}, 使用默认 amap", source);
            provider = amapWeatherProvider;
        }
        return provider;
    }

    /**
     * 获取降级数据源
     */
    public WeatherProvider getFallbackProvider() {
        if (!config.isFallbackEnabled()) {
            return null;
        }
        String source = config.getFallbackSource();
        WeatherProvider provider = providerMap.get(source);
        if (provider == null) {
            log.warn("未找到降级天气数据源: {}", source);
            return null;
        }
        return provider;
    }
}
