# 业务逻辑变更记录

> 按功能模块归类，记录核心业务变更和架构决策。详细文件变更请查阅 git log。

---

## 基础架构（2026-01-12）

- Spring Boot 2.7.18 + MyBatis Plus + Spring Security + JWT
- Vue 3 + Vite + Element Plus + Pinia
- 统一响应 `Result<T>`、全局异常处理、XSS/SQL注入防护
- RBAC 权限模型：用户 → 角色 → 权限（菜单/按钮）
- 数据权限：`@DataScope` 注解 + MyBatis 拦截器
- 动态路由：后端菜单权限 → 前端 Vue Router 路由生成
- 操作日志：`@OperationLog` 注解自动记录
- 核心模块：用户、角色、权限/菜单、部门、枚举、全局变量、日志管理

---

## 系统功能完善

### 登录与认证
- 登录日志记录（IP、浏览器、操作系统）
- 验证码开关：`captcha.enabled` 配置项控制，开发模式可跳过
- GitHub OAuth 第三方登录：授权 → 回调 → 绑定/创建账号，`sys_user_oauth` 表存储绑定关系，state 防 CSRF（Redis 3分钟过期）

### 个人中心
- 头像上传、基本信息编辑、修改密码
- 第三方账号管理（GitHub 绑定/解绑、飞书绑定/解绑）
- 飞书绑定改为 OAuth 授权流程（原手动输入 open_id 方式废弃）：点击绑定 → 跳转飞书授权页 → 回调自动获取 open_id 和用户名 → 绑定到当前账号

### 通知公告
- 通知管理（CRUD、发布）、标记已读、未读数量、`NoticeDropdown` 组件

### 系统监控
- 服务器信息（CPU/内存/磁盘/JVM）、Redis 缓存信息

### 行政区划管理
- 四级树形结构、懒加载级联选择器 `RegionCascader`
- 省市区三级数据 3424 条（GitHub Administrative-divisions-of-China）

### 按钮级权限控制
- `v-permission` 指令 + `hasPermission()` 工具函数

### 枚举管理重构
- 按类型分组显示，支持批量保存枚举项
- 新增 `typeDesc` 中文描述字段
- API：`GET /system/enum/types`、`POST /system/enum/type/{enumType}/batch`、`DELETE /system/enum/type/{enumType}`

### 通用导出功能
- EasyExcel 3.3.2 + 线程池异步 + Redis 进度缓存
- 支持：服务方法/自定义SQL数据源、数据脱敏、字典转换、多Sheet、自动过期清理
- 扩展：实现 `ExportDataProvider`/`DataConverter`/`DataMasker` 接口
- 已集成：登录日志导出

### 分页查询统一改造
- 创建 `BasePageRequest` 基类（current/size，Long 类型，`buildPage()` 方法）
- 14 个接口统一 GET → POST + @RequestBody
- stock 模块新建 4 个 QueryRequest 类

---

## 飞书消息集成

- 策略模式：`FeishuMessageHandler` 接口 + Text/Image/File 三种实现
- Token 管理：Redis 缓存 tenant_access_token，提前 5 分钟刷新
- 飞书绑定：复用 `sys_user_oauth` 表，oauthType="feishu"
- 配置：`application-dev.yml` 中 `feishu.*` 配置段
- 扩展：新增消息类型只需添加枚举值 + Handler 实现类

---

## 消息中心

三层解耦架构：`ContentBuilder`（内容生成）→ `ChannelSender`（渠道发送）→ `MessagePushService`（协调调度）

- 订阅表 `msg_subscription`，按 userId + subType + channel 管理
- 定时任务：每天 14:30 推送基金估值到飞书
- API：`GET /message/subscription/list`、`PUT /message/subscription/toggle`、`POST /message/subscription/push/{subType}`
- 扩展渠道：实现 `ChannelSender` + `@Component`
- 扩展订阅类型：实现 `ContentBuilder` + `@Component`，`MessagePushTask` 添加 `@Scheduled` 方法

---

## 股票数据分析

### 核心架构
- iTick API 客户端：Token 轮询机制，失败三次自动作废
- 数据同步：支持单只/批量/全量同步，批量接口阈值可配置（`sys_config` 表）
- 简繁体互查：OpenCC4j

