# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 开发命令

### 后端 (backend/)
```bash
cd backend
mvn spring-boot:run              # 启动开发服务器 (端口 8080)
mvn clean install                # 构建项目
mvn test                         # 运行所有测试
mvn test -Dtest=ClassName        # 运行单个测试类
mvn test -Dtest=ClassName#method # 运行单个测试方法
```

### 前端 (frontend/)
```bash
cd frontend
npm install                      # 安装依赖
npm run dev                      # 启动开发服务器 (端口 3000)
npm run build                    # 构建生产版本
npm run preview                  # 预览生产构建
```

### 数据库初始化
```bash
# 执行 SQL 文件初始化数据库
backend/src/main/resources/db/schema.sql  # 表结构
backend/src/main/resources/db/data.sql    # 初始数据
```

## 项目架构

### 整体架构
前后端分离架构，前端通过 `/api` 代理转发请求到后端 8080 端口。

```
base/
├── backend/    # Spring Boot 2.7.18 + MyBatis Plus + Spring Security + JWT
└── frontend/   # Vue 3 + Vite + Element Plus + Pinia
```

### 后端分层架构 (backend/src/main/java/com/base/)

```
com.base/
├── common/           # 公共模块（跨业务复用）
│   ├── annotation/   # @DataScope, @Log 等自定义注解
│   ├── aspect/       # 数据权限切面 DataScopeAspect
│   ├── security/     # XSS过滤、SQL注入防护
│   └── result/       # Result<T> 统一响应封装
├── config/           # 全局配置（Security, Redis, MyBatis, CORS）
├── security/         # JWT 认证过滤器和处理器
└── system/           # 核心业务模块
    ├── controller/   # REST API 控制器
    ├── service/impl/ # 业务逻辑实现
    ├── mapper/       # MyBatis Mapper 接口
    ├── entity/       # 数据库实体（继承 BaseEntity）
    └── dto/          # 请求/响应 DTO
```

### 前端架构 (frontend/src/)

```
src/
├── api/          # API 接口封装（每个模块一个文件）
├── views/        # 页面组件（按模块组织：system/, monitor/, profile/）
├── store/        # Pinia 状态管理（user.js 管理用户和权限）
├── router/       # 路由配置（支持动态路由生成）
├── layout/       # 主布局组件
├── components/   # 公共组件
└── utils/
    ├── request.js  # Axios 封装（自动携带 JWT Token）
    └── route.js    # 动态路由生成工具
```

### 核心机制

**认证流程**: 登录 → 获取 JWT Token 存入 localStorage → 请求自动携带 Authorization Header

**权限控制**: RBAC 模型
- 用户 → 角色 → 权限（菜单/按钮）
- 数据权限通过 `@DataScope` 注解 + MyBatis 拦截器实现

**动态路由**: 登录后从后端获取菜单权限 → 前端 `route.js` 转换为 Vue Router 路由

**操作日志**: 使用 `@OperationLog(module="xxx", operation="xxx")` 注解自动记录

### 关键配置文件

| 文件 | 用途 |
|------|------|
| `backend/src/main/resources/application.yml` | 主配置（激活 dev profile） |
| `backend/src/main/resources/application-dev.yml` | 开发环境（数据库、Redis 连接） |
| `frontend/vite.config.js` | Vite 配置（代理 /api → localhost:8080） |

### API 响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 超级管理员 |
| test | admin123 | 测试用户 |

### API 文档

后端启动后访问: http://localhost:8080/doc.html (Knife4j)
