package com.base.weather.controller;

import com.base.system.common.Result;
import com.base.weather.dto.CityWeather;
import com.base.weather.dto.WeatherResponse;
import com.base.weather.service.WeatherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 天气查询控制器
 */
@RestController
@RequestMapping("/weather")
@Api(tags = "天气查询")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    /**
     * 获取全国实时天气数据
     */
    @GetMapping("/realtime")
    @ApiOperation("获取全国实时天气")
    public Result<WeatherResponse> getRealtimeWeather() {
        WeatherResponse response = weatherService.getRealtimeWeather();
        return Result.success(response);
    }

    /**
     * 获取单个城市天气详情
     */
    @GetMapping("/city/{adcode}")
    @ApiOperation("获取城市天气详情")
    public Result<CityWeather> getCityWeather(@PathVariable String adcode) {
        CityWeather weather = weatherService.getCityWeather(adcode);
        if (weather == null) {
            return Result.error("未找到该城市天气数据");
        }
        return Result.success(weather);
    }
}
