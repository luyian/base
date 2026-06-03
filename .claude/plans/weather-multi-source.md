# 天气模块多数据源重构方案

## 设计思路

参考项目已有的 `QuoteProvider` + `QuoteProviderFactory` 模式（股票报价模块），将天气查询抽象为 `WeatherProvider` 接口 + 工厂，支持多个天气数据源可配置切换和降级。

## 架构设计

```
WeatherController
    ↓
WeatherService
    ↓
WeatherProviderFactory（主数据源 + 降级）
    ├── AmapWeatherProvider     （高德，当前已有逻辑迁入）
    ├── HefengWeatherProvider   （和风天气）
    └── SeniverseWeatherProvider（心知天气）
```

## 新增/修改文件

### 1. `weather/provider/WeatherProvider.java` — 接口
```java
public interface WeatherProvider {
    String getName();
    CityWeather fetchWeather(String adcode, String cityName);
}
```

### 2. `weather/provider/impl/AmapWeatherProvider.java`
从 `WeatherServiceImpl` 中提取高德 API 调用逻辑。

### 3. `weather/provider/impl/HefengWeatherProvider.java`
对接和风天气 API（`devapi.qweather.com`），使用 locationId 查询。

### 4. `weather/provider/impl/SeniverseWeatherProvider.java`
对接心知天气 API（`api.seniverse.com`），使用城市拼音查询。

### 5. `weather/factory/WeatherProviderFactory.java`
仿照 `QuoteProviderFactory`，按配置选择主/备数据源。

### 6. `weather/config/WeatherSourceConfig.java`
```yaml
weather:
  source: amap          # 主数据源：amap / hefeng / seniverse
  fallback-enabled: true
  fallback-source: hefeng
  amap:
    key: ac6eab2fe5d85b345b726b13cbdbbef1
  hefeng:
    key: (用户配置)
  seniverse:
    key: (用户配置)
```

### 7. 修改 `WeatherServiceImpl`
- 移除硬编码的高德调用逻辑
- 改为通过 `WeatherProviderFactory` 获取 provider 调用
- 主数据源失败时自动降级

## 配置方式

天气 API Key 统一放在 `application-dev.yml` 的 `weather` 节点下，和股票模块 `stock.quote` 同样模式。

## 不改动的部分

- 前端页面不变
- Controller 接口不变
- Redis 缓存逻辑不变
- `CityAdcodeData` 城市数据不变
