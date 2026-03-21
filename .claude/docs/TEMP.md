# 代码修改记录

## 基金实时估值报价源替换（2026-03-18）

### 需求
iTick API 不能使用，需要使用新的数据源获取股票实时报价。

### 解决方案
使用东方财富免费接口替换 iTick，同时保留 iTick 作为降级数据源。

### 新增文件

| 文件 | 说明 |
|------|------|
| `stock/client/QuoteProvider.java` | 报价提供者接口 |
| `stock/client/impl/EastMoneyQuoteProvider.java` | 东方财富报价实现（主数据源） |
| `stock/client/impl/ITickQuoteProvider.java` | iTick 报价实现（降级数据源，保留原逻辑） |
| `stock/config/QuoteSourceConfig.java` | 报价数据源配置类 |
| `stock/factory/QuoteProviderFactory.java` | 报价提供者工厂，支持降级 |

### 修改文件

| 文件 | 修改内容 |
|------|----------|
| `FundServiceImpl.java` | 使用 QuoteProvider 替换原有的 iTick 直接调用，支持降级逻辑 |
| `application-dev.yml` | 添加 `stock.quote` 配置项 |

### 配置说明

```yaml
stock:
  quote:
    source: eastmoney        # 主数据源：eastmoney / itick
    fallback-enabled: true   # 是否启用降级
    fallback-source: itick   # 降级数据源
```

### 架构设计

```
QuoteProvider (接口)
    │
    ├── EastMoneyQuoteProvider  (主数据源)
    └── ITickQuoteProvider      (降级数据源，保留原有代码)
              │
              ▼
       QuoteProviderFactory
              │
              ▼
         FundServiceImpl
```

---

## AI 模块 API Key 安全增强（2026-03-18）

### 需求
- 前端只能设置 API Key，不能查看具体值
- 编辑时如不填 API Key 则保留原值

### 修改内容

| 文件 | 修改 |
|------|------|
| `SysAiConfigServiceImpl.java` | `getByIdForEdit` 改为返回脱敏值；`update` 方法新增 apiKey 空判断，保留原值；`save` 方法新增 apiKey 非空校验 |
| `SysAiConfigSaveRequest.java` | 移除 apiKey 的 `@NotBlank` 验证（编辑时可选） |

### 逻辑说明
- 列表/详情查询：`toResponse()` 统一脱敏为 `sk-****abcd` 格式
- 新增：apiKey 必填
- 编辑：apiKey 可选，为空时保留原值，有值时更新

---

## AI 模块（当前状态）

### 功能概述

- **首页 AI 助手**：Dashboard 对话气泡、多轮记录、手机端适配；`POST /ai/chat`（message、context），返回 answer。
- **系统管理 - 大模型配置**：支持多条配置，选一条生效；列表 / 新增 / 编辑 / 删除 / 设为生效；默认值：timeout=30000、retry=2、maxMessageLength=2000、maxContextLength=5000。

### 后端要点

| 项 | 说明 |
|----|------|
| 依赖 | LangChain4j + langchain4j-open-ai 0.31（Java 8） |
| 配置来源 | 仅 DB 表 `sys_ai_config`，不再使用 yml |
| 生效配置 | `is_active=1` 且 `status=1` 的一条；**直接查 DB**（Redis 用 Jackson 反序列化无法还原为实体，故未做缓存） |
| 配置变更 | 编辑 / 删除 / 设为生效 后：删除 Redis 键 `ai:config:active` + `AiChatModelHolder.clearCache()`，保证缓存与配置一致 |
| 对话实现 | `AiConfigProvider` 取当前生效配置 → `AiChatModelHolder.getModel()` 得到 `ChatLanguageModel` → `generate(messages)` |

### 前端要点

- **Dashboard**：`api/ai.js` 的 `chat(data)`；多轮气泡、空状态与加载态、错误条。
- **大模型配置页**：`system/AiConfig.vue`；分页列表、表单（含四字段默认值）、「设为生效」；接口：page、list、getById、add、update、delete、setActive。

### 数据与权限

- **表**：`sys_ai_config`（见 `db/sys_ai_config.sql`），含默认一条示例数据。
- **菜单与权限**：`db/ai_config_permission.sql`（菜单 111，query/add/edit/delete），角色 1 已分配。

### 关联

- 安全：需登录；配置页需 `system:ai-config:*`。
- 异常：`AI_NOT_CONFIGURED` / `AI_SERVICE_UNAVAILABLE` 由 `GlobalExceptionHandler` 统一返回。

## remote-git-build.sh（2026-03-18）

- **问题**：`skip` 时未进入项目根目录，`PROJECT_PATH` 为当前 shell 目录；若在上级目录（如 `base-system`）执行，会把空目录挂到容器 `/app`，导致 `ENOENT package.json`。
- **修改**：`skip` 时自动识别项目根（当前目录或 `./base` 下存在 `frontend/package.json`）；构建前校验 `frontend/package.json` 存在；`git clone`/`cd` 对 `PROJECT_DIR` 加引号。

## 前端 Docker 构建 OOM（SIGKILL）（2026-03-18）

- **原因**：小内存机上 `terser` + `vite-plugin-compression` 易导致进程被 OOM Killer 杀死。
- **vite.config.js**：`VITE_LOW_MEM=1` 时使用 `esbuild` 压缩、跳过构建期 gzip；esbuild 下 `drop: console/debugger`。
- **remote-git-build.sh**：前端容器注入 `VITE_LOW_MEM=1`、`NODE_OPTIONS=--max-old-space-size=2048`。
