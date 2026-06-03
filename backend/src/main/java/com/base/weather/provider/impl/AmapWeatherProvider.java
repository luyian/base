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
 * 高德天气数据源
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AmapWeatherProvider implements WeatherProvider {

    private static final String API_URL = "https://restapi.amap.com/v3/weather/weatherInfo";

    private final WeatherSourceConfig config;
    private final RestTemplate weatherRestTemplate = new RestTemplate();

    @Override
    public String getName() {
        return "amap";
    }

    @Override
    public CityWeather fetchWeather(String adcode, String cityName) {
        String key = config.getAmapKey();
        if (key == null || key.isEmpty()) {
            log.warn("高德天气 API Key 未配置");
            return null;
        }

        String url = API_URL + "?city=" + adcode + "&key=" + key + "&extensions=base&output=JSON";
        try {
            String result = weatherRestTemplate.getForObject(url, String.class);
            JSONObject json = JSON.parseObject(result);

            if (!"1".equals(json.getString("status"))) {
                String info = json.getString("info");
                log.warn("高德天气 API 返回异常: city={}, info={}", cityName, info);
                // QPS 超限，抛出专用异常让服务层快速降级
                if (info != null && info.contains("EXCEEDED")) {
                    throw new com.base.weather.provider.QpsExceededException(getName(), info);
                }
                return null;
            }

            JSONArray lives = json.getJSONArray("lives");
            if (lives == null || lives.isEmpty()) {
                return null;
            }

            JSONObject live = lives.getJSONObject(0);
            CityWeather weather = new CityWeather();
            weather.setCity(cityName);
            weather.setAdcode(adcode);
            weather.setWeather(live.getString("weather"));
            weather.setWindDirection(live.getString("winddirection"));
            weather.setWindPower(live.getString("windpower"));
            weather.setHumidity(live.getString("humidity"));
            weather.setReportTime(live.getString("reporttime"));

            String tempStr = live.getString("temperature");
            if (tempStr != null && !tempStr.isEmpty()) {
                try {
                    weather.setTemperature(Integer.parseInt(tempStr));
                } catch (NumberFormatException e) {
                    weather.setTemperature(null);
                }
            }

            return weather;
        } catch (Exception e) {
            log.error("高德天气 API 请求异常: city={}, error={}", cityName, e.getMessage());
            return null;
        }
    }
}
