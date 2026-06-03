package com.base.weather.service;

import com.base.weather.dto.CityWeather;
import com.base.weather.dto.WeatherResponse;

/**
 * 天气服务接口
 */
public interface WeatherService {

    /**
     * 获取全国实时天气数据
     *
     * @return 全国天气响应
     */
    WeatherResponse getRealtimeWeather();

    /**
     * 获取单个城市天气详情
     *
     * @param adcode 城市行政编码
     * @return 城市天气
     */
    CityWeather getCityWeather(String adcode);
}
