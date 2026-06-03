package com.base.weather.service.impl;

import com.alibaba.fastjson2.JSON;
import com.base.system.entity.Region;
import com.base.system.mapper.RegionMapper;
import com.base.weather.dto.CityWeather;
import com.base.weather.dto.WeatherResponse;
import com.base.weather.factory.WeatherProviderFactory;
import com.base.weather.provider.WeatherProvider;
import com.base.weather.service.WeatherService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 天气服务实现
 * <p>
 * 城市数据从 sys_region 表（level=2）动态读取。
 * 通过 WeatherProviderFactory 获取数据源，支持主/备切换和降级。
 * Redis 缓存 30 分钟。
 * </p>
 */
@Slf4j
@Service
public class WeatherServiceImpl implements WeatherService {

    private static final String CACHE_KEY = "weather:realtime:all";
    private static final long CACHE_EXPIRE_MINUTES = 30;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private WeatherProviderFactory weatherProviderFactory;

    @Autowired
    private RegionMapper regionMapper;

    @Override
    public WeatherResponse getRealtimeWeather() {
        // 先查缓存
        String cached = stringRedisTemplate.opsForValue().get(CACHE_KEY);
        if (cached != null) {
            return JSON.parseObject(cached, WeatherResponse.class);
        }

        // 缓存未命中，从 sys_region 查询地级市
        List<Region> cities = regionMapper.selectList(
                new LambdaQueryWrapper<Region>()
                        .eq(Region::getLevel, 2)
                        .eq(Region::getDeleted, 0)
                        .isNotNull(Region::getLongitude)
                        .isNotNull(Region::getLatitude)
        );

        WeatherProvider primary = weatherProviderFactory.getPrimaryProvider();
        WeatherProvider fallback = weatherProviderFactory.getFallbackProvider();
        log.info("开始拉取全国天气，主数据源: {}，城市数: {}", primary.getName(), cities.size());

        List<CityWeather> weatherList = new ArrayList<>();
        int minTemp = Integer.MAX_VALUE;
        int maxTemp = Integer.MIN_VALUE;

        for (Region city : cities) {
            // 高德 adcode 为 regionCode 补末尾 "00"
            String adcode = city.getRegionCode() + "00";
            String cityName = city.getRegionName().replaceAll("市$|地区$|自治州$", "");

            CityWeather weather = fetchWithFallback(primary, fallback, adcode, cityName);
            if (weather != null) {
                // 补充省份信息：从父级区划获取
                weather.setProvince(getProvinceName(city.getParentId()));
                weather.setLongitude(city.getLongitude().doubleValue());
                weather.setLatitude(city.getLatitude().doubleValue());
                weatherList.add(weather);
                if (weather.getTemperature() != null) {
                    minTemp = Math.min(minTemp, weather.getTemperature());
                    maxTemp = Math.max(maxTemp, weather.getTemperature());
                }
            }
        }

        WeatherResponse response = new WeatherResponse();
        response.setCities(weatherList);
        response.setUpdateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.setMinTemp(minTemp == Integer.MAX_VALUE ? -10 : minTemp);
        response.setMaxTemp(maxTemp == Integer.MIN_VALUE ? 40 : maxTemp);

        // 写入缓存
        if (!weatherList.isEmpty()) {
            stringRedisTemplate.opsForValue().set(CACHE_KEY, JSON.toJSONString(response),
                    CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        }

        return response;
    }

    @Override
    public CityWeather getCityWeather(String adcode) {
        // 先从缓存中查找
        String cached = stringRedisTemplate.opsForValue().get(CACHE_KEY);
        if (cached != null) {
            WeatherResponse response = JSON.parseObject(cached, WeatherResponse.class);
            return response.getCities().stream()
                    .filter(c -> adcode.equals(c.getAdcode()))
                    .findFirst()
                    .orElse(null);
        }

        // 缓存无，从 sys_region 查城市信息后单独查询
        String regionCode = adcode.endsWith("00") ? adcode.substring(0, adcode.length() - 2) : adcode;
        Region city = regionMapper.selectOne(
                new LambdaQueryWrapper<Region>()
                        .eq(Region::getRegionCode, regionCode)
                        .eq(Region::getDeleted, 0)
        );
        if (city == null || city.getLongitude() == null) {
            return null;
        }

        String cityName = city.getRegionName().replaceAll("市$|地区$|自治州$", "");
        WeatherProvider primary = weatherProviderFactory.getPrimaryProvider();
        WeatherProvider fallback = weatherProviderFactory.getFallbackProvider();
        CityWeather weather = fetchWithFallback(primary, fallback, adcode, cityName);
        if (weather != null) {
            weather.setProvince(getProvinceName(city.getParentId()));
            weather.setLongitude(city.getLongitude().doubleValue());
            weather.setLatitude(city.getLatitude().doubleValue());
        }
        return weather;
    }

    /**
     * 获取省份名称
     */
    private String getProvinceName(Long parentId) {
        if (parentId == null || parentId == 0) {
            return "";
        }
        Region parent = regionMapper.selectById(parentId);
        if (parent == null) {
            return "";
        }
        return parent.getRegionName().replaceAll("省$|市$|自治区$|壮族自治区$|回族自治区$|维吾尔自治区$|特别行政区$", "");
    }

    /**
     * 带降级的天气查询
     */
    private CityWeather fetchWithFallback(WeatherProvider primary, WeatherProvider fallback, String adcode, String cityName) {
        try {
            CityWeather weather = primary.fetchWeather(adcode, cityName);
            if (weather != null) {
                return weather;
            }
        } catch (Exception e) {
            log.warn("主数据源[{}]查询失败: city={}, error={}", primary.getName(), cityName, e.getMessage());
        }

        // 降级
        if (fallback != null) {
            try {
                CityWeather weather = fallback.fetchWeather(adcode, cityName);
                if (weather != null) {
                    log.debug("降级数据源[{}]查询成功: city={}", fallback.getName(), cityName);
                    return weather;
                }
            } catch (Exception e) {
                log.warn("降级数据源[{}]查询失败: city={}, error={}", fallback.getName(), cityName, e.getMessage());
            }
        }

        return null;
    }
}
