# Dashboard AI 助手 + 后端 AI 支持（baseURL/apiKey 可配置）- 需求文档

版本：v1.0

日期：2026-03-16

## 一、需求背景

当前 `Dashboard` 页面为静态演示数据（无真实接口联动），缺少“快速获取帮助/解释数据/生成操作建议”的入口。

希望在首页（Dashboard）增加 AI 助手能力，并在后端提供统一 AI 代理接口，支持通过 `baseURL`、`apiKey` 进行配置（兼容 OpenAI API 风格的服务）。

## 二、目标

1. 在 `Dashboard` 页面提供 AI 对话入口（提问 -> 返回回答），降低用户学习成本。
2. 后端提供 AI 统一调用接口，屏蔽第三方差异，前端仅依赖本系统 API。
3. AI 服务可配置：至少支持 `baseURL`、`apiKey`（建议同时支持 `model`、`timeout`、`retry`）。
4. 不暴露 `apiKey` 给普通用户，不在前端直连第三方。

## 三、范围

### 3.1 本期（MVP）范围

- 前端：`Dashboard` 新增“AI 助手”卡片，支持输入问题、提交、展示回答、加载态与错误提示。
- 后端：新增 AI Controller + Service，提供一次性（非流式）对话接口。
- 配置：支持 `baseURL`、`apiKey` 配置并生效（默认推荐后端配置注入 + 环境变量）。

### 3.2 非本期范围（后续迭代）

- 流式输出（SSE/WebSocket）。
- 多轮会话持久化（对话历史存储、会话列表）。
- 工具调用（function calling）、RAG 知识库、权限敏感数据自动脱敏。
- 管理后台“在线配置 apiKey（可回显/可编辑）”。

## 四、术语与约定

- OpenAI-compatible：指支持 `/v1/chat/completions` 等接口风格的服务。
- baseURL：第三方 AI 服务的基础地址（如 `https://xxx.com/v1` 或 `https://xxx.com`）。
- apiKey：第三方 AI 服务鉴权密钥，仅后端可见。

## 五、现状说明（对接点）

- 前端 Dashboard：`frontend/src/views/Dashboard.vue`（当前为静态数据，无接口调用）。
- 前端请求封装：`frontend/src/utils/request.js`（`baseURL: '/api'`，自动携带 JWT）。
- 后端对外 HTTP 封装：`backend/src/main/java/com/base/common/util/HttpClientUtil.java`（Hutool，支持超时/重试）。
- 后端配置模式：大量使用 `@ConfigurationProperties`（参考 `FeishuConfig`、`ITickConfig`）。

## 六、总体方案

### 6.1 前端方案（Dashboard AI 卡片）

在 `Dashboard` 页面增加一块独立卡片：

- 输入区：问题输入框（支持多行）、“发送”按钮
- 输出区：展示 AI 返回文本（支持换行）
- 状态：发送中禁用按钮、展示 loading
- 错误：提示“未配置/调用失败/服务不可用”等

可选增强：将 Dashboard 上的“最近登录/系统通知”拼成 `context` 传给后端，作为回答上下文。

### 6.2 后端方案（AI 代理接口）

后端新增 AI 模块（Controller/Service/DTO），由后端向第三方 AI 服务发起请求：

- 统一接口：`POST /ai/chat`
- 后端拼装第三方请求并返回简化后的回答文本
- 禁止回传第三方原始响应头与敏感信息

## 七、功能需求

### 7.1 前端功能

1. 入口位置：Dashboard 页面显著位置（建议在现有两行卡片/表格下方新增一行卡片）。
2. 交互：
   - Enter 提交（可配置：Enter 发送，Shift+Enter 换行）
   - 发送后清空或保留输入（默认保留，便于再次编辑）
3. 展示：
   - 显示最近一次回答
   - 支持复制回答文本
4. 错误提示：
   - 未配置（后端返回特定错误码/消息）
   - 请求失败/超时/服务不可用

