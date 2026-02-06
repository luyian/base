# 股票推荐打分系统 - 需求文档

## 一、项目概述

### 1.1 项目背景
基于股票日K线数据的智能推荐系统，通过多维度技术分析规则对股票进行自动打分，帮助用户筛选出具有投资价值的股票。

### 1.2 核心目标
- **自动化打分**：每天定时对所有股票执行打分，无需人工干预
- **规则可配置**：打分规则、权重、启用状态均可通过后台配置
- **架构解耦**：新增规则只需实现策略接口，无需修改核心代码
- **结果可视化**：前端页面按分数排序展示推荐股票，支持查看打分明细
- **未来扩展**：预留实时打分接口，支持基于分钟K线的实时分析

### 1.3 技术选型
- **后端框架**：Spring Boot 2.7.18 + MyBatis Plus
- **设计模式**：策略模式 + Spring Bean自动发现
- **定时任务**：Spring @Scheduled
- **数据存储**：MySQL（3张新表）
- **前端框架**：Vue 3 + Element Plus

---

## 二、功能需求

### 2.1 核心功能

#### 2.1.1 自动打分
- 每天16:30自动执行打分任务（港股收盘后）
- 对所有正常状态的股票执行打分
- 按规则排序号顺序执行所有启用的规则
- 记录每只股票每条规则的详细得分
- 汇总总分并计算排名

#### 2.1.2 推荐列表
- 按日期查询推荐股票列表
- 按总分降序排列，显示排名
- 显示股票代码、名称、市场、总分、命中率
- 支持查看打分明细（每条规则的得分和计算过程）
- 支持跳转到K线图查看走势

#### 2.1.3 规则配置
- 查看所有打分规则列表
- 编辑规则配置（分数、权重、参数）
- 启用/禁用规则
- 调整规则执行顺序

#### 2.1.4 手动打分
- 支持手动触发打分任务
- 可对单只股票或所有股票打分
- 指定打分日期

### 2.2 业内经典打分规则

#### 规则1：均线多头排列 (MA_ALIGNMENT)
- **判断逻辑**：MA5 > MA10 > MA20 > MA60
- **分数**：固定20分
- **权重**：1.5
- **参数**：`{"ma5":5,"ma10":10,"ma20":20,"ma60":60}`
- **说明**：短期均线在长期均线上方，形成多头排列，表明趋势向好

#### 规则2：成交量突破 (VOLUME_BREAK)
- **判断逻辑**：最新成交量 > 近20日平均成交量 × 1.5倍
- **分数**：动态10-20分（突破倍数越高分数越高）
- **权重**：1.2
- **参数**：`{"period":20,"threshold":1.5}`
- **说明**：成交量突破近期平均水平，表明资金关注度提升

#### 规则3：连续上涨 (CONTINUOUS_RISE)
- **判断逻辑**：连续3-5天收盘价上涨
- **分数**：动态10-15分（连续天数越多分数越高）
- **权重**：1.0
- **参数**：`{"minDays":3,"maxDays":5}`
- **说明**：连续上涨表明短期趋势向好，动能强劲

#### 规则4：MACD金叉 (MACD_GOLDEN_CROSS)
- **判断逻辑**：DIF上穿DEA，且在近3天内发生
- **分数**：固定15分
- **权重**：1.3
- **参数**：`{"fastPeriod":12,"slowPeriod":26,"signalPeriod":9}`
- **说明**：MACD金叉是经典的趋势反转向上信号

#### 规则5：突破前高 (BREAK_HIGH)
- **判断逻辑**：突破近20日最高价
- **分数**：动态10-20分（突破幅度越大分数越高）
- **权重**：1.1
- **参数**：`{"period":20,"breakRatio":1.01}`
- **说明**：突破前高创新高，表明上涨动能强劲

---

## 三、数据库设计

