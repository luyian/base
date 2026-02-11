# 业务逻辑变更记录

---

### 2026-02-11

#### Docker 部署配置

- **需求**：添加 Dockerfile 实现前后端合并部署，一键构建镜像
- **方案**：多阶段构建（multi-stage build），Nginx 托管前端静态文件 + 反向代理后端 API
- **新增文件**：
  - `Dockerfile` - 三阶段构建（Node 构建前端 → Maven 构建后端 → JRE + Nginx 运行）
  - `.dockerignore` - 排除 node_modules、target、.git 等无关文件
  - `nginx.conf` - Nginx 配置（静态文件 + /api 反向代理到 localhost:8080）
  - `docker-entrypoint.sh` - 启动脚本（同时启动 Nginx 和 Java）
  - `backend/src/main/resources/application-docker.yml` - Docker 环境配置（MySQL/Redis 地址通过环境变量注入）
- **运行方式**：
  - 构建：`docker build -t base-system .`
  - 运行：`docker run -d -p 80:80 -e DB_HOST=xxx -e REDIS_HOST=xxx base-system`
- **环境变量**：DB_HOST、DB_PORT、DB_NAME、DB_USER、DB_PASSWORD、REDIS_HOST、REDIS_PORT、REDIS_PASSWORD
- **关联影响**：不影响现有开发环境，Docker 使用独立的 `docker` profile

#### 菜单管理列表查询按钮权限

- **需求**：菜单管理列表也查询按钮类型（type=3），但默认不展开
- **后端修改**：`PermissionServiceImpl.java` - 去掉 `treePermissions` 方法中默认过滤按钮的逻辑（原来 type 为空时只查目录和菜单）
- **前端修改**：`Permission.vue` - 去掉 `default-expand-all`，表格默认收起
- **修改文件**：
  - `backend/src/main/java/com/base/system/service/impl/PermissionServiceImpl.java`
  - `frontend/src/views/system/Permission.vue`

#### 分配权限叶子节点横向排列

- **需求**：角色管理"分配权限"对话框中，叶子节点（如用户查询、用户新增等）改为一行显示四个
- **实现**：在 `Role.vue` 中添加全局 CSS 样式，对 el-tree 二级子节点容器使用 flex 布局，每个叶子节点宽度 25%
- **修改文件**：`frontend/src/views/system/Role.vue` - 新增 `<style>` 块（非 scoped）

#### GitHub OAuth 第三方登录功能

- **需求**：新增 GitHub OAuth 第三方登录，首次登录支持"创建新账号"或"绑定已有账号"
- **OAuth 流程**：前端跳转 GitHub 授权 → 回调携带 code+state → 后端换 token 获取用户信息 → 已绑定直接登录/未绑定跳转绑定页
- **数据库**：
  - 新建 `sys_user_oauth` 表（第三方登录绑定表）
  - SQL 文件：`backend/src/main/resources/db/oauth_schema.sql`
- **后端新增文件**：
  - `config/OauthConfig.java` - OAuth 配置属性类
  - `system/entity/UserOauth.java` - 第三方绑定实体
  - `system/mapper/UserOauthMapper.java` - Mapper 接口
  - `system/dto/oauth/OauthCallbackRequest.java` - 回调请求 DTO
  - `system/dto/oauth/OauthCallbackResponse.java` - 回调响应 DTO
  - `system/dto/oauth/OauthBindNewRequest.java` - 创建新账号绑定请求
  - `system/dto/oauth/OauthBindExistRequest.java` - 绑定已有账号请求
  - `system/dto/oauth/GithubUserInfo.java` - GitHub 用户信息 DTO
  - `system/service/OauthService.java` - OAuth 服务接口
  - `system/service/impl/OauthServiceImpl.java` - OAuth 服务实现
  - `system/controller/OauthController.java` - OAuth 控制器（/auth/oauth，无需认证）
  - `system/controller/UserOauthController.java` - 第三方账号管理控制器（/system/oauth，需认证）
- **后端修改文件**：
  - `application-dev.yml` - 新增 oauth.github 配置项
- **前端新增文件**：
  - `api/oauth.js` - OAuth API 封装
  - `views/OauthCallback.vue` - OAuth 回调中转页
  - `views/OauthBind.vue` - 首次登录绑定选择页
- **前端修改文件**：
  - `views/Login.vue` - 添加 GitHub 登录按钮和第三方登录区域
  - `router/index.js` - 添加 /oauth/callback 和 /oauth/bind 路由，路由守卫放行
  - `views/profile/Index.vue` - 个人中心添加"第三方账号"标签页（绑定/解绑管理）
