# 后台管理系统

一个基于 Spring Boot + Vue 3 的现代化后台管理系统，提供完整的用户权限管理、系统监控、日志管理等功能。

## 项目简介

本项目是一个功能完善的企业级后台管理系统，采用前后端分离架构，提供了完整的 RBAC 权限管理、数据权限控制、系统监控等功能。

### 主要特性

- 🔐 **完善的权限管理**：基于 RBAC 的权限控制，支持菜单权限和按钮权限
- 📊 **数据权限控制**：支持全部数据、本部门、本部门及以下、仅本人、自定义等多种数据权限
- 🎯 **动态路由**：前端根据用户权限动态生成路由和菜单
- 📝 **操作日志**：基于 AOP 的操作日志记录，自动记录用户操作行为
- 🔔 **通知公告**：支持系统通知和公告发布，实时未读提醒
- 📈 **系统监控**：实时监控服务器状态、JVM 信息、Redis 缓存等
- 📉 **股票数据分析**：支持股票数据拉取、K线图展示、自选股票管理
- 🗺️ **行政区划管理**：支持省市区街道四级行政区划数据管理
- 🎨 **现代化 UI**：基于 Element Plus 的美观界面
- 🚀 **高性能**：Redis 缓存、MyBatis Plus 优化、前端懒加载

## 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.x | 基础框架 |
| Spring Security | 5.7.x | 安全框架 |
| MyBatis Plus | 3.5.x | ORM 框架 |
| MySQL | 8.0+ | 数据库 |
| Redis | 6.0+ | 缓存 |
| JWT | 0.11.x | Token 认证 |
| Knife4j | 4.0.x | API 文档 |
| Lombok | 1.18.x | 简化代码 |
| Hutool | 5.8.x | 工具类库 |
| OSHI | 6.4.x | 系统信息采集 |

### 前端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.3.x | 前端框架 |
| Vite | 4.4.x | 构建工具 |
| Element Plus | 2.3.x | UI 组件库 |
| Vue Router | 4.2.x | 路由管理 |
| Pinia | 2.1.x | 状态管理 |
| Axios | 1.5.x | HTTP 客户端 |
| ECharts | 5.5.x | 图表库 |

## 项目结构

```

base/
├── backend/                    # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/base/
│   │   │   │       ├── common/        # 公共模块
│   │   │   │       │   ├── annotation/    # 注解
│   │   │   │       │   ├── aspect/        # 切面
│   │   │   │       │   ├── config/        # 配置类
│   │   │   │       │   ├── enums/         # 枚举
│   │   │   │       │   ├── exception/     # 异常处理
│   │   │   │       │   ├── interceptor/   # 拦截器
│   │   │   │       │   └── util/          # 工具类
│   │   │   │       ├── system/        # 系统模块
│   │   │   │       │   ├── controller/    # 控制器
│   │   │   │       │   ├── dto/           # 数据传输对象
│   │   │   │       │   ├── entity/        # 实体类
│   │   │   │       │   ├── mapper/        # 数据访问层
│   │   │   │       │   └── service/       # 业务逻辑层
│   │   │   │       └── stock/         # 股票模块
│   │   │   │           ├── client/        # API 客户端
│   │   │   │           ├── config/        # 配置类
│   │   │   │           ├── controller/    # 控制器
│   │   │   │           ├── entity/        # 实体类
│   │   │   │           ├── factory/       # 数据工厂
│   │   │   │           ├── mapper/        # 数据访问层
│   │   │   │           ├── service/       # 业务逻辑层
│   │   │   │           └── task/          # 定时任务
│   │   │   └── resources/
│   │   │       ├── mapper/            # MyBatis XML
│   │   │       ├── db/                # 数据库脚本
│   │   │       └── application.yml    # 配置文件
│   │   └── test/                      # 测试代码
│   └── pom.xml                        # Maven 配置
│
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/               # API 接口
│   │   ├── assets/            # 静态资源
│   │   ├── components/        # 公共组件
│   │   ├── directives/        # 自定义指令
│   │   ├── layout/            # 布局组件
│   │   ├── router/            # 路由配置
│   │   ├── store/             # 状态管理
│   │   ├── utils/             # 工具函数
│   │   ├── views/             # 页面组件
│   │   │   ├── system/        # 系统管理页面
│   │   │   ├── monitor/       # 系统监控页面
│   │   │   ├── stock/         # 股票管理页面
│   │   │   └── profile/       # 个人中心页面
│   │   ├── App.vue            # 根组件
│   │   └── main.js            # 入口文件
│   ├── index.html             # HTML 模板
│   ├── package.json           # 依赖配置
│   └── vite.config.js         # Vite 配置
│
└── docs/                       # 文档目录
    ├── 部署文档.md             # 部署文档
    └── 使用手册.md             # 使用手册
```

## 功能模块

### 系统管理

- **用户管理**：用户的增删改查、状态管理、密码重置、角色分配
- **角色管理**：角色的增删改查、权限分配、数据权限配置
- **菜单管理**：菜单的树形管理、菜单权限、按钮权限
- **部门管理**：部门的树形管理、部门层级关系
- **枚举管理**：系统枚举值的管理、Redis 缓存
- **全局变量**：系统参数配置、Redis 缓存
- **行政区划**：省市区街道四级行政区划数据管理

### 股票数据分析

- **股票列表**：股票基础信息查询、市场筛选
- **K线图展示**：基于 ECharts 的 K 线图、MA 均线、成交量
- **自选股票**：自选股票管理、批量同步 K 线数据
- **Token 管理**：API Token 轮询管理、使用统计
- **映射配置**：数据字段映射配置、可视化编辑

