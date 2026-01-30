# 股票数据分析功能 - 需求文档（第一、二阶段）

## 一、功能概述

基于 iTick API 构建股票数据拉取和本地存储功能，为后续数据分析提供数据基础。

### 1.1 数据来源
- **API 服务商**：iTick (https://docs.itick.org/)
- **认证方式**：API Key（请求头携带 token），支持多 Token 轮询

### 1.2 数据范围
| 市场 | 说明 |
|------|------|
| A股 | 沪深两市股票 |
| 港股 | 香港联交所股票 |

### 1.3 数据类型
| 类型 | 说明 |
|------|------|
| 股票列表 | 股票代码、名称、市场等基础信息 |
| K线数据 | 日K线（开盘价、收盘价、最高价、最低价、成交量） |

### 1.4 核心设计原则
1. **HTTP 主动拉取**：使用 HTTP 请求主动拉取数据
2. **公共请求方法**：封装通用的 HTTP 请求工具类
3. **数据处理分离**：拉取与处理分离，使用数据工厂模式
4. **可视化映射**：前端可编辑源数据与目标数据的字段映射
5. **Token 轮询**：支持多 Token 管理，轮询使用，支持作废/添加
6. **自选股票池**：仅拉取用户自选的股票数据
7. **K线图展示**：前端提供 K 线图可视化

---

## 二、数据库设计

> **命名规范**：所有股票模块表使用 `stk_` 前缀，字段名见名知意

### 2.1 Token 管理表 `stk_api_token`

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | bigint | 主键，自增 |
| token_value | varchar(200) | Token 值 |
| token_name | varchar(50) | Token 名称/备注 |
| provider | varchar(20) | 服务商（itick） |
| status | tinyint | 状态（0-作废, 1-正常） |
| last_used_time | datetime | 最后使用时间 |
| use_count | int | 使用次数 |
| daily_limit | int | 每日限额（0表示无限制） |
| daily_used | int | 当日已用次数 |
| expire_time | datetime | 过期时间（null表示永不过期） |
| create_time | datetime | 创建时间 |
| create_by | varchar(64) | 创建人 |
| update_time | datetime | 更新时间 |
| update_by | varchar(64) | 更新人 |
| deleted | tinyint | 逻辑删除标志 |

**索引**：
- `idx_provider_status` - (provider, status) 联合索引

### 2.2 数据映射配置表 `stk_data_mapping`

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | bigint | 主键，自增 |
| mapping_code | varchar(50) | 映射编码（唯一标识） |
| mapping_name | varchar(100) | 映射名称 |
| source_type | varchar(50) | 源数据类型（如：itick_stock_list） |
| target_table | varchar(50) | 目标表名 |
| field_mapping | text | 字段映射配置（JSON格式） |
| transform_script | text | 转换脚本（可选） |
| status | tinyint | 状态（0-禁用, 1-启用） |
| create_time | datetime | 创建时间 |
| create_by | varchar(64) | 创建人 |
| update_time | datetime | 更新时间 |
| update_by | varchar(64) | 更新人 |
| deleted | tinyint | 逻辑删除标志 |

**field_mapping JSON 示例**：
```json
{
  "mappings": [
    {"source": "symbol", "target": "stock_code", "type": "string"},
    {"source": "name", "target": "stock_name", "type": "string"},
    {"source": "open", "target": "open_price", "type": "decimal", "scale": 4}
  ]
}
```

### 2.3 股票基础信息表 `stk_stock_info`

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | bigint | 主键，自增 |
| stock_code | varchar(20) | 股票代码（如：600000.SH, 00700.HK） |
| stock_name | varchar(100) | 股票名称 |
| market | varchar(10) | 市场（SH-沪市, SZ-深市, HK-港股） |
| exchange | varchar(20) | 交易所 |
| currency | varchar(10) | 交易货币（CNY/HKD） |
| status | tinyint | 状态（0-退市, 1-正常） |
| create_time | datetime | 创建时间 |
| create_by | varchar(64) | 创建人 |
| update_time | datetime | 更新时间 |
| update_by | varchar(64) | 更新人 |
| deleted | tinyint | 逻辑删除标志 |

**索引**：
- `uk_stock_code` - stock_code 唯一索引
- `idx_market` - market 普通索引

### 2.4 自选股票表 `stk_watchlist`

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | bigint | 主键，自增 |
| user_id | bigint | 用户ID |
| stock_code | varchar(20) | 股票代码 |
| sort_order | int | 排序号 |
| remark | varchar(200) | 备注 |
| create_time | datetime | 创建时间 |
| create_by | varchar(64) | 创建人 |
| update_time | datetime | 更新时间 |
| update_by | varchar(64) | 更新人 |
| deleted | tinyint | 逻辑删除标志 |

**索引**：
- `uk_user_stock` - (user_id, stock_code) 联合唯一索引
- `idx_user_id` - user_id 普通索引

### 2.5 K线数据表 `stk_kline_daily`

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | bigint | 主键，自增 |
| stock_code | varchar(20) | 股票代码 |
| trade_date | date | 交易日期 |
| open_price | decimal(12,4) | 开盘价 |
| high_price | decimal(12,4) | 最高价 |
| low_price | decimal(12,4) | 最低价 |
| close_price | decimal(12,4) | 收盘价 |
| volume | bigint | 成交量 |
| amount | decimal(18,4) | 成交额 |
| change_rate | decimal(10,4) | 涨跌幅(%) |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

**索引**：
- `uk_stock_date` - (stock_code, trade_date) 联合唯一索引
- `idx_trade_date` - trade_date 普通索引

---

## 三、第一阶段：数据拉取

### 3.1 公共 HTTP 请求工具

#### 3.1.1 HttpClientUtil 工具类
```java
/**
 * 公共 HTTP 请求工具类
 */
public class HttpClientUtil {
    // GET 请求
    public static <T> T get(String url, Map<String, String> headers, Class<T> responseType);

    // POST 请求
    public static <T> T post(String url, Object body, Map<String, String> headers, Class<T> responseType);

    // 带重试的请求
    public static <T> T executeWithRetry(Supplier<T> request, int maxRetry);
}
```

### 3.2 Token 生命周期管理

#### 3.2.1 Token 轮询策略
```
┌─────────────────────────────────────────────────────┐
│                  Token 管理器                        │
├─────────────────────────────────────────────────────┤
│  1. 获取可用 Token 列表（status=1, 未过期, 未超限）    │
│  2. 按最后使用时间排序，选择最久未用的 Token          │
│  3. 更新 Token 使用记录（last_used_time, use_count） │
│  4. 每日零点重置 daily_used 计数                     │
└─────────────────────────────────────────────────────┘
```

#### 3.2.2 TokenManager 服务
```java
public interface TokenManager {
    // 获取下一个可用 Token
    String getNextToken(String provider);

    // 标记 Token 使用完成
    void markTokenUsed(Long tokenId);

    // 作废 Token
    void disableToken(Long tokenId);

    // 添加新 Token
    void addToken(ApiTokenDTO tokn    // 重置每日计数（定时任务调用）
    void resetDailyCount();
}
```

### 3.3 iTick API 接口封装

#### 3.3.1 ITickApiClient
```java
/**
 * iTick API 客户端 - 负责数据拉取
 */
public interface ITickApiClient {
    // 获取股票列表（原始数据）
    String fetchStockList(String market);

    // 获取K线数据（原始数据）
    String fetchKlineData(String stockCode, String period, LocalDate start, LocalDate end);
}
```

### 3.4 数据工厂模式

#### 3.4.1 架构设计
```
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│  API Client  │───▶│ Data Factory │───▶│   Database   │
│  (数据拉取)   │    │  (数据转换)   │    │   (数据存储)  │
└──────────────┘    ──────┘    └──────────────┘
                           │
                           ▼
                    ┌──────────────┐
                    │ 映射配置表    │
                    │ stk_data_    │
                    │ mapping      │
                    └──────────────┘
```

#### 3.4.2 DataFactory 接口
```java
/**
 * 数据工厂 - 负责源数据到目标数据的转换
 */
public interface DataFactory {
    // 根据映射配置转换数据
    <T> List<T> transform(String sourceJson, String mappingCode, Class<T> targetClass);

    // 获取映射配置
    DataMappingDTO getMapping(String mappingCode);

    // 保存映射配置（前端可视化编辑）
    void saveMapping(DataMappingDTO mapping);
}
```

#### 3.4.3 字段映射
```java
/**
 * 字段映射处理器
 */
public class FieldMappingProcessor {
    // 执行字段映射
    public Object mapField(Object sourceValue, FieldMapping mapping);

    // 支持的转换类型：string, int, long, decimal, date, datetime
}
```

### 3.5 配置项

在 `application.yml` 中添加：
```yaml
stock:
  itick:
    base-url: https://api.itick.org
    timeout: 30000
    retry: 3
```

---

## 四、第二阶段：本地存储

### 4.1 后端模块结构

```
backend/src/main/java/com/base/stock/
├── controller/
│   ├── StockController.java        # 股票查询接口
│   ├── StockSyncController.java    # 数据同步接口
│   ├── TokenController.java        # Token 管理接口
│   ├── DataMappingConer.java  # 映射配置接口
│   └──atchlistController.java    # 自选股票接口
├── service/
│   ├── StockService.java           # 股票业务服务
│   ├── StockSyncService.java       # 数据同步服务
│   ├── TokenManager.java           # Token 管理器
│   ├── DataFactory.java            # 数据工厂
│   ├── WatchlistService.java       # 自选股票服务
│   └── impl/
├── mapper/
│   ├── StockInfoMapper.java
│   ├── StockKlineMapper.java
│   ├── ApiTokenMapper.java
│   ├── DataMappingMapper.java
│   └── WatchlistMapper.java
├── entity/
│   ├── StockInfo.java
│   ├── StockKline.java
│   ├── ApiToken.java
│   ├── DataMapping.java
│   └── Watchlist.java
├── dto/
├── client/
│   └── ITickApiClient.java         # iTick API 客户端
├── factory/
│   ├── DataFactory.java
│   └── FieldMappingProcessor.java
├── task/
│   └── StockSyncTask.java          # 定时同步任务
└── util/
    └── HttpClientUtil.java         # HTTP 工具类
```

### 4.2 数据同步策略

#### 4.2.1 股票列表同步
- **触发方式**：手动触发 / 每日定时（每天 9:00）
- **同步逻辑**：
  1. TokenManager 获取可用 Token
  2. ITickApiClient 拉取原始数据
  3. DataFactory 根据映射配置转换数据
  4. 与本地数据对比，新增/更新/标记退市

#### 4.2.2 K线数据同步
- **触发方式**：手动触发 / 每日定时（每天 18:00，收盘后）
- **同步范围**：仅同步自选股票池中的股票
- **历史数据**：首次同步拉取近1个月的历史数据
- *辑**：
  1. 获取自选股票池中的股票列表
  2. TokenManager 获取可用 Token（轮询）
  3. ITickApiClient 拉取原始 K 线数据
  4. DataFactory 转换数据
  5. 增量插入数据库

### 4.3 定时任务

```java
@Component
public class StockSyncTask {

    // 每天 9:00 同步股票列表
    @Scheduled(cron = "0 0 9 * * ?")
    public void syncStockList() { ... }

    // 每天 18:00 同步K线数据（自选股票）
    @Scheduled(cron = "0 0 18 * * ?")
    public void syncKlineData() { ... }

    // 每天 0:00 重置 Token 每日计数
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetTokenDailyCount() { ... }
}
```

### 4.4 后端 API 接口

#### 4.4.1 Token 管理
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/stock/token/list` | GET | 查询 Token 列表 |
| `/api/stock/token` | POST | 添加 Token |
| `/api/stock/token/{id}` | PUT | 更新 Token |
| `/api/stock/token/{id}/disable` | PUT | 作废 Token |
| `/api/stock/token/{id}` | DELETE | 删除 Token |

#### 4.4.2 数据映射配置
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/stock/mapping/list` | GET | 查询映射配置列表 |
| `/api/stock/mapping/{code}` | GET | 获取映射配置详情 |
| `/api/stock/mapping` | POST | 保存映射配置 |
| `/api/stock/mapping/{id}` | DELETE | 删除映射配置 |

#### 4.4.3 自选股票
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/stock/watchlist` | GET | 查询自选股票列表 |
| `/api/stock/watchlist` | POST | 添加自选股票 |
| `/api/stock/watchlist/{id}` | DELETE | 删除自选股票 |
| `/api/stock/watchlist/sort` | PUT | 调整排序 |

#### 4.4.4 数据同步
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/stock/sync/stock-list` | POST | 手动同步股票列表 |
| `/api/stock/sync/kline` | POST | 手动同步K线数据 |

#### 4.4.5 数据查询
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/stock/list` | GET | 查询股票列表（分页） |
| `/api/stock/{code}` | GET | 查询股票详情 |
| `/api/stock/{code}/kline` | GET | 查询K线数据 |

---

## 五、前端页面设计

### 5.1 前端目录结构

```
frontend/src/views/stock/
├── index.vue                    # 股票列表页面
├── detail.vue                   # 股K线图）
├── watchlist/
│   └── index.vue                # 自选股票管理
├── token/
│   └── index.vue                # Token 管理页面
├── mapping/
│   ├── index.vue                # 映射配置列表
│   └── edit.vue                 # 映射配置编辑（可视化）
└── components/
    ├── KlineChart.vue           # K线图组件
    ├── StockSearch.vue          # 股票搜索组件
    └── MappingEditor.vue        # 字段映射编辑器
```

### 5.2 页面功能说明

#### 5.2.1 股票列表页面 `/stock`
- 股票列表展示（支持搜索、筛选市场）
- 添加/移除自选
- 跳转详情页

#### 5.2.2 股票详情页面 `/stock/:code`
- 股票基本信息展示
- **K线图展示**（使用 ECharts）
- 支持切换时间范围（近1周/1月/n#### 5.2.3 自选股票页面 `/stock/watchlist`
- 自选股票列表
- 拖拽排序
- 批量同步 K 线数据

#### 5.2.4 Token 管理页面 `/stock/token`
- Token 列表（显示状态、使用次数、最后使用时间）
- 添加/编辑/作废 Token
- 查看 Token 使用统计

#### 5.2.5 映射配置页面 `/stock/mapping`
- 映射配置列表
- **可视化映射编辑器**：
  - 左侧：源数据字段列表
  - 右侧：目标表字段列表
  - 中间：拖拽连线建立映射关系
  - 支持配置字段类型转换

### 5.3 K线图组件

使用 **ECharts** 实现 K 线图：

```vue
<template>
  <div ref="chartRef" class="kline-chart"></div>
</template>

<script setup>
// 使用 ECharts 的 candlestick 图表类型
// 支持缩放、拖拽查看
// 显示 MA5、MA10、MA20 均线
</script>
```

### 5.4 前端路由配置

```javascript
// 股票模块路由
{
  path: '/stock',
  component: Layout,
  children: [
    { path: '', component: () => import('@/views/stock/index.vue') },
    { path: ':code', component: () => import('@/views/stock/detail.vue') },
    { path: 'watchlist', component: () => import('@/views/stock/watchlist/index.vue') },
    { path: 'token', component: () => import('@/views/stock/token/index.vue') },
    { path: 'mapping', component: () => import('@/views/stock/mapping/index.vue') },
    { path: 'mapping/edit/:id?', component: () => import('@/views/stock/mapping/edit.vue') }
  ]
}
```

---

## 六、关键文件清单

### 6.1 后端文件

| 文件 | 说明 |
|------|------|
| `backend/src/main/resources/db/stock_schema.sql` | 股票模块表结构（stk_前缀） |
| `backend/src/main/java/com/base/stock/entity/StockInfo.java` | 股票信息实体 |
| `backend/src/main/java/com/base/stock/entity/StockKline.java` | K线数据实体 |
| `backend/src/main/java/com/base/stock/entity/Watchlist.java` | 自选股票实体 |
| `backend/src/main/java/com/base/stock/entity/ApiToken.java` | Token 实体 |
| `backend/src/main/java/com/base/stock/entity/DataMapping.java` | 映射配置实体 |
| `backend/src/main/java/com/base/stock/util/HttpClientUtil.java` | HTTP 工具类 |
| `backend/src/main/java/com/base/stock/service/TokenManager.java` | Token 管理器 |
| `backend/src/main/java/com/base/stock/factory/DataFactory.java` | 数据工厂 |
| `backend/src/main/java/com/base/stock/client/ITickApiClient.java` | iTick API 客户端 |
| `backend/src/main/java/com/base/stock/task/StockSyncTask.java` | 定时同步任务 |

### 6.2 前端文件

| 文件 | 说明 |
|------|------|
| `frontend/src/views/stock/index.vue` | 股票列表页面 |
| `frontend/src/views/stock/detail.vue` | 股票详情页面（K线图） |
| `frontend/src/views/stock/watchlist/index.vue` | 自选股票页面 |
| `frontend/src/views/stock/token/index.vue` | Token 管理页面 |
| `frontend/src/views/stock/mapping/index.vue` | 映射配置列表 |
| `frontend/src/views/stock/mapping/edit.vue` | 映射配置编辑 |
| `frontend/src/views/stock/components/KlineChart.vue` | K线图组件 |
| `frontend/src/api/stock.js` | 股票模块 API |

---

## 七、验证方案

### 7.1 后端测试
1. 启动后端服务
2. 添加 iTick API Token（`POST /api/stock/token`）
3. 配置数据映射（`POST /api/stock/mapping`）
4. 同步股票列表（`POST /api/stock/sync/stock-list`）
5. 添加自选股票（`POST /api/stock/watchlist`）
6. 同步 K 线数据（`POST /api/stock/sync/kline`）
7. 查询 K 线数据验证（`GET /api/stock/{code}/kline`）

### 7.2 前端测试
1. 启动前端服务
2. 访问 Token 管理页面，添加 Token
3. 访问映射配置页面，配置字段映射
4. 访问股票列表页面，查看股票数据
5. 添加自选股票
6. 访问股票详情页面，查看 K 线图显示

---

## 八、已确认事项

| 事项 | 决定 |
|------|------|
| 数据来源 | iTick API |
| 市场范围 | 港股、A股 |
| 历史数据范围 | 首次同步拉取近1个月的历史K线数据 |
| K线同步范围 | 仅同步自选股票池中的股票 |
| 表名前缀 | `stk_` 前缀标识股票模块 |

## 九、待提供信息

1. **iTick API Token**：需要用户提供 API Token 用于配置
2. **iTick API 文档**：需要确认具体的接口路径和参数格式
