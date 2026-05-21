# Base 企业级管理系统

基于 Spring Boot 2.7 + Vue 3 + Element Plus 构建的现代化企业级后台管理系统，提供完整的 RBAC 权限管理、股票数据分析、AI 大模型集成、消息推送等功能。

## 技术架构

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.18 | 基础框架 |
| Spring Security | 5.7.x | 认证授权 |
| MyBatis Plus | 3.5.3 | ORM 框架 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 6.0+ | 缓存中间件 |
| JWT | 0.11.5 | Token 认证 |
| LangChain4j | 0.31.0 | AI 大模型集成 |
| Knife4j | 3.0.3 | API 文档 |
| EasyExcel | 3.3.2 | Excel 导出 |
| OSHI | 6.4.0 | 系统监控 |
| Druid | 1.2.16 | 数据库连接池 |

### 前端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.x | 渐进式框架 |
| Vite | 7.2.x | 构建工具 |
| Element Plus | 2.13.x | UI 组件库 |
| Vue Router | 4.6.x | 路由管理 |
| Pinia | 3.0.x | 状态管理 |
| ECharts | 5.5.x | 图表可视化 |
| Axios | 1.13.x | HTTP 客户端 |

## 系统架构

```
┌─────────────────────────────────────────────────────────────────┐
│                         前端 (Vue 3)                            │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐            │
│  │  Pinia  │  │ Router  │  │ ECharts │  │Element+ │            │
│  └─────────┘  └─────────┘  └─────────┘  └─────────┘            │
└───────────────────────────┬─────────────────────────────────────┘
                            │ /api (Vite Proxy)
┌───────────────────────────▼─────────────────────────────────────┐
│                      后端 (Spring Boot)                         │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                    Security Filter                        │  │
│  │              JWT 认证 + 权限校验 + XSS 过滤               │  │
│  └──────────────────────────────────────────────────────────┘  │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐            │
│  │ system  │  │  stock  │  │   ai    │  │ message │            │
│  │ 系统管理 │  │股票分析 │  │ AI对话  │  │消息推送 │            │
│  └─────────┘  └─────────┘  └─────────┘  └─────────┘            │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              Common (公共模块)                            │  │
│  │   数据权限切面 | 操作日志 | 文件上传 | 安全工具           │  │
│  └──────────────────────────────────────────────────────────┘  │
└───────────────────────────┬─────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
   ┌─────────┐        ┌─────────┐        ┌─────────┐
   │  MySQL  │        │  Redis  │        │ 外部API │
   │ 数据存储 │        │  缓存   │        │ iTick等 │
   └─────────┘        └─────────┘        └─────────┘
```

## 项目结构

```
base/
├── backend/                          # 后端项目
│   ├── src/main/java/com/base/
│   │   ├── ai/                       # AI 大模型模块
│   │   │   ├── config/               # LangChain4j 配置
│   │   │   ├── controller/           # AI 对话接口
│   │   │   ├── service/              # AI 服务实现
│   │   │   └── dto/                  # 请求响应对象
│   │   ├── common/                   # 公共模块
│   │   │   ├── annotation/           # @DataScope, @Log 注解
│   │   │   ├── aspect/               # 数据权限切面
│   │   │   ├── interceptor/          # MyBatis 拦截器
│   │   │   └── security/             # XSS/SQL注入防护
│   │   ├── config/                   # 全局配置
│   │   ├── message/                  # 消息推送模块
│   │   │   ├── channel/              # 渠道发送器 (飞书/钉钉/邮件)
│   │   │   ├── content/              # 内容生成器
│   │   │   ├── task/                 # 定时推送任务
│   │   │   └── service/              # 订阅服务
│   │   ├── security/                 # JWT 认证
│   │   ├── stock/                    # 股票分析模块
│   │   │   ├── client/               # 行情数据源 (iTick/东方财富/腾讯)
│   │   │   ├── engine/               # 打分引擎
│   │   │   ├── strategy/             # 10+ 打分策略
│   │   │   ├── task/                 # 数据同步/估值计算任务
│   │   │   └── service/              # 业务服务
│   │   └── system/                   # 系统管理模块
│   │       ├── controller/           # REST 接口
│   │       ├── service/              # 业务逻辑
│   │       ├── mapper/               # 数据访问
│   │       ├── entity/               # 实体类
│   │       └── dto/                  # 数据传输对象
│   └── src/main/resources/
│       ├── mapper/                   # MyBatis XML
│       ├── db/                       # 数据库脚本
│       └── application*.yml          # 配置文件
│
└── frontend/                         # 前端项目
    ├── src/
    │   ├── api/                      # API 接口封装
    │   ├── components/               # 公共组件
    │   ├── directives/               # 自定义指令 (权限控制)
  yout/                   # 布局组件
    │   ├── router/                   # 路由配置
    │   ├── store/                    # Pinia 状态管理
    │   ├── utils/                    # 工具函数
    │   │   ├── request.js            # Axios 封装
    │   │   └── route.js              # 动态路由生成
    │   └── views/                    # 页面组件
    │       ├── system/               # 系统管理页面
    │       ├── stock/                # 股票分析页面
    │       ├── message/              # 消息中心页面
    │       └── monitor/              # 系统监控页面
    └── vite.config.js                # Vite 配置
```

