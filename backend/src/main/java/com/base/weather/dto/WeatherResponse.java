package com.base.weather.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 全国天气响应
 */
@Data
public class WeatherResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 城市天气列表
     */
    private List<CityWeather> cities;

    /**
     * 数据更新时间
     */
    private String updateTime;

    /**
     * 温度范围 - 最低温
     */
    private Integer minTemp;

    /**
     * 温度范围 - 最高温
     */
    private Integer maxTemp;
}
