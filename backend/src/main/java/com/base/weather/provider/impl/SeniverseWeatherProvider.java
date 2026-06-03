package com.base.weather.provider.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.base.weather.config.WeatherSourceConfig;
import com.base.weather.dto.CityWeather;
import com.base.weather.provider.WeatherProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 心知天气数据源
 * <p>
 * API 文档：https://seniverse.yuque.com/hyper/api
 * 免费版每日 800 次调用
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SeniverseWeatherProvider implements WeatherProvider {

    private static final String API_URL = "https://api.seniverse.com/v3/weather/now.json";

    private final WeatherSourceConfig config;
    private final RestTemplate weatherRestTemplate = new RestTemplate();

    @Override
    public String getName() {
        return "seniverse";
    }

    @Override
    public CityWeather fetchWeather(String adcode, String cityName) {
        String key = config.getSeniverseKey();
        if (key == null || key.isEmpty()) {
            log.warn("心知天气 API Key 未配置");
            return null;
        }

        // 心知天气使用城市名称或城市ID查询
        String url = API_URL + "?key=" + key + "&location=" + cityName + "&language=zh-Hans&unit=c";
        try {
            String result = weatherRestTemplate.getForObject(url, String.class);
            JSONObject json = JSON.parseObject(result);

            JSONArray results = json.getJSONArray("results");
            if (results == null || results.isEmpty()) {
                log.warn("心知天气 API 无数据: city={}", cityName);
                return null;
            }

            JSONObject first = results.getJSONObject(0);
            JSONObject now = first.getJSONObject("now");
            if (now == null) {
                return null;
            }

            CityWeather weather = new CityWeather();
            weather.setCity(cityName);
            weather.setAdcode(adcode);
            weather.setWeather(now.getString("text"));
            weather.setWindDirection(now.getString("wind_direction"));
            weather.setWindPower(now.getString("wind_scale"));
            weather.setHumidity(now.getString("humidity"));

            // 心知天气的更新时间在 last_update 字段
            String lastUpdate = first.getString("last_update");
            weather.setReportTime(lastUpdate != null ? lastUpdate : "");

            String tempStr = now.getString("temperature");
            if (tempStr != null && !tempStr.isEmpty()) {
                try {
                    weather.setTemperature(Integer.parseInt(tempStr));
                } catch (NumberFormatException e) {
                    weather.setTemperature(null);
                }
            }

            return weather;
        } catch (Exception e) {
            log.error("心知天气 API 请求异常: city={}, error={}", cityName, e.getMessage());
            return null;
        }
    }
}
