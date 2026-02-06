# 股票推荐打分系统 - 部署指南

## 📋 部署前准备

### 1. 环境要求
- ✅ JDK 1.8+
- ✅ MySQL 5.7+
- ✅ Node.js 14+
- ✅ Maven 3.6+

### 2. 数据准备
- ✅ 确保股票基础数据已导入（`stk_stock_info` 表）
- ✅ 确保股票K线数据已同步（`stk_kline_daily` 表，至少60条）
- ✅ 建议先同步近3个月的K线数据

---

## 🚀 部署步骤

### 步骤1：初始化数据库表

**执行顺序**：必须按以下顺序执行

#### 1.1 创建数据库表和初始化规则
```bash
# 在MySQL中执行
mysql -u root -p your_database < backend/src/main/resources/db/recommend_schema.sql
```

**此脚本会创建**：
- `stk_score_rule` - 打分规则配置表
- `stk_score_record` - 打分记录表
- `stk_recommend` - 推荐股票表
- 初始化5条经典技术分析规则

#### 1.2 配置菜单和权限
```bash
# 在MySQL中执行
mysql -u root -p your_database < backend/src/main/resources/db/recommend_permission.sql
```

**此脚本会创建**：
- 股票推荐菜单（`/stock/recommend`）
- 规则配置菜单（`/stock/recommend/rule`）
- 5个按钮权限
- 为超级管理员分配所有权限

**验证SQL执行结果**：
```sql
-- 查看新增的菜单
SELECT id, permission_name, permission_code, permission_type, path
FROM sys_permission
WHERE (permission_code LIKE 'stock:recommend%' OR permission_code LIKE 'stock:rule%')
  AND deleted = 0
ORDER BY sort;

-- 应该看到7条记录：
-- 1. 股票推荐（菜单）
-- 2. 规则配置（菜单）
-- 3-7. 5个按钮权限
```

---

### 步骤2：启动后端服务

#### 2.1 编译项目
```bash
cd backend
mvn clean install -DskipTests
```

#### 2.2 启动服务
```bash
mvn spring-boot:run
```

**或者使用jar包启动**：
```bash
java -jar target/base-system-1.0.0.jar
```

#### 2.3 验证启动成功
查看日志，确认以下内容：
- ✅ `========== 系统启动成功 ==========`
- ✅ `@EnableScheduling` 注解已生效
- ✅ 定时任务已注册：`DailyScoreTask.executeDailyScore`
- ✅ 5个策略Bean已注册：`MaAlignmentStrategy`, `VolumeBreakStrategy`, 等

**测试API**：
```bash
# 测试查询规则列表
curl http://localhost:8080/stock/recommend/rule/list \
  -H "Authorization: Bearer YOUR_TOKEN"

# 应该返回5条规则配置
```

---

### 步骤3：启动前端服务

#### 3.1 安装依赖（首次）
```bash
cd frontend
npm install
```

#### 3.2 启动开发服务器
```bash
npm run dev
```

#### 3.3 访问系统
- 前端地址：http://localhost:3000
- 登录系统（使用admin账号）
- 在左侧菜单找到"股票管理" → "股票推荐"

---

### 步骤4：功能验证

#### 4.1 验证规则配置
1. 访问：http://localhost:3000/stock/recommend/rule
2. 应该看到5条规则：
   - ✅ 均线多头排列（20分，权重1.5）
   - ✅ 成交量突破（10-20分，权重1.2）
   - ✅ 连续上涨（10-15分，权重1.0）
   - ✅ MACD金叉（15分，权重1.3）
   - ✅ 突破前高（10-20分，权重1.1）
3. 所有规则状态应为"启用"

#### 4.2 手动触发打分测试
1. 访问：http://localhost:3000/stock/recommend
2. 选择日期（建议选择最近的交易日）
3. 点击"手动打分"按钮
4. 等待打分完成（查看后端日志）

