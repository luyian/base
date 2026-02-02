# 业务逻辑变更记录

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

## 开发计划变更记录

| 日期 | 变更内容 |
|------|----------|
| 2026-02-02 | K线趋势弹窗合并、分钟K线功能完善 |
| 2026-01-30 | 添加手动同步股票数据入口 |
| 2026-01-29 | 股票数据分析功能开发完成 |
| 2026-01-26 | 行政区划管理模块、按钮权限控制 |
| 2026-01-14 | 通知公告完善、动态路由、系统监控 |
| 2026-01-12 | 项目基础架构搭建、核心功能模块开发 |
