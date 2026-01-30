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
