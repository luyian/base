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

**下一步**：功能测试和验证

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

#### 1. 股票拉取全部K线功能
- **需求**：添加"拉取全部"按钮，支持时间范围，不再只是拉取关注的股票
- **实现**：
  - 后端新增 `batchSyncAllKlineData` 方法，支持按市场拉取所有股票K线数据
  - 新增 API 接口 `POST /stock/sync/kline/all`，支持 market、startDate、endDate 参数
  - 前端股票列表页面添加"拉取全部K线"按钮
  - 弹窗支持选择市场（全部/港股/沪市/深市）和时间范围
- **文件**：
  - `backend/src/main/java/com/base/stock/service/StockSyncService.java` - 新增接口方法
  - `backend/src/main/java/com/base/stock/service/impl/StockSyncServiceImpl.java` - 实现拉取全部逻辑
  - `backend/src/main/java/com/base/stock/controller/StockSyncController.java` - 新增 API
  - `frontend/src/api/stock.js` - 新增 batchSyncAllKline 接口
  - `frontend/src/views/stock/index.vue` - 添加拉取全部K线按钮和对话框

#### 2. Token 失败三次自动作废
- **需求**：Token 失败三次以上就直接作废，不再使用
- **实现**：
  - ApiToken 实体新增 `failCount` 字段记录连续失败次数
  - TokenManagerService 新增 `recordTokenFailure` 和 `resetTokenFailure` 方法
  - ITickApiClientImpl 在请求失败时记录失败次数，成功时重置
  - 当失败次数达到3次时，自动将 Token 状态设为作废（status=0）
- **文件**：
  - `backend/src/main/resources/db/add_token_fail_count.sql` - 数据库字段更新脚本
  - `backend/src/main/java/com/base/stock/entity/ApiToken.java` - 新增 failCount 字段
  - `backend/src/main/java/com/base/stock/service/TokenManagerService.java` - 新增接口方法
  - `backend/src/main/java/com/base/stock/service/impl/TokenManagerServiceImpl.java` - 实现失败记录逻辑
  - `backend/src/main/java/com/base/stock/client/impl/ITickApiClientImpl.java` - 集成失败处理

#### 3. 修复个人中心页面报错
- **问题**：点击个人中心报错 `Unknown column 'create_by' in 'field list'`
- **原因**：`UserRole` 实体类继承了 `BaseEntity`，但 `sys_user_role` 表只有 `id`、`user_id`、`role_id`、`create_time` 四个字段，没有 `create_by`、`update_by`、`update_time`、`deleted` 字段
- **修复**：将 `UserRole` 实体类改为独立实体，不继承 `BaseEntity`，只定义表中实际存在的字段
- **文件**：`backend/src/main/java/com/base/system/entity/UserRole.java`

#### 2. 修复主页右上角不显示昵称问题
- **问题**：主页右上角只显示用户名（admin），不显示昵称（超级管理员）
- **原因**：`/system/profile` API 返回的 `UserProfileResponse` 使用 `name` 字段，而 `/auth/info` API 返回的 `UserInfoResponse` 使用 `nickname` 字段。当访问个人中心页面时，`profile/Index.vue` 用 `getProfile()` 返回的数据覆盖了 store 中的 userInfo，导致 `nickname` 字段丢失
- **修复**：统一使用 `nickname` 字段
  - `UserProfileResponse.java` - 将 `name` 改为 `nickname`
  - `UpdateProfileRequest.java` - 将 `name` 改为 `nickname`
  - `UserProfileServiceImpl.java` - 将 `request.getName()` 改为 `request.getNickname()`
  - `frontend/src/layout/Index.vue` - 显示 `nickname` 而非 `username`
  - `frontend/src/router/index.js` - 避免重复加载 userInfo
- **文件**：
  - `backend/src/main/java/com/base/system/dto/user/UserProfileResponse.java`
  - `backend/src/main/java/com/base/system/dto/user/UpdateProfileRequest.java`
  - `backend/src/main/java/com/base/system/service/impl/UserProfileServiceImpl.java`
  - `frontend/src/layout/Index.vue`
  - `frontend/src/router/index.js`

#### 3. 修复 iTick API K线数据 kType 参数错误
- **问题**：拉取一个月的K线数据只保存了3条记录
- **原因**：kType 参数值映射错误，使用了错误的枚举值
- **修复**：根据 iTick API 文档修正 kType 参数映射
  - 日K：`kType=2` → `kType=8`（一天）
  - 周K：`kType=3` → `kType=9`（一周）
  - 月K：`kType=4` → `kType=10`（一月）
- **文件**：`backend/src/main/java/com/base/stock/client/impl/ITickApiClientImpl.java`

#### 2. 股票查询支持简繁体互查
- **需求**：输入简体字可以查询到繁体字的股票名称（港股股票名称多为繁体）
- **实现**：
  - 添加 OpenCC4j 依赖（简繁体转换库）
  - 创建 `ChineseConvertUtil` 工具类
  - 修改 `StockServiceImpl.pageStocks()` 方法，查询时同时匹配简体和繁体关键字
- **文件**：
  - `backend/pom.xml` - 添加 opencc4j 依赖
  - `backend/src/main/java/com/base/common/util/ChineseConvertUtil.java` - 简繁体转换工具类
  - `backend/src/main/java/com/base/stock/service/impl/StockServiceImpl.java` - 修改查询逻辑