### 系统监控

- **服务器监控**：CPU、内存、JVM、磁盘等实时监控
- **缓存监控**：Redis 信息、缓存键管理、缓存清理

### 日志管理

- **操作日志**：记录用户的所有操作行为
- **登录日志**：记录用户的登录信息

### 通知公告

- **通知管理**：通知的发布、编辑、删除
- **我的通知**：查看个人通知、标记已读
- **实时提醒**：顶部栏未读通知提醒

### 个人中心

- **基本信息**：修改个人信息
- **修改密码**：修改登录密码
- **头像上传**：上传个人头像

## 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Node.js 16+

### 后端启动

1. **创建数据库**

```sql
CREATE DATABASE base DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. **导入数据**

执行 `backend/src/main/resources/schema.sql` 和 `data.sql` 文件

3. **修改配置**

编辑 `backend/src/main/resources/application.yml`，修改数据库和 Redis 连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/base?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password

  redis:
    host: localhost
    port: 6379
    password: your_password
```

4. **启动后端**

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

API 文档地址：`http://localhost:8080/doc.html`

### 前端启动

1. **安装依赖**

```bash
cd frontend
npm install
```

2. **启动开发服务器**

```bash
npm run dev
```

前端服务将在 `http://localhost:3000` 启动

3. **构建生产版本**

```bash
npm run build
```

### 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 超级管理员 |
| test | admin123 | 测试用户 |

## 核心功能说明

### 权限控制

系统采用 RBAC（基于角色的访问控制）模型：

- **菜单权限**：控制用户可以访问哪些菜单
- **按钮权限**：控制用户可以执行哪些操作
- **数据权限**：控制用户可以查看哪些数据

数据权限支持以下范围：
- 全部数据权限
- 本部门及以下数据权限
- 本部门数据权限
- 仅本人数据权限
- 自定义数据权限

### 动态路由

前端路由根据用户权限动态生成：

1. 用户登录后，从后端获取菜单权限
2. 前端根据菜单数据动态生成路由
3. 使用 `router.addRoute()` 动态添加路由
4. 侧边栏菜单根据权限动态渲染

### 操作日志

使用 AOP 切面自动记录操作日志：

```java
@OperationLog(module = "用户管理", operation = npublic Result<Void> addUser(@RequestBody UserSaveRequest request) {
    // 业务逻辑
}
```

### 数据权限

使用注解和 MyBatis 拦截器实现数据权限：

```java
@DataScope(deptAlias = "u", userAlias = "u")
public IPage<UserResponse> pageUsers(UserQueryRequest request) {
    // 业务逻辑
}
```

## 开发指南

### 后端开发

1. **创建实体类**：继承 `BaseEntity`
2. **创建 Mapper 接口**：继承 `BaseMapper<T>`
3. **创建 Service 接口和实现类**
4. **创建 Controller**：使用 `@RestController` 注解
5. **添加权限注解**：使用 `@PreAuthorize` 控制接口权限
6. **添加操作日志**：使用 `@OperationLog` 记录操作

### 前端开发

1. **创建 API 接口**：在 `src/api/` 目录下创建
2. **创建页面rc/views/` 目录下创建
3. **配置路由**：在 `src/router/index.js` 中配置（如果是固定路由）
4. **使用状态管理**：在 `src/store/` 目录下创建 store

## 部署说明

详细部署文档请参考：[部署文档](docs/deploy.md)

### 生产环境配置

1. **后端配置**

- 修改 `application-prod.yml` 配置文件
- 配置生产环境的数据库和 Redis
- 配置文件上传路径
- 配置 JWT 密钥

2. **前端配置**

- 修改 `.env.production` 配置文件
- 配置生产环境的 API 地址
- 执行 `npm run build` 构建生产版本

3. **部署方式**

- **后端**：打包成 JAR 文件，使用 `java -jar` 运行
- **前端**：将 `dist` 目录部署到 Nginx

## 常见问题

### 1. 登录后提示 401 未授权

检查 JWT Token 是否正确配置，确保前端请求头中包含 `Authorization` 字段。

### 2. 菜单不显示

检查用户是否分配了角色，角色是否分配了菜单权限。

### 3. 数据权限不生效

检查方法是否添加了 `@DataScope` 注解，确保 MyBatis 拦截器已注册。

### 4. Redis 连接失败

检查 Redis 服务是否启动，配置文件中的连接信息是否正确。

## 更新日志

### v1.1.0 (2026-01-29)

- ✨ 新增股票数据分析模块
  - 股票列表查询（支持港股、A股）
  - K 线图展示（ECharts 实现，支持 MA 均线）
  - 自选股票管理
  - API Token 轮询管理
  - 数据字段映射配置
  - iTick API 数据同步
- ✨ 新增行政区划管理（省市区街道四级）
- ✨ 新增按钮级别权限控制
- 🐛 修复刷新页面后权限数据丢失问题
- 🐛 修复角色权限分配功能

### v1.0.0 (2026-01-14)

- ✨ 完成用户、角色、权限、部门管理
- ✨ 完成枚举、全局变量管理
- ✨ 完成操作日志、登录日志管理
- ✨ 完成通知公告管理
- ✨ 完成个人中心功能
- ✨ 完成数据权限控制
- ✨ 完成系统监控功能
- ✨ 完成前端动态路由

## 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 开源协议

本项目采用 MIT 协议开源。

## 联系方式

如有问题或建议，请提交 Issue 或联系开发团队。

---

**注意**：本项目仅供学习交于商业用途。
