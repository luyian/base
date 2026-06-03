package com.base.weather.service.impl;

import com.alibaba.fastjson2.JSON;
import com.base.system.entity.Region;
import com.base.system.mapper.RegionMapper;
import com.base.weather.dto.CityWeather;
import com.base.weather.dto.WeatherResponse;
import com.base.weather.factory.WeatherProviderFactory;
import com.base.weather.provider.QpsExceededException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    /**
     * 请求间隔（毫秒），用于控制 QPS 避免触发数据源限流。
     * 高德免费版并发 QPS 较低，200ms（≈5QPS）可有效避免触发 CUQPS 限制。
     */
    private static final long REQUEST_INTERVAL_MS = 200;

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

        // 批量预加载省份信息，避免逐个城市查询
        Map<Long, String> provinceNameMap = loadProvinceNameMap(cities);

        WeatherProvider primary = weatherProviderFactory.getPrimaryProvider();
        WeatherProvider fallback = weatherProviderFactory.getFallbackProvider();
        log.info("开始拉取全国天气，主数据源: {}，城市数: {}", primary.getName(), cities.size());

        List<CityWeather> weatherList = new ArrayList<>();
        int minTemp = Integer.MAX_VALUE;
        int maxTemp = Integer.MIN_VALUE;

        // 当前使用的数据源（QPS 超限时会切换）
        WeatherProvider currentProvider = primary;
        boolean switchedToFallback = false;

        for (int i = 0; i < cities.size(); i++) {
            Region city = cities.get(i);
            String cityName = city.getRegionName().replaceAll("市$|地区$|自治州$", "");

            // 过滤非真实城市（如"省直辖县级行政区划"等行政占位记录）
            if (cityName.contains("直辖") || cityName.contains("行政区划")) {
                continue;
            }

            // regionCode 转高德 adcode（6位）：2位补0000，4位补00
            String adcode = toAmapAdcode(city.getRegionCode());

            CityWeather weather = null;
            try {
                weather = currentProvider.fetchWeather(adcode, cityName);
            } catch (QpsExceededException e) {
                // QPS 超限，切换到降级数据源
                log.warn("数据源[{}] QPS 超限，已完成 {}/{} 城市，切换到降级数据源",
                        currentProvider.getName(), i, cities.size());
                if (fallback != null && !switchedToFallback) {
                    currentProvider = fallback;
                    switchedToFallback = true;
                    // 用降级数据源重试当前城市
                    weather = fetchSafely(currentProvider, adcode, cityName);
                }
            } catch (Exception e) {
                log.warn("数据源[{}]查询失败: city={}, error={}", currentProvider.getName(), cityName, e.getMessage());
                // 单城市失败，尝试降级
                if (fallback != null && !switchedToFallback) {
                    weather = fetchSafely(fallback, adcode, cityName);
                }
            }

            if (weather != null) {
                weather.setProvince(provinceNameMap.getOrDefault(city.getParentId(), ""));
                weather.setLongitude(city.getLongitude().doubleValue());
                weather.setLatitude(city.getLatitude().doubleValue());
                weatherList.add(weather);
                if (weather.getTemperature() != null) {
                    minTemp = Math.min(minTemp, weather.getTemperature());
                    maxTemp = Math.max(maxTemp, weather.getTemperature());
                }
            }

            // QPS 限流：请求间加间隔
            if (i < cities.size() - 1) {
                sleep(REQUEST_INTERVAL_MS);
            }
        }

        log.info("天气拉取完成，成功 {}/{} 城市", weatherList.size(), cities.size());

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
        // adcode 是 6 位高德格式，需要还原为本地 regionCode 查表
        String regionCode = adcode.replaceAll("0+$", "");
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
     * 批量加载省份名称映射
     *
     * @param cities 地级市列表
     * @return parentId -> 省份名称
     */
    private Map<Long, String> loadProvinceNameMap(List<Region> cities) {
        List<Long> parentIds = cities.stream()
                .map(Region::getParentId)
                .filter(id -> id != null && id != 0)
                .distinct()
                .collect(Collectors.toList());

        if (parentIds.isEmpty()) {
            return new HashMap<>();
        }

        List<Region> provinces = regionMapper.selectBatchIds(parentIds);
        Map<Long, String> map = new HashMap<>(provinces.size());
        for (Region province : provinces) {
            String name = province.getRegionName()
                    .replaceAll("省$|市$|自治区$|壮族自治区$|回族自治区$|维吾尔自治区$|特别行政区$", "");
            map.put(province.getId(), name);
        }
        return map;
    }

    /**
     * 获取省份名称（单城市查询时使用）
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
     * 安全获取天气（捕获异常返回 null）
     */
    private CityWeather fetchSafely(WeatherProvider provider, String adcode, String cityName) {
        try {
            return provider.fetchWeather(adcode, cityName);
        } catch (Exception e) {
            log.warn("数据源[{}]查询失败: city={}, error={}", provider.getName(), cityName, e.getMessage());
            return null;
        }
    }

    /**
     * 带降级的天气查询（单城市场景）
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

    /**
     * 线程休眠（用于 QPS 控制）
     */
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 本地 regionCode 转高德 6 位 adcode
     * <p>
     * sys_region 表存储的 regionCode：直辖市 2 位（如 "11"），地级市 4 位（如 "1301"）。
     * 高德 API 要求 6 位 adcode（如 "110000"、"130100"）。
     * </p>
     *
     * @param regionCode 本地区划代码（2位或4位）
     * @return 6位高德 adcode
     */
    private String toAmapAdcode(String regionCode) {
        if (regionCode == null) {
            return "";
        }
        int len = regionCode.length();
        if (len >= 6) {
            return regionCode.substring(0, 6);
        }
        // 不足 6 位，右侧补 0
        StringBuilder sb = new StringBuilder(regionCode);
        while (sb.length() < 6) {
            sb.append('0');
        }
        return sb.toString();
    }
}
