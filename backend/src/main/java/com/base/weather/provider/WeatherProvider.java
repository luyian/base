package com.base.weather.provider;

import com.base.weather.dto.CityWeather;

/**
 * 天气数据源提供者接口
 * <p>
 * 参考 QuoteProvider 设计，各天气平台实现此接口
 * </p>
 */
public interface WeatherProvider {

    /**
     * 数据源名称
     *
     * @return 名称标识（如 amap、hefeng、seniverse）
     */
    String getName();

    /**
     * 查询单个城市实时天气
     *
     * @param adcode   城市行政编码（6位高德格式）
     * @param cityName 城市名称（用于部分 API 按名称查询）
     * @return 天气数据，查询失败返回 null
     */
    CityWeather fetchWeather(String adcode, String cityName);
}