- **安全设计**：state 防 CSRF（Redis 3分钟过期）、oauthToken 临时凭证（5分钟过期）、client-secret 仅后端存储
- **关联影响**：SecurityConfig 中 /auth/** 已放行，OauthController 路径自动被覆盖；UserOauthMapper 在 com.base.system.mapper 包下，MybatisPlusConfig 无需修改

#### 登录验证码开关功能

- **需求**：开发模式下可配置跳过验证码校验
- **实现方案**：通过 `captcha.enabled` 配置项控制验证码是否启用
- **配置方式**：`application.yml` 中设置 `captcha.enabled: false` 即可关闭验证码
- **后端修改**：
  - `CaptchaResponse.java` - 新增 `enabled` 字段，标识验证码是否启用
  - `AuthServiceImpl.java` - 新增 `captchaEnabled` 配置读取；`generateCaptcha()` 未启用时返回空数据；`login()` 未启用时跳过验证码校验
  - `application.yml` - 新增 `captcha.enabled: true` 配置项
- **前端修改**：
  - `Login.vue` - 根据接口返回的 `enabled` 字段动态显示/隐藏验证码输入框和图片；表单验证规则改为 computed 动态计算
- **关联影响**：不影响其他模块，仅影响登录流程

---

## 股票数据分析功能

> 基于需求文档 `stock-requirement.md`，已完成全部开发

### 开发进度

**状态**：✅ 全部完成

| 阶段 | 内容 | 状态 |
|------|------|------|
| 第一阶段 | 基础工具类（HttpClientUtil、JsonUtil） | ✅ 完成 |
| 第二阶段 | 数据库表 + 实体类 + Mapper | ✅ 完成 |
| 第三阶段 | Token 管理模块 | ✅ 完成 |
| 第四阶段 | 数据工厂模块 | ✅ 完成 |
| 第五阶段 | iTick API 客户端 | ✅ 完成 |
| 第六阶段 | 股票业务模块 | ✅ 完成 |
| 第七阶段 | 前端页面开发 | ✅ 完成 |
| 第八阶段 | 分钟K线查看功能 | ✅ 完成 |

### 文件清单

**后端文件**：
- `backend/src/main/java/com/base/common/util/HttpClientUtil.java` - HTTP 请求工具类
- `backend/src/main/java/com/base/common/util/JsonUtil.java` - JSON 工具类
- `backend/src/main/java/com/base/stock/` - 股票模块（entity、mapper、service、controller）
- `backend/src/main/resources/db/stock_schema.sql` - 数据库表结构

**前端文件**：
- `frontend/src/api/stock.js` - API 接口
- `frontend/src/views/stock/` - 股票相关页面

---

## 变更历史

### 2026-02-05

#### 1. 修复 el-select 选择器 placeholder 显示不全问题

- **问题**：多个页面的下拉选择器 placeholder 显示不全，宽度太窄导致文字被截断
- **修复**：给所有搜索栏中的 el-select 添加固定宽度 `style="width: 120px"`
- **修复文件**：
  - `frontend/src/views/system/ExportTask.vue` - 状态选择器
  - `frontend/src/views/system/Role.vue` - 状态选择器
  - `frontend/src/views/system/Permission.vue` - 权限类型、状态选择器
  - `frontend/src/views/system/Region.vue` - 层级、状态选择器
  - `frontend/src/views/system/ExportConfig.vue` - 数据源类型、状态选择器
  - `frontend/src/views/system/Department.vue` - 状态选择器

#### 2. 枚举管理添加中文描述字段

- **需求**：在枚举管理列表中添加"中文描述"列，用于显示枚举类型的中文说明
- **数据库**：
  - `sys_enum` 表新增 `type_desc` 字段（VARCHAR(100)）
  - 迁移脚本：`backend/src/main/resources/db/add_enum_type_desc.sql`
- **后端修改**：
  - `Enum.java` - 实体类新增 `typeDesc` 字段
  - `EnumTypeResponse.java` - DTO 新增 `typeDesc` 字段
  - `EnumTypeBatchSaveRequest.java` - 请求类新增 `typeDesc` 字段
  - `EnumService.java` - `batchSaveByType` 方法签名增加 `typeDesc` 参数
  - `EnumServiceImpl.java` - 实现类更新，支持保存和查询 `typeDesc`
  - `EnumController.java` - 接口路径改为 `/type/batch`，使用 `EnumTypeBatchSaveRequest`
- **前端修改**：
  - `frontend/src/api/enum.js` - `batchSaveByType` 方法增加 `typeDesc` 参数
  - `frontend/src/views/system/Enum.vue` - 列表添加中文描述列，表单添加中文描述输入框

#### 3. 修复个人中心角色不显示问题

- **问题**：个人中心页面角色显示为空
- **原因**：后端返回的 `roles` 是角色ID的字符串（如 "1,2"），但前端期望的是角色对象数组（包含 `id` 和 `roleName`）
- **修复**：
  - `UserProfileResponse.java` - 将 `roles` 字段类型从 `String` 改为 `List<RoleInfo>`，新增 `RoleInfo` 内部类
  - `UserProfileServiceImpl.java` - 使用 `RoleMapper.selectRolesByUserId` 查询角色信息，返回角色对象列表

---

### 2026-02-04

#### 1. 枚举管理页面重构

- **需求**：将枚举管理页面从"逐条显示"改为"按类型分组显示"
- **改动内容**：
  - 主列表按枚举类型分组，同类型只显示一条
  - 去掉备注、枚举标签列，改为显示：枚举类型、枚举项数量、创建时间
  - 点击编辑进入详情弹窗，可编辑该类型下所有枚举项
  - 支持批量保存枚举项（新增、修改、删除）
- **后端新增**：
  - `EnumTypeResponse.java` - 枚举类型响应 DTO
  - `EnumItemSaveRequest.java` - 枚举项保存请求 DTO
  - `EnumTypeBatchSaveRequest.java` - 批量保存请求 DTO
  - `EnumService` 新增方法：`listEnumTypes()`、`batchSaveByType()`、`deleteByType()`
  - `EnumController` 新增接口：
    - `GET /system/enum/types` - 查询枚举类型列表
    - `POST /system/enum/type/{enumType}/batch` - 批量保存枚举项
    - `DELETE /system/enum/type/{enumType}` - 按类型删除
- **前端改动**：
  - `enum.js` 新增 API：`listEnumTypes()`、`batchSaveByType()`、`deleteByType()`
  - `Enum.vue` 重构：主列表改为类型列表，编辑弹窗改为表格形式编辑枚举项
- **文件清单**：
  - `backend/src/main/java/com/base/system/dto/enums/EnumTypeResponse.java`
  - `backend/src/main/java/com/base/system/dto/enums/EnumItemSaveRequest.java`
  - `backend/src/main/java/com/base/system/dto/enums/EnumTypeBatchSaveRequest.java`
  - `backend/src/main/java/com/base/system/service/EnumService.java`
  - `backend/src/main/java/com/base/system/service/impl/EnumServiceImpl.java`
  - `backend/src/main/java/com/base/system/controller/EnumController.java`
  - `frontend/src/api/enum.js`
  - `frontend/src/views/system/Enum.vue`

#### 2. 通用导出功能模块

- **需求**：实现按配置导出、百万数据导出、异步导出 + 分批查询 + 流式写入
- **技术栈**：EasyExcel 3.3.2、线程池异步执行、Redis 进度缓存
- **功能特性**：
  - 可视化配置导出字段和数据源
  - 支持服务方法和自定义 SQL 两种数据源
  - 数据脱敏（手机号、身份证、邮箱、银行卡、姓名）
  - 字典转换（关联 sys_enum 表）
  - 自定义转换器扩展
  - 多 Sheet 导出支持
  - 异步任务进度实时更新
  - 文件自动过期清理

**数据库表**：
- `sys_export_config` - 导出配置主表
- `sys_export_field` - 导出字段配置表
- `sys_export_task` - 导出任务表

**后端文件**：
- `backend/src/main/resources/db/export_schema.sql` - 数据库表结构
- `backend/src/main/java/com/base/common/export/` - 导出公共模块
  - `constant/` - 枚举类（ExportStatusEnum、DataSourceTypeEnum、MaskTypeEnum、FieldTypeEnum）
  - `config/` - 配置类（ExportThreadPoolConfig、ExportFileConfig）
  - `converter/` - 转换器（DataConverter、DictConverter）
  - `mask/` - 脱敏器（DataMasker、PhoneMasker、IdCardMasker、EmailMasker、BankCardMasker、NameMasker）
  - `provider/` - 数据提供者（ExportDataProvider、ServiceDataProvider、SqlDataProvider）
  - `registry/` - 注册表（DataSourceRegistry、ConverterRegistry、MaskerRegistry）
  - `engine/` - 导出引擎（ExportEngine、ExportContext、ExportExecutor）
- `backend/src/main/java/com/base/system/export/` - 导出业务模块
  - `entity/` - 实体类（ExportConfig、ExportField、ExportTask）
  - `mapper/` - Mapper 接口
  - `dto/` - DTO 类（config/、field/、task/）
  - `service/` - 服务接口和实现
  - `controller/` - 控制器（ExportConfigController、ExportTaskController）

**前端文件**：
- `frontend/src/api/exportConfig.js` - 导出配置 API
- `frontend/src/api/exportTask.js` - 导出任务 API
- `frontend/src/views/system/ExportConfig.vue` - 导出配置管理页面
- `frontend/src/views/system/ExportTask.vue` - 导出任务管理页面
- `frontend/src/components/ExportButton.vue` - 通用导出按钮组件

**配置文件**：
- `backend/src/main/resources/application-dev.yml` - 新增导出配置项
- `backend/pom.xml` - 新增 EasyExcel 依赖

**API 接口**：
- 配置管理：`/system/export/config/*`
- 任务管理：`/system/export/task/*`

**扩展方式**：
- 新增数据源：实现 `ExportDataProvider` 接口
- 新增转换器：实现 `DataConverter` 接口
- 新增脱敏规则：实现 `DataMasker` 接口

#### 2. 登录日志导出功能

- **需求**：为登录日志添加导出功能
- **实现**：
  - 后端：
    - `LoginLogService` 新增 `exportCount` 和 `exportPage` 方法
    - `LoginLogServiceImpl` 实现导出查询方法，抽取公共查询条件构建方法
  - 数据库：
    - 创建登录日志导出配置初始化脚本 `init_login_log_export.sql`
    - 配置编码：`login_log_export`
    - 导出字段：日志ID、用户名、登录IP、登录地点、浏览器、操作系统、登录状态、提示信息、登录时间
    - 登录状态使用字典转换（login_status）
  - 前端：
    - 登录日志页面添加"导出"按钮
    - 添加导出进度弹窗，显示导出进度和下载按钮
- **文件**：
  - `backend/src/main/java/com/base/system/service/LoginLogService.java`
  - `backend/src/main/java/com/base/system/service/impl/LoginLogServiceImpl.java`
  - `backend/src/main/resources/db/init_login_log_export.sql`
  - `frontend/src/views/system/LoginLog.vue`

#### 3. 导出模块菜单和权限初始化

- **需求**：为导出模块添加菜单和权限初始化 SQL
- **实现**：
  - 创建 `export_permission.sql` 初始化脚本
  - 添加导出配置、导出任务两个二级菜单（放在系统管理下）
  - 添加配置和任务的按钮权限（查询、新增、编辑、删除）
  - 为超级管理员分配所有导出模块权限
- **文件**：`backend/src/main/resources/db/export_permission.sql`

#### 4. 修复导出模块启动错误

- **问题**：
  1. `ExportExecutor` 在 common 包中直接依赖 system 包的 `ExportTaskMapper`，违反分层架构
  2. `MybatisPlusConfig` 的 `@MapperScan` 未包含 `com.base.system.export.mapper` 包
- **修复**：
  - `ExportExecutor.java`：移除对 `ExportTaskMapper` 的直接依赖，改为通过回调函数 `Consumer<ExportTask>` 更新任务状态
  - `ExportTaskServiceImpl.java`：在调用 `exportExecutor.submit()` 时传入任务更新回调
  - `MybatisPlusConfig.java`：在 `@MapperScan` 中添加 `com.base.system.export.mapper` 包
- **文件**：
  - `backend/src/main/java/com/base/common/export/engine/ExportExecutor.java`
  - `backend/src/main/java/com/base/system/export/service/impl/ExportTaskServiceImpl.java`
  - `backend/src/main/java/com/base/config/MybatisPlusConfig.java`

---

### 2026-02-03

#### 1. K线图添加成交量和涨跌幅显示
- **需求**：在K线图 tooltip 中显示成交量和涨跌幅
- **实现**：
  - 日K线（KlineChart.vue）：
    - 添加 `formatVolume` 函数格式化成交量（支持万/亿单位）
    - 添加 `formatChangeRate` 函数格式化涨跌幅（带正负号和颜色）
    - tooltip 中新增成交量和涨跌幅显示，涨跌幅使用红涨绿跌颜色
  - 分钟K线（MinuteKlineChart.vue）：
    - 添加 `formatVolume` 函数格式化成交量
    - 添加 `calcChangeRate` 函数计算涨跌幅（收盘价相对开盘价）
    - tooltip 中新增成交量和涨跌幅显示
- **文件**：
  - `frontend/src/views/stock/components/KlineChart.vue`
  - `frontend/src/views/stock/components/MinuteKlineChart.vue`

---

### 2026-02-02

#### 1. K线趋势弹窗合并
- **需求**：将日K线和分钟K线合并到同一个弹窗中，使用 tab 页签切换，按钮改为"趋势"
- **实现**：
  - 将操作列的"日K线"和"分钟K线"两个按钮合并为一个"趋势"按钮
  - 新建趋势弹窗，使用 el-tabs 组件实现页签切换
  - 日K线 tab：支持近1周/近1月/近3月日期范围切换，复用 KlineChart 组件
  - 分钟K线 tab：保留原有的1分钟/5分钟切换和加载更多功能
  - 实现懒加载：切换 tab 时才加载对应数据
  - 删除原有的跳转详情页逻辑，改为弹窗内显示
  - 图表组件添加 nextTick + setTimeout resize 处理，解决弹窗打开时图表缩在左边的问题
- **文件**：
  - `frontend/src/views/stock/watchlist/index.vue`
  - `frontend/src/views/stock/components/KlineChart.vue`
  - `frontend/src/views/stock/components/MinuteKlineChart.vue`

#### 2. 分钟K线图表横轴优化
- **问题**：分钟K线图表横轴刻度太密集，每个数据点都显示完整日期时间，导致标签重叠；缩放后标签仍然密集
- **修复**：
  - 添加 `currentZoomStart` 和 `currentZoomEnd` 变量记录当前缩放范围
  - 在 `datazoom` 事件处理中更新缩放范围变量
  - 将 `interval` 改为回调函数，根据可视范围内的数据点数量动态计算显示间隔（目标约 10 个标签）
  - 自定义 `formatter` 函数实现智能日期显示：第一个标签和日期变化时显示 `MM-DD\nHH:mm`，同一天内只显示 `HH:mm`
- **文件**：`frontend/src/views/stock/components/MinuteKlineChart.vue`

#### 3. 自选股分钟K线查看功能
- **需求**：在自选股页面添加分钟K线查看功能，支持1分钟/5分钟K线切换，支持左滑加载更多历史数据
- **实现**：
  - 后端：
    - `ITickApiClient` 接口新增 `fetchMinuteKlineData` 方法
    - `ITickApiClientImpl` 实现分钟K线数据获取，复用 Token 轮询机制
    - 新增 `MinuteKlineResponse` DTO 类
    - `WatchlistService` 和 `WatchlistController` 新增相关方法和接口
  - 前端：
    - `stock.js` 新增 `getMinuteKline` API 方法
    - 新增 `MinuteKlineChart.vue` 组件，基于 ECharts 实现分钟K线图表
- **文件**：
  - `backend/src/main/java/com/base/stock/client/ITickApiClient.java`
  - `backend/src/main/java/com/base/stock/client/impl/ITickApiClientImpl.java`
  - `backend/src/main/java/com/base/stock/dto/MinuteKlineResponse.java`
  - `backend/src/main/java/com/base/stock/service/WatchlistService.java`
  - `backend/src/main/java/com/base/stock/service/impl/WatchlistServiceImpl.java`
  - `backend/src/main/java/com/base/stock/controller/WatchlistController.java`
  - `frontend/src/api/stock.js`
  - `frontend/src/views/stock/components/MinuteKlineChart.vue`
  - `frontend/src/views/stock/watchlist/index.vue`

#### 4. K线数据精准拉取优化
- **问题**：`fetchKlineData()` 方法的日期参数被忽略，`limit=100` 硬编码
- **修复**：根据日期范围动态计算 `limit`，添加 `et` 参数支持结束时间
- **文件**：`backend/src/main/java/com/base/stock/client/impl/ITickApiClientImpl.java`

#### 5. 修复自选列表不显示股票名称和市场问题
- **问题**：自选列表页面股票名称、市场列显示为空
- **修复**：`Watchlist` 实体添加关联字段，`WatchlistMapper` 添加关联查询方法
- **文件**：
  - `backend/src/main/java/com/base/stock/entity/Watchlist.java`
  - `backend/src/main/java/com/base/stock/mapper/WatchlistMapper.java`
  - `backend/src/main/java/com/base/stock/service/impl/WatchlistServiceImpl.java`

#### 6. 股票拉取全部K线功能
- **需求**：添加"拉取全部"按钮，支持按市场和时间范围拉取所有股票K线数据
- **文件**：
  - `backend/src/main/java/com/base/stock/service/StockSyncService.java`
  - `backend/src/main/java/com/base/stock/service/impl/StockSyncServiceImpl.java`
  - `backend/src/main/java/com/base/stock/controller/StockSyncController.java`
  - `frontend/src/api/stock.js`
  - `frontend/src/views/stock/index.vue`

#### 7. Token 失败三次自动作废
- **需求**：Token 失败三次以上自动作废
- **实现**：ApiToken 实体新增 `failCount` 字段，TokenManagerService 新增失败记录方法
- **文件**：
  - `backend/src/main/resources/db/add_token_fail_count.sql`
  - `backend/src/main/java/com/base/stock/entity/ApiToken.java`
  - `backend/src/main/java/com/base/stock/service/TokenManagerService.java`
  - `backend/src/main/java/com/base/stock/service/impl/TokenManagerServiceImpl.java`
  - `backend/src/main/java/com/base/stock/client/impl/ITickApiClientImpl.java`

#### 8. 修复个人中心页面报错
- **问题**：`Unknown column 'create_by' in 'field list'`
- **修复**：`UserRole` 实体类改为独立实体，不继承 `BaseEntity`
- **文件**：`backend/src/main/java/com/base/system/entity/UserRole.java`

#### 9. 修复主页右上角不显示昵称问题
- **问题**：主页右上角只显示用户名，不显示昵称
- **修复**：统一使用 `nickname` 字段
- **文件**：
  - `backend/src/main/java/com/base/system/dto/user/UserProfileResponse.java`
  - `backend/src/main/java/com/base/system/dto/user/UpdateProfileRequest.java`
  - `backend/src/main/java/com/base/system/service/impl/UserProfileServiceImpl.java`
  - `frontend/src/layout/Index.vue`
  - `frontend/src/router/index.js`

#### 10. 修复 iTick API K线数据 kType 参数错误
- **问题**：拉取一个月的K线数据只保存了3条记录
- **修复**：修正 kType 参数映射（日K: 8，周K: 9，月K: 10）
- **文件**：`backend/src/main/java/com/base/stock/client/impl/ITickApiClientImpl.java`

#### 11. 股票查询支持简繁体互查
- **需求**：输入简体字可以查询到繁体字的股票名称
- **实现**：添加 OpenCC4j 依赖，创建简繁体转换工具类
- **文件**：
  - `backend/pom.xml`
  - `backend/src/main/java/com/base/common/util/ChineseConvertUtil.java`
  - `backend/src/main/java/com/base/stock/service/impl/StockServiceImpl.java`

---

### 2026-01-30

#### 1. 添加手动同步股票数据入口
- **功能**：在股票列表页面添加"同步股票"按钮，支持手动拉取股票数据
- **文件**：`frontend/src/views/stock/index.vue`

---

### 2026-01-29

#### 1. 修复刷新页面后权限数据丢失问题
- **问题**：角色管理列表操作栏按钮不显示
- **修复**：创建修复脚本 `fix_role_permissions.sql`
- **文件**：`backend/src/main/resources/db/fix_role_permissions.sql`

#### 2. 修复角色权限分配功能
- **问题**：角色管理页面使用模拟数据
- **修复**：调用真实 API 获取权限树和部门树
- **文件**：`frontend/src/views/system/Role.vue`

#### 3. 修复部门表字段不匹配问题
- **问题**：`Unknown column 'order_num' in 'field list'`
- **修复**：将 `orderNum` 字段改为 `sort`
- **文件**：
  - `backend/src/main/java/com/base/system/entity/Dept.java`
  - `backend/src/main/java/com/base/system/dto/DeptTreeNode.java`
  - `backend/src/main/java/com/base/system/service/impl/DeptServiceImpl.java`

---

### 2026-01-26

#### 1. 行政区划管理模块前端开发
- **页面**：`frontend/src/views/system/Region.vue`
- **组件**：`frontend/src/components/RegionCascader.vue`
- **API**：`frontend/src/api/region.js`
- **功能**：树形结构展示、四级层级、懒加载级联选择器

#### 2. 省市区三级数据导入
- **数据源**：GitHub Administrative-divisions-of-China
- **数据量**：3424 条（省31 + 市337 + 区3056）
- **文件**：`backend/src/main/resources/db/init_region_full.sql`

#### 3. 按钮级别权限控制
- **前端实现**：权限指令 `v-permission`、权限工具函数
- **文件**：
  - `frontend/src/directives/permission.js`
  - `frontend/src/utils/permission.js`
  - `frontend/src/store/user.js`

---

### 2026-01-22

#### 1. 实现登录日志记录功能
- **功能**：记录登录成功/失败、IP、浏览器、操作系统等信息
- **文件**：`backend/src/main/java/com/base/system/service/impl/AuthServiceImpl.java`

#### 2. 修复监控模块权限缺失问题
- **文件**：
  - `backend/src/main/resources/db/data.sql`
  - `backend/src/main/resources/db/update_monitor_permission.sql`

#### 3. 修复登录报错问题
- **问题**：`sys_user` 表缺少 `remark` 字段、`BaseEntity` 类型不匹配
- **文件**：
  - `backend/src/main/resources/db/schema.sql`
  - `backend/src/main/java/com/base/common/entity/BaseEntity.java`

---

### 2026-01-15

#### 系统测试与文档
- **单元测试**：密码加密、SQL注入防护、XSS防护
- **接口测试**：认证、用户、角色、部门控制器
- **文档**：`docs/使用手册.md`、`docs/部署文档.md`

---

### 2026-01-14

#### 1. 通知公告模块完善
- **后端**：标记已读、获取未读数量、我的通知列表
- **前端**：`views/system/MyNotice.vue`、`components/NoticeDropdown.vue`

#### 2. 前端动态路由功能
- **文件**：`frontend/src/api/menu.js`、`frontend/src/utils/route.js`、`frontend/src/store/user.js`、`frontend/src/router/index.js`

#### 3. 个人中心头像上传
- **文件**：`backend/src/main/java/com/base/system/service/UserProfileService.java`、`backend/src/main/java/com/base/system/controller/UserProfileController.java`

#### 4. 系统监控模块
- **后端**：服务器信息（CPU/内存/磁盘/JVM）、Redis缓存信息
- **前端**：`views/monitor/Server.vue`、`views/monitor/Cache.vue`

---

### 2026-01-13

#### 1. 个人中心页面
- **文件**：`frontend/src/views/profile/Index.vue`
- **功能**：头像上传、基本信息编辑、修改密码

#### 2. 通知公告管理页面
- **文件**：`frontend/src/views/system/Notice.vue`
- **功能**：通知列表、新增/编辑、发布、删除

---

### 2026-01-12

#### 项目基础架构搭建

**后端**：统一响应结构、全局异常处理器、JWT/Redis 工具类、MyBatis Plus、Spring Security

**数据库**：用户表、角色表、权限表、部门表、枚举表、全局变量表、日志表、通知表

**前端**：Vue 3 + Vite + Element Plus + Pinia、Axios 封装、路由配置、登录页面、首页、基础布局

**功能模块**：登录认证、用户管理、角色管理、权限/菜单管理、部门管理、枚举管理、全局变量管理、日志管理

---

---

### 2026-02-06

#### 股票推荐打分系统（后端完成）

**需求背景**：
- 基于股票日K线数据的智能推荐系统
- 通过多维度技术分析规则对股票进行打分
- 支持规则配置、权重调整、定时打分
- 架构高度解耦，支持动态扩展打分规则

**技术架构**：
- **设计模式**：策略模式 + Spring Bean自动发现
- **定时任务**：Spring @Scheduled（每天16:30执行）
- **数据存储**：MySQL（3张新表）

**数据库表**：
1. `stk_score_rule` - 打分规则配置表（规则元数据、分数、权重、参数）
2. `stk_score_record` - 打分记录表（每只股票每条规则的详细得分）
3. `stk_recommend` - 推荐股票表（每日汇总结果和排名）

**核心模块**：
- **策略框架**：`ScoreStrategy` 接口、`ScoreContext` 上下文、`ScoreResult` 结果
- **打分引擎**：`ScoreEngine` - 自动注入所有策略Bean，按顺序执行打分
- **打分服务**：`ScoreService` - 单只/批量/全量打分、排名计算
- **规则管理**：`ScoreRuleService` - 规则CRUD、启用/禁用
- **推荐查询**：`RecommendService` - 分页查询推荐列表、打分明细

**5条经典打分规则**：
1. **均线多头排列** (MA_ALIGNMENT) - MA5 > MA10 > MA20 > MA60，固定20分，权重1.5
2. **成交量突破** (VOLUME_BREAK) - 成交量突破近期平均水平，动态10-20分，权重1.2
3. **连续上涨** (CONTINUOUS_RISE) - 连续3-5天收盘价上涨，动态10-15分，权重1.0
4. **MACD金叉** (MACD_GOLDEN_CROSS) - DIF上穿DEA形成金叉，固定15分，权重1.3
5. **突破前高** (BREAK_HIGH) - 突破近20日最高价，动态10-20分，权重1.1

**后端文件清单**：
- 数据库：`backend/src/main/resources/db/recommend_schema.sql`
- 实体类：`backend/src/main/java/com/base/stock/recommend/entity/` (3个)
- Mapper：`backend/src/main/java/com/base/stock/recommend/mapper/` (3个)
- Service：`backend/src/main/java/com/base/stock/recommend/service/` (3个接口 + 3个实现)
- Controller：`backend/src/main/java/com/base/stock/recommend/controller/` (2个)
- 策略框架：`backend/src/main/java/com/base/stock/recommend/strategy/` (接口、上下文、结果)
- 策略实现：`backend/src/main/java/com/base/stock/recommend/strategy/impl/` (5个)
- 打分引擎：`backend/src/main/java/com/base/stock/recommend/engine/ScoreEngine.java`
- 定时任务：`backend/src/main/java/com/base/stock/recommend/task/DailyScoreTask.java`
- 启动类修改：`backend/src/main/java/com/base/system/BaseSystemApplication.java` (添加@EnableScheduling)

**API接口**：
- `GET /stock/recommend/list` - 分页查询推荐列表
- `GET /stock/recommend/detail` - 查询打分明细
- `POST /stock/recommend/execute` - 手动触发打分
- `GET /stock/recommend/latest-date` - 查询最新推荐日期
- `GET /stock/recommend/rule/list` - 查询规则列表
- `PUT /stock/recommend/rule/{id}` - 更新规则配置
- `POST /stock/recommend/rule/{id}/enable` - 启用规则
- `POST /stock/recommend/rule/{id}/disable` - 禁用规则

**扩展性设计**：
- 新增规则只需：1) 数据库插入规则配置 2) 创建策略实现类（实现ScoreStrategy接口）3) 标注@Component注解
- 无需修改：打分引擎、打分服务、前端页面
- 预留实时打分接口（supportRealtime方法）

**前端文件清单**：
- API封装：`frontend/src/api/recommend.js`
- 推荐列表页面：`frontend/src/views/stock/recommend/index.vue`
- 规则配置页面：`frontend/src/views/stock/recommend/rule.vue`

**前端功能**：
- 推荐列表：日期选择、分页展示、排名标识、打分明细查看、K线图跳转
- 规则配置：规则列表、编辑规则（分数、权重、参数）、启用/禁用规则
- 手动打分：支持手动触发打分任务

**待完成**：
- 数据库表初始化（执行 recommend_schema.sql）
- 添加菜单和权限配置
- 功能测试和验证

---

### 2026-02-06

#### 优化推荐股票排名逻辑

- **需求**：去掉排名字段，改为根据分数降序查询后直接使用序号
- **优化内容**：
  - 后端：删除 `RecommendStock` 实体的 `rank` 字段
  - 后端：删除 `RecommendStockMapper.updateRank()` 方法
  - 后端：删除 `ScoreService.calculateRank()` 方法及其实现
  - 后端：删除打分完成后调用 `calculateRank` 的代码
  - 前端：将"排名"列改为"序号"列，使用 `type="index"` 自动生成序号
  - 前端：添加 `getTableIndex` 函数计算考虑分页的序号
- **优势**：简化逻辑，无需维护排名字段，排序完全依赖查询结果
- **修改文件**：
  - `backend/src/main/java/com/base/stock/recommend/entity/RecommendStock.java`
  - `backend/src/main/java/com/base/stock/recommend/mapper/RecommendStockMapper.java`
  - `backend/src/main/java/com/base/stock/recommend/service/ScoreService.java`
  - `backend/src/main/java/com/base/stock/recommend/service/impl/ScoreServiceImpl.java`
  - `frontend/src/views/stock/recommend/index.vue`

#### 修复推荐股票排序问题

- **问题**：推荐股票列表没有按分数排序
- **原因**：后端查询时按 `rank` 字段排序，但只有全量打分时才会更新排名，手动打分单只或部分股票时排名不会更新
- **修复**：将排序逻辑从按 `rank` 升序改为按 `totalScore` 降序，确保始终按分数从高到低排序
- **修改文件**：
  - `backend/src/main/java/com/base/stock/recommend/service/impl/RecommendServiceImpl.java` - 修改 `pageRecommend` 和 `listRecommend` 方法的排序逻辑

#### 批量K线同步接口优化

- **需求**：当需要同步的股票数量超过配置阈值时，使用itick的批量获取K线接口
- **优化效果**：性能提升约100倍（100只股票从100秒降至1秒）
- **配置化参数**：
  - `stock.sync.batch.threshold`：批量同步阈值（默认100）
  - `stock.sync.batch.size`：批量请求大小（默认100）
  - 配置存储在 `sys_config` 表，支持动态调整
- **实现方式**：
  - 添加配置初始化SQL脚本
  - 添加批量K线接口调用方法 `ITickApiClient.fetchBatchKlineData()`
  - 添加批量数据解析方法 `parseBatchKlineJson()`
  - 重构批量同步逻辑，支持配置化阈值判断、市场分组、分批处理
  - 保持向后兼容，股票数量未超过阈值时使用原有单个接口
- **修改文件**：
  - `backend/src/main/resources/db/stock_sync_config.sql` - 配置初始化脚本（新建）
  - `backend/src/main/java/com/base/stock/client/ITickApiClient.java` - 新增批量接口方法签名
  - `backend/src/main/java/com/base/stock/client/impl/ITickApiClientImpl.java` - 实现批量接口调用
  - `backend/src/main/java/com/base/stock/service/impl/StockSyncServiceImpl.java` - 重构批量同步逻辑，注入ConfigService

---

### 2026-02-10

#### 股票行业中英文对照映射（改为后端转换）

#### 分页查询统一改造（14个接口）

**需求背景**：
- 14个分页查询接口全部使用 GET 请求，不利于传递复杂查询条件
- 没有统一的分页参数基类，各 QueryRequest 各自定义分页字段
- 分页参数命名不一致：`current/size`（system）、`pageNum/pageSize`（export）、`page/size`（stock）
- 参数类型不一致：Integer / Long / int 混用
- stock 模块 4 个接口使用散参数，缺少请求对象

**改造内容**：

1. **创建 BasePageRequest 基类**
   - `common/dto/BasePageRequest.java` - 统一分页参数（current/size，Long 类型），提供 `buildPage()` 便捷方法

2. **改造 9 个 QueryRequest 类**（继承 BasePageRequest，删除自身分页字段）
   - `UserQueryRequest`、`RoleQueryRequest`、`ConfigQueryRequest`
   - `LoginLogQueryRequest`、`OperationLogQueryRequest`、`NoticeQueryRequest`
   - `EnumQueryRequest`、`ExportConfigQueryRequest`、`ExportTaskQueryRequest`

3. **新建 4 个 QueryRequest 类**（stock 模块）
   - `StockQueryRequest`（market, industry, keyword）
   - `SyncFailureQueryRequest`（stockCode, status）
   - `RecommendQueryRequest`（recommendDate）
   - `ScoreRuleQueryRequest`（预留扩展）

4. **改造 14 个 Controller 方法**（GET → POST + @RequestBody）
   - system 模块 8 个、export 模块 2 个、stock 模块 4 个

5. **改造 Service 层**
   - 9 个 ServiceImpl 中 `new Page<>()` 统一替换为 `request.buildPage()`
   - StockService、RecommendService 接口签名改为接收 QueryRequest 对象
   - SyncFailureService 新增 `pageFailures` 方法，查询逻辑从 Controller 下沉到 Service

6. **改造前端**
   - 11 个 API 方法从 `get + params` 改为 `post + data`
   - 4 个页面分页参数统一为 `current/size`：ExportConfig、ExportTask、stock/index、recommend/index

**新增文件**：
- `backend/src/main/java/com/base/common/dto/BasePageRequest.java`
- `backend/src/main/java/com/base/stock/dto/StockQueryRequest.java`
- `backend/src/main/java/com/base/stock/dto/SyncFailureQueryRequest.java`
- `backend/src/main/java/com/base/stock/recommend/dto/RecommendQueryRequest.java`
- `backend/src/main/java/com/base/stock/recommend/dto/ScoreRuleQueryRequest.java`

**修改文件（后端）**：
- 9 个 QueryRequest 类、14 个 Controller、12 个 Service/ServiceImpl

**修改文件（前端）**：
- `api/user.js`、`api/role.js`、`api/config.js`、`api/loginLog.js`、`api/operationLog.js`
- `api/notice.js`、`api/enum.js`、`api/exportConfig.js`、`api/exportTask.js`
- `api/stock.js`、`api/recommend.js`
- `views/system/ExportConfig.vue`、`views/system/ExportTask.vue`
- `views/stock/index.vue`、`views/stock/recommend/index.vue`

---

**需求**：股票所属行业字段存储的是英文，需要显示中文；中文转换改为后端处理

**实现方案**：
- 通过系统枚举管理功能配置行业映射（`stock_industry`），支持后台动态维护
- 后端查询股票列表时，从枚举服务获取映射，填充 `industryCn` 字段返回前端
- 前端直接使用后端返回的 `industryCn` 字段显示中文
- 行业下拉筛选选项通过独立接口 `/stock/industry/options` 获取

**后端修改**：
- `StockInfo.java` - 新增 `industryCn` 字段（`@TableField(exist = false)`，非数据库字段）
- `StockService.java` - 新增 `listIndustryOptions()` 方法
- `StockServiceImpl.java` - 注入 `EnumService`，查询列表后填充 `industryCn`；实现行业选项列表方法
- `StockController.java` - 新增 `GET /stock/industry/options` 接口

**前端修改**：
- `frontend/src/api/stock.js` - 新增 `listIndustryOptions()` API 方法
- `frontend/src/views/stock/index.vue` - 去掉枚举接口调用，改用后端 `industryCn` 字段和行业选项接口

**数据库初始化**：
- `backend/src/main/resources/db/init_stock_industry.sql` - 行业枚举初始化脚本（需修复语法错误后执行）

**扩展方式**：通过系统管理 -> 枚举管理页面，编辑 `stock_industry` 类型即可动态添加/修改映射

---

### 2026-02-09

#### 基金估值功能优化

**需求变更**：
- 进入基金列表时不再查询实时估值，只显示基金基本信息
- 点击查看详情时才实时获取估值数据
- 详情弹窗提供刷新估值功能
- 详情弹窗添加编辑基金入口
- 实时估值结果缓存到 Redis，过期时间 1 小时
- 列表页显示 Redis 缓存的估值数据

**修改内容**：

1. **后端 Redis 缓存**
   - `FundValuationResponse` 新增 `cacheTime` 字段，记录缓存时间
   - `FundService` 新增接口：
     - `getCachedValuation(Long fundId)` - 获取缓存的估值
     - `listFundsWithCachedValuation()` - 查询基金列表（带缓存估值）
   - `FundServiceImpl` 实现：
     - `getValuation` 方法获取实时估值后缓存到 Redis（key: `fund:valuation:{fundId}`，过期时间 1 小时）
     - `getCachedValuation` 从 Redis 获取缓存估值
     - `listFundsWithCachedValuation` 返回基金列表，优先使用缓存估值
   - `FundController` 的 `/list` 接口改为调用 `listFundsWithCachedValuation()`

2. **前端列表页优化**
   - 列表卡片显示缓存的估值数据（涨跌幅、持仓数量）
   - 显示缓存时间（如"5分钟前"、"1小时前"）
   - 无缓存时显示"暂无估值，点击查看获取"

3. **缓存策略**
   - 缓存 Key：`fund:valuation:{fundId}`
   - 过期时间：3600 秒（1 小时）
   - 触发缓存：用户点击查看详情时

**修改文件**：
- `backend/src/main/java/com/base/stock/fund/dto/FundValuationResponse.java` - 新增 cacheTime 字段
- `backend/src/main/java/com/base/stock/fund/service/FundService.java` - 新增接口方法
- `backend/src/main/java/com/base/stock/fund/service/impl/FundServiceImpl.java` - 实现 Redis 缓存逻辑
- `backend/src/main/java/com/base/stock/fund/controller/FundController.java` - 修改 list 接口
- `frontend/src/views/stock/fund/index.vue` - 显示缓存估值和时间

---

#### 基金估值功能

**需求背景**：
- 用户需要一个基金估值功能，允许自行配置基金中的股票和占比
- 实时计算基金涨跌幅，使用 iTick 批量实时报价接口获取数据
- 本地不存储实时数据，直接调用 API 返回前端显示

**核心约束**：
- iTick 实时报价接口 `codes` 参数最多只能传 3 个股票代码
- 需要多线程分批请求后汇总

**数据库表**：
- `stk_fund_config` - 基金配置主表（用户ID、基金名称、代码、描述、状态）
- `stk_fund_holding` - 基金持仓明细表（基金ID、股票代码、权重占比）

**后端文件**：
- `backend/src/main/resources/db/fund_schema.sql` - 数据库表结构
- `backend/src/main/java/com/base/stock/fund/entity/FundConfig.java` - 基金配置实体
- `backend/src/main/java/com/base/stock/fund/entity/FundHolding.java` - 基金持仓实体
- `backend/src/main/java/com/base/stock/fund/dto/FundConfigRequest.java` - 配置请求DTO
- `backend/src/main/java/com/base/stock/fund/dto/FundValuationResponse.java` - 估值响应DTO
- `backend/src/main/java/com/base/stock/fund/dto/StockQuote.java` - 股票报价DTO
- `backend/src/main/java/com/base/stock/fund/mapper/FundConfigMapper.java` - 基金配置Mapper
- `backend/src/main/java/com/base/stock/fund/mapper/FundHoldingMapper.java` - 基金持仓Mapper
- `backend/src/main/java/com/base/stock/fund/service/FundService.java` - 服务接口
- `backend/src/main/java/com/base/stock/fund/service/impl/FundServiceImpl.java` - 服务实现（含多线程报价请求）
- `backend/src/main/java/com/base/stock/fund/controller/FundController.java` - 控制器

**前端文件**：
- `frontend/src/api/fund.js` - API 封装
- `frontend/src/views/stock/fund/index.vue` - 基金列表页（含实时估值、持仓明细弹窗、新建/编辑弹窗）

**修改文件**：
- `backend/src/main/java/com/base/config/MybatisPlusConfig.java` - 添加 fund.mapper 包扫描

**API接口**：
- `GET /stock/fund/list` - 查询基金列表
- `GET /stock/fund/{id}` - 查询基金详情
- `POST /stock/fund` - 创建基金
- `PUT /stock/fund/{id}` - 更新基金
- `DELETE /stock/fund/{id}` - 删除基金
- `GET /stock/fund/{id}/valuation` - 获取单个基金实时估值
- `POST /stock/fund/valuation/batch` - 批量获取基金实时估值
- `GET /stock/fund/valuation/all` - 获取所有基金实时估值

**多线程请求核心逻辑**：
1. 查询基金持仓列表
2. 按市场(HK/SH/SZ)分组股票
3. 每组按3个一批分割
4. 使用 CompletableFuture 并发请求所有批次
5. 汇总报价数据到 ConcurrentHashMap
6. 计算加权涨跌幅：Σ(股票涨跌幅 × 权重) / 100

**待完成**：
- 执行 `fund_schema.sql` 创建数据库表
- 添加菜单和权限配置
- 功能测试和验证

---

#### 批量拉取数据库操作优化

**优化背景**：
- 原有批量同步逻辑中，数据库操作采用逐条处理方式（先查询是否存在，再插入或更新）
- 每条记录需要2次数据库交互（1次查询 + 1次插入/更新），效率低下
- 同步1000只股票的K线数据，可能产生数万次数据库操作

**优化方案**：
- 使用 MySQL 的 `INSERT ... ON DUPLICATE KEY UPDATE` 语法实现批量 upsert
- 将逐条操作改为批量操作，每批500条
- 并发同步时先收集所有数据，最后一次性批量保存

**优化内容**：

1. **StockKlineMapper 批量操作**
   - 新增 `batchUpsert` 方法
   - 使用唯一索引 `(stock_code, trade_date)` 实现冲突更新

2. **StockInfoMapper 批量操作**
   - 新增 `batchUpsert` 方法
   - 使用唯一索引 `(stock_code)` 实现冲突更新

3. **SyncFailureService 批量操作**
   - 新增 `batchMarkSuccess` - 批量标记成功
   - 新增 `batchMarkAbandoned` - 批量标记放弃
   - 新增 `batchUpdateRetryCount` - 批量更新重试次数

4. **StockSyncServiceImpl 优化**
   - `syncStockList` - 改为批量 upsert
   - `syncKlineData` - 改为批量 upsert
   - `batchSyncAllKlineDataConcurrent` - 先收集所有K线数据，最后批量保存
   - `saveKlineData` / `batchSaveKlineData` - 使用批量 upsert

**新增文件**：
- `backend/src/main/resources/mapper/StockKlineMapper.xml` - K线批量操作SQL
- `backend/src/main/resources/mapper/StockInfoMapper.xml` - 股票信息批量操作SQL

**修改文件**：
- `backend/src/main/java/com/base/stock/mapper/StockKlineMapper.java` - 新增 batchUpsert 方法
- `backend/src/main/java/com/base/stock/mapper/StockInfoMapper.java` - 新增 batchUpsert 方法
- `backend/src/main/java/com/base/stock/service/SyncFailureService.java` - 新增批量操作方法
- `backend/src/main/java/com/base/stock/service/impl/SyncFailureServiceImpl.java` - 实现批量操作
- `backend/src/main/java/com/base/stock/service/impl/StockSyncServiceImpl.java` - 优化为批量操作

**性能提升**：
- 原：每条记录2次DB操作 → 现：每批1次DB操作
- 同步1000只股票（每只100条K线）：原约20万次DB操作 → 现约200次DB操作

**Bug修复 - trade_date 为空导致批量插入失败**：
- **问题**：批量插入K线数据时报错 `Column 'trade_date' cannot be null`
- **原因**：API 返回的数据中某些记录的时间戳字段 `t` 为 null，转换后 `tradeDate` 也为 null
- **修复**：在 `batchSaveKlineData` 方法中添加过滤逻辑，过滤掉 `tradeDate` 为 null 的无效记录
- **修改文件**：`backend/src/main/java/com/base/stock/service/impl/StockSyncServiceImpl.java`

---

#### 批量拉取数据多线程优化

**需求背景**：
- 当前批量同步采用单线程顺序执行，无法充分利用多个Token的并发能力
- 缺少失败追溯机制，同步失败时仅记录日志
- 无补拉机制，失败数据需要重新执行整个批量同步

**技术方案**：
- 在HTTP请求层封装多线程能力，而非业务层使用多线程
- 共享Token池，线程从池中获取Token，用完归还
- 逐个提交请求，内部自动并发执行

**核心组件**：

1. **并发HTTP执行器** (`ConcurrentHttpExecutor`)
   - 管理Token池（`BlockingQueue<ApiToken>`）
   - 管理线程池（`ThreadPoolExecutor`）
   - 线程数计算：`Token数 / 6`（最小1，最大10）
   - 支持同步/异步执行、批量执行

2. **执行器工厂** (`ConcurrentHttpExecutorFactory`)
   - 按服务商管理执行器实例
   - 支持Token池刷新

3. **失败记录机制**
   - 记录失败的股票代码、日期范围、失败原因
   - 支持重试次数跟踪和状态管理

4. **补拉功能**
   - 查询待重试的失败记录
   - 使用并发执行器批量补拉
   - 自动更新失败记录状态

**数据库表**：
- `stk_sync_failure` - 股票同步失败记录表

**配置参数**（`application-dev.yml`）：
```yaml
stock:
  sync:
    tokens-per-thread: 6      # 每个线程需要的Token数量
    max-threads: 10           # 最大线程数
    token-acquire-timeout: 30000  # 获取Token超时时间（毫秒）
    max-retry-count: 3        # 失败最大重试次数
```

**新增文件**：
- `backend/src/main/resources/db/sync_failure_schema.sql` - 失败记录表结构
- `backend/src/main/java/com/base/stock/entity/SyncFailure.java` - 失败记录实体
- `backend/src/main/java/com/base/stock/mapper/SyncFailureMapper.java` - Mapper接口
- `backend/src/main/java/com/base/stock/service/SyncFailureService.java` - 服务接口
- `backend/src/main/java/com/base/stock/service/impl/SyncFailureServiceImpl.java` - 服务实现
- `backend/src/main/java/com/base/stock/http/ConcurrentHttpRequest.java` - 请求封装类
- `backend/src/main/java/com/base/stock/http/ConcurrentHttpResponse.java` - 响应封装类
- `backend/src/main/java/com/base/stock/http/ConcurrentHttpExecutor.java` - 并发执行器
- `backend/src/main/java/com/base/stock/http/ConcurrentHttpExecutorFactory.java` - 执行器工厂
- `backend/src/main/java/com/base/stock/config/StockSyncConfig.java` - 同步配置类

**修改文件**：
- `backend/src/main/java/com/base/stock/service/TokenManagerService.java` - 新增 `getAvailableTokens` 方法
- `backend/src/main/java/com/base/stock/service/impl/TokenManagerServiceImpl.java` - 实现获取所有可用Token
- `backend/src/main/java/com/base/stock/client/impl/ITickApiClientImpl.java` - 添加并发执行方法
- `backend/src/main/java/com/base/stock/service/impl/StockSyncServiceImpl.java` - 添加并发同步和补拉方法
- `backend/src/main/java/com/base/stock/controller/StockSyncController.java` - 添加新接口
- `backend/src/main/resources/application-dev.yml` - 添加同步配置

**API接口**：
- `POST /stock/sync/kline/all/concurrent` - 并发批量同步K线数据
- `POST /stock/sync/retry-failed` - 补拉失败数据
- `GET /stock/sync/failure/list` - 查询失败记录列表

**待完成**：
- 执行 `sync_failure_schema.sql` 创建失败记录表
- 功能测试和验证

---

#### 股票详情信息扩展

**需求**：
- 扩展 `stk_stock_info` 表，添加股票详情字段
- 详情需要单独调用 iTick `/stock/info` 接口，一次只能传一个股票代码
- 添加批量和单独更新股票详情的方法

**新增字段**：
| 字段 | 类型 | 说明 |
|------|------|------|
| stock_type | VARCHAR(20) | 股票类型（stock-股票） |
| sector | VARCHAR(100) | 所属板块 |
| industry | VARCHAR(100) | 所属行业 |
| business_desc | TEXT | 公司简介 |
| website_url | VARCHAR(255) | 公司网站URL |
| market_cap | DECIMAL(20,2) | 总市值 |
| total_shares | DECIMAL(20,2) | 总股本 |
| pe_ratio | DECIMAL(10,4) | 市盈率 |
| high_52_week | DECIMAL(10,4) | 52周最高价 |
| low_52_week | DECIMAL(10,4) | 52周最低价 |

**修改文件**：
- `backend/src/main/java/com/base/stock/entity/StockInfo.java` - 实体类新增字段
- `backend/src/main/java/com/base/stock/client/ITickApiClient.java` - 新增 `fetchStockInfo` 方法
- `backend/src/main/java/com/base/stock/client/impl/ITickApiClientImpl.java` - 实现获取股票详情
- `backend/src/main/java/com/base/stock/service/StockSyncService.java` - 新增接口方法
- `backend/src/main/java/com/base/stock/service/impl/StockSyncServiceImpl.java` - 实现同步逻辑
- `backend/src/main/java/com/base/stock/controller/StockSyncController.java` - 新增接口
- `backend/src/main/resources/mapper/StockInfoMapper.xml` - 更新批量 upsert SQL

**新增文件**：
- `backend/src/main/resources/db/stock_info_detail.sql` - 数据库迁移脚本

**API接口**：
- `POST /stock/sync/info/{stockCode}` - 同步单只股票详情
- `POST /stock/sync/info/batch` - 批量同步股票详情（按市场）

**待完成**：
- 执行 `stock_info_detail.sql` 添加数据库字段

---

#### 修复股票推荐K线按钮报错

**问题**：点击股票推荐页面的"K线"按钮报错 `Maximum call stack size exceeded`

**原因**：
- 原代码使用 `router.push({ path: '/stock/detail', query: { code: row.stockCode } })` 跳转
- 但路由配置是 `stock/detail/:code`（路径参数），不是 query 参数
- 路由匹配失败导致无限重定向

**修复方案**：
- 抽取公共组件 `TrendDialog.vue`，封装趋势弹窗功能
- 推荐页面和自选股页面都复用该组件
- 组件支持 v-model 控制显示、stock-code/stock-name 属性、header slot 自定义头部
- 组件暴露 `getBothChartImages` 方法供 PDF 导出使用

**新增文件**：
- `frontend/src/views/stock/components/TrendDialog.vue` - 趋势弹窗公共组件

**修改文件**：
- `frontend/src/views/stock/recommend/index.vue` - 使用 TrendDialog 组件
- `frontend/src/views/stock/watchlist/index.vue` - 使用 TrendDialog 组件，代码精简约 200 行

---

## 开发计划变更记录

| 日期 | 变更内容 |
|------|----------|
| 2026-02-10 | 分页查询统一改造（14个接口） |
| 2026-02-10 | 股票行业中英文对照映射 |
| 2026-02-09 | 修复股票推荐K线按钮报错 |
| 2026-02-09 | 股票推荐单条打分权限控制 |
| 2026-02-09 | 基金估值功能 |
| 2026-02-09 | 批量拉取数据多线程优化 |
| 2026-02-06 | 股票推荐打分系统后端开发完成 |
| 2026-02-04 | 通用导出功能模块开发 |
| 2026-02-02 | K线趋势弹窗合并、分钟K线功能完善 |
| 2026-01-30 | 添加手动同步股票数据入口 |
| 2026-01-29 | 股票数据分析功能开发完成 |
| 2026-01-26 | 行政区划管理模块、按钮权限控制 |
| 2026-01-14 | 通知公告完善、动态路由、系统监控 |
| 2026-01-12 | 项目基础架构搭建、核心功能模块开发 |
