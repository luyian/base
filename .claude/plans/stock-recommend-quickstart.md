# 股票推荐打分系统 - 快速部署指南

## 📋 修正说明

**问题**：权限初始化SQL使用了错误的字段名
- ❌ 错误：`permission_type` 字段（不存在）
- ✅ 正确：`type` 字段（tinyint类型）

**类型值说明**：
- `type = 1`：目录
- `type = 2`：菜单
- `type = 3`：按钮

---

## 🚀 快速部署（3步）

### 步骤1：初始化数据库

```bash
# 1. 创建表和初始化规则（3张表 + 5条规则）
mysql -u root -p your_database < backend/src/main/resources/db/recommend_schema.sql

# 2. 配置菜单和权限（2个菜单 + 5个按钮权限）
mysql -u root -p your_database < backend/src/main/resources/db/recommend_permission.sql
```

**验证SQL执行结果**：
```sql
-- 查看新增的菜单和按钮
SELECT id, permission_name, permission_code, type, path, sort
FROM sys_permission
WHERE (permission_code LIKE 'stock:recommend%' OR permission_code LIKE 'stock:rule%')
  AND deleted = 0
ORDER BY sort;

-- 应该看到7条记录：
-- 1. 股票推荐（菜单，type=2）
-- 2. 规则配置（菜单，type=2）
-- 3-7. 5个按钮权限（type=3）
```

### 步骤2：启动服务

```bash
# 后端（已编译成功）
cd backend
mvn spring-boot:run

# 前端（已编译成功）
cd frontend
npm run dev
```

### 步骤3：访问系统

- 登录系统：http://localhost:3000
- 推荐列表：http://localhost:3000/stock/recommend
- 规则配置：http://localhost:3000/stock/recommend/rule

---

## ✅ 编译状态

### 后端 ✅
```
[INFO] BUILD SUCCESS
[INFO] Total time: 14.537 s
编译文件：287个源文件
```

### 前端 ✅
```
✓ built in 24.10s
生成文件：80+ 个文件
```

**已修复的问题**：
- ✅ `index.vue` 第118行语法错误（缺少 `<` 符号）

---

## 📊 菜单结构

```
股票管理 (stock) - type=1 目录
  ├── 股票列表 (stock:info) - type=2 菜单
  ├── 自选股票 (stock:watchlist) - type=2 菜单
  ├── 股票推荐 (stock:recommend) - type=2 菜单 ← 新增
  │   ├── 查询推荐列表 (stock:recommend:list) - type=3 按钮
  │   ├── 查询打分明细 (stock:recommend:detail) - type=3 按钮
  │   └── 手动触发打分 (stock:recommend:execute) - type=3 按钮
  └── 规则配置 (stock:recommend:rule) - type=2 菜单 ← 新增
      ├── 查询规则列表 (stock:rule:list) - type=3 按钮
      └── 编辑规则配置 (stock:rule:edit) - type=3 按钮
```

---

## 🧪 功能测试

### 测试1：验证菜单显示

1. 登录系统（admin/admin123）
2. 查看左侧菜单
3. 应该看到"股票管理"下新增了：
   - ✅ 股票推荐
   - ✅ 规则配置

### 测试2：查看规则配置

1. 访问：http://localhost:3000/stock/recommend/rule
2. 应该看到5条规则：
   - ✅ 均线多头排列（20分，权重1.5）
   - ✅ 成交量突破（10-20分，权重1.2）
   - ✅ 连续上涨（10-15分，权重1.0）
   - ✅ MACD金叉（15分，权重1.3）
   - ✅ 突破前高（10-20分，权重1.1）

### 测试3：手动触发打分

1. 访问：http://localhost:3000/stock/recommend
2. 选择日期（建议选择最近的交易日）
3. 点击"手动打分"按钮
4. 等待打分完成（查看后端日志）

**后端日志示例**：
```
2026-02-06 14:30:00 INFO  - ========== 开始执行每日打分任务 ==========
2026-02-06 14:30:00 INFO  - 开始对所有股票执行打分，日期：2026-02-06
2026-02-06 14:30:00 INFO  - 共有 1000 只股票需要打分
...
2026-02-06 14:35:00 INFO  - 所有股票打分完成，成功：998，失败：2
2026-02-06 14:35:01 INFO  - ========== 每日打分任务执行完成 ==========
```

### 测试4：查看推荐列表

1. 刷新推荐列表页面
2. 应该看到按总分降序排列的股票列表
3. 前3名显示红色排名标签
4. 点击"打分明细"查看各规则得分

---

## 📝 注意事项

### 1. 数据准备
- ⚠️ 确保股票K线数据已同步（至少60条）
- ⚠️ 新上市股票可能因数据不足无法打分

### 2. 定时任务
- ⏰ 默认每天16:30自动执行打分
- 📅 周末和节假日没有交易数据

### 3. 权限配置
- 👤 超级管理员（role_id=1）已自动分配所有权限
- 👥 其他角色需要手动分配权限

---

## 🔧 常见问题

### Q1：菜单不显示？
**解决**：
1. 检查权限是否分配成功
```sql
SELECT * FROM sys_role_permission
WHERE role_id = 1
  AND permission_id IN (
    SELECT id FROM sys_permission
    WHERE permission_code LIKE 'stock:recommend%'
  );
```
2. 清除浏览器缓存，重新登录

### Q2：打分失败 - K线数据不足？
**解决**：
1. 同步更多历史K线数据
```bash
# 在股票列表页面点击"拉取全部"按钮
# 选择市场和日期范围，同步K线数据
```

### Q3：定时任务不执行？
**解决**：
1. 检查启动类是否有 `@EnableScheduling` 注解
```java
@SpringBootApplication
@EnableScheduling  // 确保有这个注解
@ComponentScan(basePackages = {"com.base"})
public class BaseSystemApplication {
    // ...
}
```

---

## 📚 相关文档

- 📖 需求文档：`.claude/plans/stock-recommend-requirement.md`
- 📖 开发总结：`.claude/plans/stock-recommend-summary.md`
- 📖 部署指南：`.claude/plans/stock-recommend-deployment.md`

---

## 🎉 部署完成

系统已准备就绪，可以开始使用！

**下一步**：
1. ✅ 执行数据库初始化SQL
2. ✅ 启动后端服务
3. ✅ 启动前端服务
4. ✅ 访问系统并测试功能

祝使用愉快！🚀