### 3.1 打分规则配置表 (stk_score_rule)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID |
| rule_code | varchar(50) | 规则编码（唯一标识，对应策略Bean名称） |
| rule_name | varchar(100) | 规则名称 |
| rule_desc | varchar(500) | 规则描述 |
| category | varchar(20) | 规则分类（TECHNICAL-技术面, FUNDAMENTAL-基本面） |
| score_type | varchar(20) | 打分类型（FIXED-固定分, DYNAMIC-动态分） |
| base_score | int | 基础分数 |
| max_score | int | 最高分数（动态打分时使用） |
| weight | decimal(5,2) | 权重系数（最终得分 = 规则得分 × 权重） |
| config_json | text | 规则参数配置（JSON格式） |
| status | tinyint | 状态（0-禁用, 1-启用） |
| sort_order | int | 排序号（执行顺序） |
| remark | varchar(500) | 备注 |

**索引**：
- `uk_rule_code`：规则编码唯一索引
- `idx_status_sort`：状态+排序号索引

### 3.2 打分记录表 (stk_score_record)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID |
| stock_code | varchar(20) | 股票代码 |
| rule_code | varchar(50) | 规则编码 |
| score_date | date | 打分日期 |
| score | decimal(10,2) | 得分 |
| weighted_score | decimal(10,2) | 加权得分（得分 × 权重） |
| hit_flag | tinyint | 命中标志（0-未命中, 1-命中） |
| detail_json | text | 打分详情（JSON格式，记录计算过程） |

**索引**：
- `uk_stock_rule_date`：股票代码+规则编码+打分日期联合唯一索引
- `idx_score_date`：打分日期索引
- `idx_stock_date`：股票代码+打分日期索引

### 3.3 推荐股票表 (stk_recommend)

| 字段 |
|------|------|------|
| id | bigint | 主键ID |
| stock_code | varchar(20) | 股票代码 |
| recommend_date | date | 推荐日期 |
| total_score | decimal(10,2) | 总分 |
| hit_rule_count | int | 命中规则数 |
| total_rule_count | int | 总规则数 |
| hit_rate | decimal(5,2) | 命中率（%） |
| rank | int | 排名 |
| score_detail | text | 得分明细（JSON格式） |

**索引**：
- `uk_stock_date`：股票代码+推荐日期联合唯一索引
- `idx_recommend_date_score`：推荐日期+总分降序索引
- `idx_recommend_date_rank`：推荐日期+排名索引

---

## 四、API接口设计

### 4.1 推荐查询接口

#### 4.1.1 分页查询推荐列表
```
GET /stock/recommend/list
参数：
  - recommendDate: 推荐日期 (YYYY-MM-DD)
  - page: 页码
  - size: 每页大小
响应：
  {
    "code": 200,
    "data": {
      "records": [...],
      "total": 100
    }
  }
```

#### 4.1.2 查询打分明细
```
GET /stock/recommend/detail
参数：
  - stockCode: 股票代码
  - scoreDate: 打分日期 (YYYY-MM-DD)
响应：
  {
    "code": 200,
    "data": [...]
  }
```

#### 4.1.3 手动触发打分
```
POST /stock/recommend/execute
参数：
  - stockCode: 股票代码（可选）
  - scoreDate: 打分日期 (YYYY-MM-DD)
响应：
  {
    "code": 200,
    "message": "success"
  }
```

#### 4.1.4 查询最新推荐日期
```
GET /stock/recommend/latest-date
响应：
  {
    "code": 200,
    "data": "2026-02-06"
  }
```

### 4.2 规则配置接口

#### 4.2.1 查询规则列表
```
GET /stock/recommend/rule/list
响应：
  {
    "code": 200,
    "data": [...]
  }
```

#### 4.2.2 更新规则配置
```
PUT /stock/recommend/rule/{id}
请求体：
  {
    "ruleName": "均线多头排列",
    "baseScore": 20,
    "weight": 1.5,
    ...
  }
响应：
  {
    "code": 200,
    "message": "success"
  }
```

#### 4.2.3 启用规则
```
POST /stock/recommend/rule/{id}/enable
响应：
  {
    "code": 200,
    "message": "success"
  }
```

#### 4.2.4 禁用规则
```
POST /stock/recommend/rule/{id}/disable
响应：
  {
    "code": 200,
    "message": "success"
  }
```

---

## 五、前端页面设计

### 5.1 推荐股票列表页面

**路由**：`/stock/recommend`