## 核心功能

### 1. 权限管理 (RBAC)

基于角色的访问控制，支持三级权限粒度：

- **菜单权限**：控制用户可访问的菜单
- **按钮权限**：控制用户可执行的操作
- **数据权限**：控制用户可查看的数据范围

数据权限支持 5 种模式：
| 模式 | 说明 |
|------|------|
| 全部数据 | 无限制 |
| 自定义 | 指定部门数据 |
| 本部门 | 仅本部门数据 |
| 本部门及以下 | 本部门及子部门数据 |
| 仅本人 | 仅自己创建的数据 |

通过 `@DataScope` 注解 + MyBatis 拦截器实现，对业务代码无侵入：

```java
@DataScope(deptAlias = "d", userAlias = "u")
public List<User> selectUserList(UserQueryRequest request) {
    return userMapper.selectUserList(request);
}
```

### 2. 动态路由

前端根据用户权限动态生成路由，无需硬编码：

```
登录 → 获取菜单权限 → generateRoutes() → 注册到 Vue Router
```

支持懒加载、路由缓存、面包屑导航。

### 3. 股票数据分析

#### 多数据源支持

| 数据源 | 用途 |
|--------|------|
| iTick API | 港股/A股 K线数据 |
| 东方财富 | 实时行情 |
| 腾讯财经 | 实时行情备用 |

#### 智能推荐引擎

基于策略模式的多规则打分引擎，内置 10+ 技术分析策略：

| 策略 | 说明 |
|------|------|
| MACD 金叉 | DIF 上穿 DEA |
| 均线多头 | MA5 > MA10 > MA20 |
| 均线支撑 | 价格回踩均线 |
| KDJ 金叉 | K 线上穿 D 线 |
| RSI 超卖 | RSI < 30 |
| 布林突破 | 价格突破上轨 |
| 放量上涨 | 成交量放大 + 价格上涨 |
| 连续上涨 | N 日连续收阳 |
| 突破新高 | 创 N 日新高 |

每个策略可独立配置参数、权重、启用状态。

#### 基金估值

根据基金持仓配置，实时计算基金估值涨跌幅：

```
估值涨跌幅 = Σ(持仓股票涨跌幅 × 权重)
```

### 4. AI 大模型集成

基于 LangChain4j 实现，支持多个 AI 提供商：

- 阿里云通义千问
- 智谱 GLM
- MiniMax
- 其他 OpenAI 兼容接口

支持动态切换生效配置，无需重启服务。

### 5. 消息推送

三层解耦架构，支持多渠道、多订阅类型：

```
定时任务触发
    ↓
MessagePushService.executePush(subType)
    ↓
ContentBuilder.build(userId)  →  生成内容
    ↓
ChannelSender.send(userId, message)  →  发送消息
```

扩展方式：
- 新增渠道：实现 `ChannelSender` 接口
- 新增订阅类型：实现 `ContentBuilder` 接口

### 6. 导出配置

可视化配置数据导出，支持：

- 字段映射与排序
- 数据脱敏 (手机号/身份证/银行卡)
- 字典翻译
- 异步导出大数据量
- 多 Sheet 分页

## 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Node.js 16+

### 1. 初始化数据库

```bash
# 创建数据库
mysql -uroot -p -e "CREATE DATABASE base_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 导入表结构和初始数据
mysql -uroot -p base_system < backend/src/main/resources/db/tables.sql
mysql -uroot -p base_system < backend/src/main/resources/db/data.sql
```