### 7.2 后端功能

1. 提供 AI 对话接口：接收 message/context，返回 answer。
2. 配置校验：
   - 未配置 `baseURL` 或 `apiKey` 时，拒绝调用并返回可读错误信息。
3. 超时与重试：
   - 支持配置 `timeout`、`retry`。
4. 安全：
   - 不记录 `apiKey` 到日志
   - 对用户输入做长度限制，避免滥用
5.（可选）权限：
   - 最小实现：只要求登录态即可（`SecurityConfig` 已默认所有接口需认证）
   - 推荐实现：增加细粒度权限 `dashboard:ai:chat`，前端按钮通过 `v-permission` 控制。

## 八、配置需求

### 8.1 推荐配置方式（安全优先）

后端采用 `@ConfigurationProperties(prefix = "ai")` 读取配置：

- `ai.enabled`：是否启用（默认 `false`）
- `ai.base-url`：第三方基础地址
- `ai.api-key`：第三方密钥（建议仅通过环境变量注入，例如 `${AI_API_KEY:}`）
- `ai.model`：默认模型（可选）
- `ai.timeout`：超时（毫秒）
- `ai.retry`：重试次数

说明：`apiKey` 不建议写入仓库的 `application-*.yml`，以免泄露；部署时通过环境变量/密钥管理注入。

### 8.2 允许的替代方案（运行期可变）

如果必须“运行期可修改 baseURL”，可将 `ai.base-url` 放入 `sys_config`（只放非敏感项）：

- `ai.baseUrl`：第三方基础地址（允许管理员在“全局变量”中修改）

强约束：`apiKey` 仍不建议放入 `sys_config`，原因：

- `GET /system/config/key/{configKey}` 当前缺少权限注解，登录用户可读取任意 key；
- 前端配置管理页面会明文展示 `configValue`。

## 九、接口设计（后端）

### 9.1 POST /ai/chat

请求（JSON）：

- `message`：必填，用户问题
- `context`：选填，上下文文本（例如 dashboard 的摘要信息）

响应（统一 Result）：

- `code`：200 成功；非 200 表示失败
- `message`：提示信息
- `data`：
  - `answer`：AI 返回文本

示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "answer": "..."
  }
}
```

### 9.2 错误码/错误消息约定

- 参数缺失/超长：`400` + 明确提示
- AI 未启用/未配置：建议 `400` 或 `503` + 明确提示（如“AI 未配置 baseURL/apiKey”）
- 第三方鉴权失败/限流：建议 `503`（或映射为业务码）+ 明确提示

## 十、非功能需求

1. 性能：单次对话响应时间（不含第三方排队）建议 < 10s。
2. 稳定性：第三方失败不影响系统其他模块；错误返回可读。
3. 安全：
   - `apiKey` 不落前端、不输出日志、不通过任何配置查询接口暴露
   - 防滥用：限制 `message` 长度（例如 1~2000 字符），必要时加简单频控
4. 可观测性：
   - 记录调用耗时、第三方状态码、失败原因（不包含密钥与完整 prompt）

## 十一、风险与对策

1. `sys_config` 存放 apiKey 泄露风险：默认禁止，仅允许环境变量/密钥管理。
2. 第三方接口差异：以“OpenAI-compatible”作为约束；对非兼容服务需额外适配层。
3. 成本与滥用：建议后续加入配额/限流、审计。
4. 全局异常/Result 混用导致错误格式不一致：AI 接口建议统一使用 `com.base.common.result.Result` 与对应异常体系。

## 十二、验收标准

1. Dashboard 页面出现 AI 助手入口，可提交问题并看到回答。
2. 后端提供 AI 对话接口，前端通过系统 API 调用成功。
3. 修改 `baseURL`、`apiKey` 后可生效（按选定配置方式：重启生效或运行期生效）。
4. `apiKey` 不在前端页面/接口响应/日志中泄露。
5. 未配置时提示明确，且不影响其他功能正常使用。