**功能特性**：
- 日期选择器（默认最新交易日）
- 按总分降序展示推荐股票
- 表格列：排名、股票代码、股票名称、市场、总分、命中率、操作
- 排名前3名使用红色标签，4-10名使用橙色标签
- 支持查看打分明细（弹窗展示各规则得分）
- 支持跳转到股票详情页查看K线图
- 支持手动触发打分（管理员权限）
- 分页展示

### 5.2 规则配置管理页面

**路由**：`/stock/recommend/rule`

**功能特性**：
- 规则列表展示（规则名称、分类、分数、权重、状态、排序）
- 启用/禁用规则（开关按钮）
- 编辑规则配置（分数、权重、参数）
- 规则参数支持JSON格式配置
- 参数验证（JSON格式校验）

---

## 六、扩展性设计

### 6.1 新增规则的步骤

**示例：新增"RSI超卖"规则**

1. **数据库插入规则配置**
```sql
INSERT INTO stk_score_rule (rule_code, rule_name, rule_desc, category, score_type, base_score, max_score, weight, config_json, status, sort_order)
VALUES ('RSI_OVERSOLD', 'RSI超卖', 'RSI指标低于30，表明超卖', 'TECHNICAL', 'FIXED', 10, 10, 1.0, '{"period":14,"threshold":30}', 1, 6);
```

2. **创建策略实现类**
```java
@Slf4j
@Component
public class RsiOversoldStrategy implements ScoreStrategy {
    @Override
    public String getStrategyCode() {
        return "RSI_OVERSOLD";
    }

    @Override
    public String getStrategyName() {
        return "RSI超卖";
    }

    @Override
    public ScoreResult execute(ScoreContext context) {
        // 实现RSI计算逻辑
        // ...
    }

    @Override
    public boolean validateConfig(ScoreRule rule) {
        return true;
    }
}
```

3. **重启应用，自动生效！**

**无需修改**：
- 打分引擎（自动注入所有策略）
- 打分服务（通用执行流程）
- 前端页面（动态展示规则）

### 6.2 实时打分扩展

**未来实现方案**：
1. 新增 `RealtimeScoreService`
2. 调用 iTick API 获取分钟K线数据
3. 实现基于分钟K线的策略（如 `MinuteVolumeStrategy`）
4. 在股票详情页点击时触发实时打分
5. 结果缓存到 Redis（避免频繁调用API）

**预留接口**：
```java
public interface ScoreStrategy {
    // 现有方法
    ScoreResult execute(ScoreContext context);

    // 预留方法（未来实现）
    default boolean supportRealtime() {
        return false;  // 默认不支持实时打分
    }
}
```

---

## 七、权限配置

### 7.1 菜单配置

| 菜单名称 | 路由 | 权限标识 |
|---------|------|---------|
| 股票推荐 | /stock/recommend | stock:recommend:list |
| 规则配置 | /stock/recommend/rule | stock:rule:list |

### 7.2 按钮权限

| 功能 | 权限标识 |
|------|---------|
| 查询推荐列表 | stock:recommend:list |
| 查询打分明细 | stock:recommend:detail |
| 手动触发打分 | stock:recommend:execute |
| 查询规则列表 | stock:rule:list |
| 编辑规则配置 | stock:rule:edit |

---

## 八、注意事项

1. **数据量要求**：打分需要至少60条K线数据（计算MA60），新上市股票可能无法打分
2. **交易日判断**：周末和节假日没有交易数据，定时任务需要判断是否为交易日
3. **异常处理**：单只股票打分失败不应影响其他股票，需要充分的异常捕获
4. **日志记录**：详细记录打分过程，便于排查问题
5. **权限控制**：手动触发打分和规则配置需要管理员权限
6. **性能优化**：建议对1000只股票的打分耗时控制在5分钟以内

---

## 九、后续优化方向

1. **规则优化**：根据回测结果调整规则参数和权重
2. **性能优化**：引入缓存机制，减少重复计算
3. **实时打分**：实现基于分钟K线的实时分析
4. **回测功能**：支持历史数据回测，验证规则有效性
5. **机器学习**：引入机器学习模型，自动优化规则权重
6. **消息推送**：高分股票推送到用户（邮件/微信）