**后端日志示例**：
```
2026-02-06 14:30:00 INFO  - ========== 开始执行每日打分任务 ==========
2026-02-06 14:30:00 INFO  - 开始对所有股票执行打分，日期：2026-02-06
2026-02-06 14:30:00 INFO  - 共有 1000 只股票需要打分
2026-02-06 14:30:01 INFO  - 开始对股票 00001 执行打分，日期：2026-02-06
2026-02-06 14:30:01 DEBUG - 规则 MA_ALIGNMENT 执行完成，命中：true，得分：20.0
2026-02-06 14:30:01 DEBUG - 规则 VOLUME_BREAK 执行完成，命中：false，得分：0.0
...
2026-02-06 14:35:00 INFO  - 所有股票打分完成，成功：998，失败：2
2026-02-06 14:35:00 INFO  - 开始计算排名，日期：2026-02-06
2026-02-06 14:35:01 INFO  - 排名计算完成
2026-02-06 14:35:01 INFO  - ========== 每日打分任务执行完成 ==========
```

#### 4.3 查看推荐列表
1. 刷新推荐列表页面
2. 应该看到按总分降序排列的股票列表
3. 前3名显示红色排名标签
4. 点击"打分明细"查看各规则得分

#### 4.4 查看打分明细
1. 点击任意股票的"打分明细"按钮
2. 应该看到5条规则的详细得分：
   - 规则编码、规则名称
   - 命中状态（命中/未分、加权得分
   - 计算详情（JSON格式）

---

## 🔧 配置说明

### 定时任务配置

**默认配置**：每天16:30执行（港股收盘后）

**修改定时任务时间**：
编辑 `DailyScoreTask.java`：
```java
@Scheduled(cron = "0 30 16 * * ?")  // 修改这里
public void executeDailyScore() {
    // ...
}
```

**Cron表达式说明**：
- `0 30 16 * * ?` - 每天16:30
- `0 0 9 * * ?` - 每天9:00
- `0 0 0 * * ?` - 每天0:00

### 规则参数配置

**通过前端页面配置**：
1. 访问规则配置页面
2. 点击"编辑"按钮
3. 修改"规则参数"字段（JSON格式）

**示例**：
```json
{
  "ma5": 5,
  "ma10": 10,
  "ma20": 20,
  "ma60": 60
}
```

**通过数据库直接修改**：
```sql
UPDATE stk_score_rule
SET config_jso= '{"period":30,"threshold":2.0}'
WHERE rule_code = 'VOLUME_BREAK';
```

---

## 📊 性能优化建议

### 1. 数据库索引优化
```sql
-- 确保以下索引存在
SHOW INDEX FROM stk_score_rule;
SHOW INDEX FROM stk_score_record;
SHOW INDEX FROM stk_recommend;

-- 如果缺少索引，手动创建
CREATE INDEX idx_status_sort ON stk_score_rule(status, sort_order);
CREATE INDEX idx_stock_date ON stk_score_record(stock_code, score_date);
CREATE INDEX idx_recommend_date_score ON stk_recommend(recommend_date, total_score DESC);
```

### 2. 打分性能优化

**批量查询优化**：
- 使用 `LIMIT 100` 限制K线数据量
- 只查询必要的字段

**缓存优化**（可选）：
```java
// 在 ScoreServiceImpl 中添加缓存
@Cacheable(value = "klineData", key = "#stockCode + '_' + #scoreDate")
public List<StockKline> getKlineData(String stockCode, LocalDate scoreDate) {
    // ...
}
```

### 3. 定时任务优化

**只对活跃股票打分**：
```java
// 修改 executeAllStockScore 方法
List<StockInfo> stocks = stockInfoMapper.selectList(
    new LambdaQueryWrapper<StockInfo>()
        .eq(StockInfo::getStatus, 1)
        .eq(StockInfo::getDeleted, 0)
        .gt(StockInfo::getVolume, 0)  // 添加成交量过滤
);
```

---

## 🐛 常见问题排查