### 2. 修改配置

编辑 `backend/src/main/resources/application-dev.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/base_system?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
```

### 3. 启动后端bash
cd backend
mvn spring-boot:run
```

后端服务启动在 `http://localhost:8080`

API 文档：`http://localhost:8080/doc.html`

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端服务启动在 `http://localhost:3000`

### 5. 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 超级管理员 |
| test | admin123 | 测试用户 |

## 开发指南

### 后端开发

```bash
cd backend
mvn spring-boot:run              # 启动开发服务器
mvn clean install                # 构建项目
mvn test                         # 运行测试
mvn test -Dtest=ClassName        # 运行单个测试类
```

### 前端开发

```bash
cd frontend
npm run dev                      # 启动开发服务器
npm run build                    # 构建生产版本
npm run preview                  # 预览生产构建
```

### 添加新模块

1. **后端**：在 `com.base` 下创建新包，按 controller/service/mapper/entity/dto 分层
2. **前端**：在 `src/views` 下创建页面，在 `src/api` 下封装接口
3. **权限**：在菜单管理中配置菜单和按钮权限

### 添加打分策略

1. 实现 `ScoreStrategy` 接口
2. 添加 `@Component` 注解
3. 在数据库 `stk_score_rule` 表中配置规则

```java
@Component
public class MyStrategy implements ScoreStrategy {
    @Override
    public String getStrategyCode() { return "MY_STRATEGY"; }
    
    @Override
    public ScoreResult execute(ScoreContext context) {
        // 实现打分逻辑
    }
}
```

### 添加消息渠道

实现 `ChannelSender` 接口：

```java
@Component
public class EmailChannelSender implements ChannelSender {
    @Override
    public String getChannel() { return "email"; }
    
    @Override
    public void send(Long userId, String message) {
        // 实现发送逻辑
    }
}
```

## 部署

### 生产环境配置

1. 修改 `application-prod.yml` 配置数据库、Redis、JWT 密钥
2. 配置文件上传路径
3. 前端修改 `.env.production` 配置 API 地址

### 构建

```bash
# 后端
cd backend
mvn clean package -DskipTests
java -jar target/base-system-1.0.0.jar --spring.profiles.active=prod

# 前端
cd frontend
npm run b 将 dist 目录部署到 Nginx
```

### Docker 构建 (低内存)

```bash
cd frontend
VITE_LOW_MEM=1 npm run build
```

## 数据库设计

### 核心表

| 表名 | 说明 |
|------|------|
| sys_user | 用户表 |
| sys_role | 角色表 |
| sys_permission | 权限表 |
| sys_dept | 部门表 |
| sys_user_role | 用户角色关联 |
| sys_role_permission | 角色权限关联 |
| sys_role_department | 角色数据权限 |

### 股票模块表

| 表名 | 说明 |
|------|------|
| stk_stock_info | 股票基础信息 |
| stk_kline_daily | 日K线数据 |
| stk_score_rule | 打分规则配置 |
| stk_score_record | 打分记录 |
| stk_recommend | 推荐股票 |
| stk_fund_config | 基金配置 |
| stk_fund_holding | 基金持仓 |
| stk_watchlist | 自选股票 |

### 消息模块表

| 表名 | 说明 |
|------|------|
cription | 消息订阅 |

### 知识库模块表

| 表名 | 说明 |
|------|------|
| kb_knowledge_base | 知识库 |
| kb_document | 文档 |
| kb_directory | 目录 |
| kb_tag | 标签 |

## API 响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

| code | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 500 | 服务器错误 |

## 更新日志

### v1.2.0 (2026-03)

- 新增知识库管理模块
- 新增消息推送模块 (飞书/钉钉/邮件)
- 新增文件管理模块
- 新增大模型配置模块
- 新增导出配置模块
- 优化股票推荐引擎性能

### v1.1.0 (2026-01)

- 新增股票数据分析模块
- 新增行政区划管理
- 新权限控制
- 修复权限数据刷新丢失问题

### v1.0.0 (2026-01)

- 完成用户、角色、权限、部门管理
- 完成枚举、全局变量管理
- 完成操作日志、登录日志管理
- 完成通知公告管理
- 完成数据权限控制
- 完成系统监控功能

## 许可证

MIT License
