package com.base.weather.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 城市天气数据
 */
@Data
public class CityWeather implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 城市名称
     */
    private String city;

    /**
     * 城市行政编码（adcode）
     */
    private String adcode;

    /**
     * 省份
     */
    private String province;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 当前温度（℃）
     */
    private Integer temperature;

    /**
     * 天气现象（晴/多云/阴/雨等）
     */
    private String weather;

    /**
     * 风向
     */
    private String windDirection;

    /**
     * 风力等级
     */
    private String windPower;

    /**
     * 湿度（%）
     */
    private String humidity;

    /**
     * 数据发布时间
     */
    private String reportTime;
}