### 问题1：定时任务不执行

**原因**：未添加 `@EnableScheduling` 注解

**解决**：
检查 `BaseSystemApplication.java`：
```java
@SpringBootApplication
@EnableSc  // 确保有这个注解
@ComponentScan(basePackages = {"com.base"})
public class BaseSystemApplication {
    // ...
}
```

### 问题2：策略Bean未注入

**原因**：策略类未标注 `@Component` 注解

**解决**：
检查策略实现类：
```java
@Slf4j
@Component  // 确保有这个注解
public class MaAlignmentStrategy implements ScoreStrategy {
    // ...
}
```

### 问题3：打分失败 - K线数据不足

**原因**：股票K线数据少于60条

**解决**：
1. 同步更多历史K线数据
2. 或者修改规则，降低数据量要求

### 问题4：菜单不显示

**原因**：权限未分配或前端路由未配置

**解决**：
1. 检查数据库权限分配：
```sql
SELECT * FROM sys_role_permission
WHERE role_id = 1
  AND permission_id IN (
    SELECT id FROM sys_pion
    WHERE permission_code LIKE 'stock:recommend%'
  );
```

2. 清除浏览器缓存，重新登录

### 问题5：打分明细显示为空

**原因**：打分记录未保存或查询失败

**解决**：
1. 检查后端日志是否有异常
2. 检查数据库 `stk_score_record` 表是否有数据：
```sql
SELECT * FROM stk_score_record
WHERE stock_code = '00001'
  AND score_date = '2026-02-06'
LIMIT 10;
```

---

## 📝 数据维护

### 定期清理历史数据

**建议保留近3个月的数据**：

```sql
-- 清理3个月前的打分记录
DELETE FROM stk_score_record
WHERE score_date < DATE_SUB(CURDATE(), INTERVAL 3 MONTH);

-- 清理3个月前的推荐记录
DELETE FROM stk_recommend
WHERE recommend_date < DATE_SUB(CURDATE(), INTERVAL 3 MONTH);
```

**创建定时清理任务**（可选）：
```java
@Scheduled(cron = "0 0 2 1 * ?")  // 每月1号凌晨2点执行
public void cleanOldData() {
    LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
    // 清理逻辑...
}
```

### 监控打分任务

**查看最近的打分记录**：
```sql
SELECT recommend_date, COUNT(*) as stock_count, AVG(total_score) as avg_score
FROM stk_recommend
WHERE recommend_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
GROUP BY recommend_date
ORDER BY recommend_date DESC;
```

**查看规则命中率**：
```sql
SELECT rule_code,
       SUM(hit_flag) as hit_count,
       COUNT(*) as total_count,
       ROUND(SUM(hit_flag) / COUNT(*) * 100, 2) as hit_rate
FROM stk_scorERE score_date = CURDATE()
GROUP BY rule_code;
```

---

## ✅ 部署检查清单

部署完成后，请逐项检查：

- [ ] 数据库表已创建（3张表）
- [ ] 规则配置已初始化（5条规则）
- [ ] 菜单和权限已配置（2个菜单，5个按钮权限）
- [ ] 后端服务启动成功
- [ ] 前端服务启动成功
- [ ] 定时任务已注册
- [ ] 策略Bean已注入（5个）
- [ ] 手动打分测试通过
- [ ] 推荐列表显示正常
- [ ] 打分明细显示正常
- [ ] 规则配置功能正常
- [ ] 启用/禁用规则功能正常

---

## 🎉 部署完成

恭喜！股票推荐打分系统已成功部署。

**下一步**：
1. 观察定时任务执行情况
2. 根据实际效果调整规则权重
3. 监控系统性能
4. 收集用户反馈

**技术支持**：
- 需求文档：`.claude/plans/stock-recommend-requirement.md`
- 开发总结：`.claude/plans/stock-recommend-summary.md`
- 业务记录：`.claude/docs/TEMP.md`

祝使用愉快！🚀