### 多线程同步优化
- `ConcurrentHttpExecutor`：共享 Token 池，线程数 = Token数/6（最小1，最大10）
- 批量 upsert：`INSERT ... ON DUPLICATE KEY UPDATE`，每批 500 条
- 失败记录表 `stk_sync_failure`，支持补拉重试（最大 3 次）

### 股票详情扩展
- `stk_stock_info` 新增：行业、市值、总股本、市盈率、52周高低价等字段
- 行业中英文映射：通过枚举管理 `stock_industry` 配置，后端填充 `industryCn`

### K线功能
- 日K线 + 分钟K线（1分钟/5分钟），合并到趋势弹窗 `TrendDialog.vue`
- tooltip 显示成交量和涨跌幅，横轴智能日期显示
- 日期范围动态计算 limit，支持左滑加载更多

### 自选股
- `stk_watchlist` 表（stock_code + user_id 联合唯一）
- 列表显示：行业、总市值、总股本、市盈率

---

## 股票推荐打分系统

- 策略模式 + Spring Bean 自动发现，定时任务每天 16:30 执行
- 数据库：`stk_score_rule`（规则配置）、`stk_score_record`（打分记录）、`stk_recommend`（推荐汇总）
- 5 条规则：均线多头排列、成交量突破、连续上涨、MACD金叉、突破前高
- 排序直接按 totalScore 降序，前端用序号代替排名字段
- 扩展：数据库插入规则 + 实现 `ScoreStrategy` 接口 + `@Component`
- API：`/stock/recommend/list|detail|execute|latest-date`、`/stock/recommend/rule/list|{id}|{id}/enable|{id}/disable`

---

## 基金估值

- 基金为公共资源，管理员管理配置，用户通过 `stk_fund_watchlist` 自选
- 实时估值：iTick 批量报价接口（每次最多 3 个），CompletableFuture 并发请求
- 加权涨跌幅：Σ(股票涨跌幅 × 权重) / 100
- Redis 缓存估值（key: `fund:valuation:{fundId}`，1 小时过期），列表页显示缓存数据，详情页实时获取
- **估值持久化**：15:30 后获取的估值写入 `stk_fund_valuation_record` 表（fund_id + trade_date 唯一），列表页优先读取当日数据库记录
- 新增文件：`FundValuationRecord.java`、`FundValuationRecordMapper.java`、`db/fund_valuation_record.sql`
- 前端编辑基金时，持仓股票名称回显：`handleEdit` 同时保存 `stockName`，并预填充 `stockOptions`
- 关闭详情弹窗后自动刷新基金列表（`@close="fetchFundList"`）
- API：CRUD `/stock/fund/*`、估值 `/stock/fund/{id}/valuation`、自选 `/stock/fund/watchlist/*`

---

## 用户管理

### 分配角色修复（2026-02-27）
- 修复分配角色弹窗显示空白的问题：`User.vue` 的 `handleAssignRole` 方法原先未调用角色列表接口，直接赋空数组
- 导入 `listAllRoles` 并在打开弹窗前调用，填充 `allRoles` 数据

### 按钮权限控制补全（2026-02-27）
- 为所有缺少 `v-permission` 指令的页面按钮补上权限控制，没有权限的按钮不渲染
- 涉及页面：Enum、LoginLog、Config、ExportConfig、Region、Permission、OperationLog、Department、Notice、stock/index、token、mapping、watchlist、recommend/rule
- 权限码与后端 `@PreAuthorize` 注解保持一致

### 菜单按权限渲染修复（2026-02-27）
- 修复 `PermissionServiceImpl.getCurrentUserMenuTree()` 未按用户权限过滤菜单的问题
- 原实现直接查询所有菜单返回，改为通过 `selectPermissionsByUserId` 查询当前用户拥有的权限，再过滤出目录和菜单
- 新增注入 `SysUserMapper`，用于根据用户名查询用户 ID

---

## Docker 部署

- 构建脚本 `remote-git-build.sh`：宿主机上 git clone docker 分支 → npm build → mvn package → docker build
- Dockerfile 仅打包运行环境（JRE + Nginx + Redis），不含构建步骤（Docker 环境无外网）
- Nginx 托管静态文件 + 反向代理 `/api` → localhost:8080
- 环境变量注入：DB_HOST/PORT/NAME/USER/PASSWORD、REDIS_HOST/PORT/PASSWORD
- 独立 `docker` profile，不影响开发环境