---

### 2026-01-30

#### 1. 添加手动同步股票数据入口
- **功能**：在股票列表页面添加"同步股票"按钮，支持手动拉取股票数据
- **实现**：
  - 搜索栏新增"同步股票"按钮（橙色警告样式）
  - 点击弹出对话框，可选择市场（港股/沪市/深市）
  - 调用 `/stock/sync/stock-list` 接口同步数据
  - 同步完成后自动刷新列表
- **文件**：`frontend/src/views/stock/index.vue`

---

### 2026-01-29

#### 1. 修复刷新页面后权限数据丢失问题
- **问题**：角色管理列表操作栏按钮不显示
- **原因**：
  - 权限编码不一致（数据库 vs 前端代码）
  - 缺少角色权限关联数据
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
- **组件**：`frontend/src/components/RegionCascader.vue`（地址选择组件）
- **API**：`frontend/src/api/region.js`
- **功能**：
  - 树形结构展示区划数据
  - 支持四级层级（省/市/区/街道）
  - 懒加载级联选择器

#### 2. 省市区三级数据导入
- **数据源**：GitHub Administrative-divisions-of-China
- **数据量**：3424 条（省31 + 市337 + 区3056）
- **文件**：`backend/src/main/resources/db/init_region_full.sql`

#### 3. 按钮级别权限控制
- **前端实现**：
  - 权限指令 `v-permission`
  - 权限工具函数 `utils/permission.js`
  - 用户 Store 添加权限管理
- **使用示例**：
  ```vue
  <el-button v-permission="'system:user:add'">新增</el-button>
  ```
- **文件**：
  - `frontend/src/directives/permission.js`
  - `frontend/src/utils/permission.js`
  - `frontend/src/store/user.js`

---

### 2026-01-22

#### 1. 实现登录日志记录功能
- **修复**：在 `AuthServiceImpl` 中添加登录日志记录逻辑
- **功能**：记录登录成功/失败、IP、浏览器、操作系统等信息
- **文件**：`backend/src/main/java/com/base/system/service/impl/AuthServiceImpl.java`

#### 2. 修复监控模块权限缺失问题
- **修复**：补充服务器监控、缓存监控的权限数据
- **文件**：
  - `backend/src/main/resources/db/data.sql`
  - `backend/src/main/resources/db/update_monitor_permission.sql`

#### 3. 修复登录报错问题
- **问题1**：`sys_user` 表缺少 `remark` 字段
- **问题2**：`BaseEntity` 中 `createBy/updateBy` 类型不匹配
- **文件**：
  - `backend/src/main/resources/db/schema.sql`
  - `backend/src/main/java/com/base/common/entity/BaseEntity.java`

---

### 2026-01-15

#### 系统测试与文档
- **单元测试**：密码加密、SQL注入防护、XSS防护
- **接口测试**：认证、用户、角色、部门控制器
- **文档**：
  - `docs/使用手册.md`
  - `docs/部署文档.md`

---

### 2026-01-14

#### 1. 通知公告模块完善
- **后端**：标记已读、获取未读数量、我的通知列表
- **前端**：
  - 我的通知页面 `views/system/MyNotice.vue`
  - 未读通知提醒组件 `components/NoticeDropdown.vue`

#### 2. 前端动态路由功能
- **实现**：登录后从后端获取菜单，动态生成路由
- **文件**：
  - `frontend/src/api/menu.js`
  - `frontend/src/utils/route.js`
  - `frontend/src/store/user.js`
  - `frontend/src/router/index.js`

#### 3. 个人中心头像上传
- **功能**：上传头像文件、删除旧头像、更新数据库
- **文件**：
  - `backend/src/main/java/com/base/system/service/UserProfileService.java`
  - `backend/src/main/java/com/base/system/controller/UserProfileController.java`

#### 4. 系统监控模块
- **后端**：服务器信息（CPU/内存/磁盘/JVM）、Redis缓存信息
- **前端**：
  - 服务器监控页面 `views/monitor/Server.vue`
  - 缓存监控页面 `views/monitor/Cache.vue`

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

**后端**：
- 统一响应结构 `Result<T>`
- 全局异常处理器
- JWT 工具类、Redis 工具类
- MyBatis Plus 配置
- Spring Security 集成

**数据库**：
- 用户表、角色表、权限表、部门表
- 枚举表、全局变量表、日志表、通知表
- 初始化数据（admin/test 用户）

**前端**：
- Vue 3 + Vite + Element Plus + Pinia
- Axios 封装、路由配置
- 登录页面、首页、基础布局

**功能模块**：
- 登录认证（验证码、JWT Token、登录失败限制）
- 用户管理（CRUD、重置密码、分配角色）
- 角色管理（CRUD、分配权限）
- 权限/菜单管理（树形结构）
- 部门管理（树形结构）
- 枚举管理（Redis缓存）
- 全局变量管理（Redis缓存）
- 日志管理（操作日志、登录日志）

---

## 开发计划变更记录

| 日期 | 变更内容 |
|------|----------|
| 2026-01-29 | 股票数据分析功能开发完成 |
| 2026-01-26 | 行政区划管理模块、按钮权限控制 |
| 2026-01-14 | 通知公告完善、动态路由、系统监控 |
| 2026-01-12 | 项目基础架构搭建、核心功能模块开发 |
