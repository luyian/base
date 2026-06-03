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
 * 和风天气数据源
 * <p>
 * API 文档：https://dev.qweather.com/docs/api/weather/weather-now/
 * 免费版每日 1000 次调用
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HefengWeatherProvider implements WeatherProvider {

    private static final String API_URL = "https://devapi.qweather.com/v7/weather/now";

    private final WeatherSourceConfig config;
    private final RestTemplate weatherRestTemplate = new RestTemplate();

    @Override
    public String getName() {
        return "hefeng";
    }

    @Override
    public CityWeather fetchWeather(String adcode, String cityName) {
        String key = config.getHefengKey();
        if (key == null || key.isEmpty()) {
            log.warn("和风天气 API Key 未配置");
            return null;
        }

        // 和风天气使用 LocationID，与高德 adcode 格式一致（去掉末尾两位0后补"01"）
        // 或者直接用经纬度查询；这里用 adcode 作为 location 尝试
        String url = API_URL + "?location=" + adcode + "&key=" + key;
        try {
            String result = weatherRestTemplate.getForObject(url, String.class);
            JSONObject json = JSON.parseObject(result);

            String code = json.getString("code");
            if (!"200".equals(code)) {
                log.warn("和风天气 API 返回异常: city={}, code={}", cityName, code);
                return null;
            }

            JSONObject now = json.getJSONObject("now");
            if (now == null) {
                return null;
            }

            CityWeather weather = new CityWeather();
            weather.setCity(cityName);
            weather.setAdcode(adcode);
            weather.setWeather(now.getString("text"));
            weather.setWindDirection(now.getString("windDir"));
            weather.setWindPower(now.getString("windScale"));
            weather.setHumidity(now.getString("humidity"));
            weather.setReportTime(now.getString("obsTime"));

            String tempStr = now.getString("temp");
            if (tempStr != null && !tempStr.isEmpty()) {
                try {
                    weather.setTemperature(Integer.parseInt(tempStr));
                } catch (NumberFormatException e) {
                    weather.setTemperature(null);
                }
            }

            return weather;
        } catch (Exception e) {
            log.error("和风天气 API 请求异常: city={}, error={}", cityName, e.getMessage());
            return null;
        }
    }
}
