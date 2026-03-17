# 代码修改记录

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
| 配置变更 | 编辑 / 删除 / 设为生效 后：`AiChatModelHolder.clearCache()`，下次对话用新配置 |
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
