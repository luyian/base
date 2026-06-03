# 业务逻辑变更记录

## 2026-05-28 文件管理模块改造：FastDFS → 腾讯云 COS
- 新建 CosService，从 sys_config 表读取 COS 配置（延迟初始化 COSClient）
- FileServiceImpl 替换全部 FastDFSClient 调用为 CosService
- FileUploadService / FileUploadUtil 替换本地文件存储为 COS 上传
- 删除 FastDFSClient、FastDFSConfig
- 新增 cos_config_init.sql（5 条 COS 配置插入 sys_config 表）

## 2026-05-28 新增候选人配置辅助接口，解决节点属性面板权限不足问题
- WorkflowController 新增 `/workflow/candidates/roles` 和 `/workflow/candidates/departments` 接口，无需系统管理权限
- NodePropertiesPanel 改为调用工作流接口获取角色和部门数据

## 2026-05-28 新增密码变更审批流程（个人中心提交申请，超级管理员审核）
- 新增 SQL 初始化脚本 init_password_change_flow.sql，包含 BPMN XML 流程定义（开始→管理员审核→结束）
- 修复 getMyPendingTasks 支持候选人任务查询（多管理员场景）
- 个人中心"修改密码"标签页增加"密码变更申请（需审批）"区域
- 新增 PasswordChangeHandler 实现审批通过/拒绝回调
- approveTask 方法补充 NodeEventHandler 回调触发逻辑
- 新增流程设计器节点属性面板（NodePropertiesPanel），点击 UserTask 右侧弹出候选人配置和事件回调编辑
- 新增 Flowable Moddle 描述文件，bpmn-js 正确解析 flowable 扩展元素
- CandidateAssignmentTaskListener 和 triggerAfterApprove 支持从 flowable:properties 扩展属性读取配置
- 密码变更流程 BPMN XML 改为使用 flowable:properties 存储候选人和回调配置

## 2026-05-27 集成 Flowable 6.8.1 替换自定义 JSON 工作流引擎

### 后端
- 添加 flowable-spring-boot-starter 6.8.1 依赖（排除 mybatis/liquibase 避免冲突）
- 新增 FlowableConfig 配置类、sys_flowable_definition_ext 扩展表
- 重写 ProcessDefinitionService / ProcessEngineService + Flowable 实现类
- DTO 调整：taskId Long→String，新增 ProcessInstanceResponse / ProcessHistoryResponse
- 新增 CandidateAssignmentTaskListener + FlowableNodeEventListener
- 重构 ProcessContext 去除旧实体依赖，删除旧 strategy 包
- 删除 25 个旧文件（Entity/Mapper/MapperXML/ServiceImpl/Strategy）

### 前端
- 重写 ProcessDesign.vue 为 bpmn.js 可视化拖拽设计器
- 适配 MyTask.vue / MyInitiated.vue 字段名（activityName / processInstanceId 等）
- 新增 bpmn 辅助文件（defaultDiagram.js / zh-CN.js）和 getBpmnXml API

## 2026-05-07 AI 中转服务 (sub2api-lite)

### 新增模块
在 base 项目 backend 下新增 `com.base.relay` 包，实现精简版 AI API 中转服务。

### 核心功能
1. **上游渠道管理**：管理 Anthropic (Claude) 和 OpenAI 的上游 API Key
2. **对外 API Key 生成**：生成 sk- 开头的随机 key，绑定到指定渠道
3. **请求转发**：
   - `POST /v1/messages` → 转发到 Claude
   - `POST /v1/chat/completions` → 转发到 OpenAI
   - `GET /v1/models` → 获取模型列表
4. **请求日志**：记录每次转发的平台、模型、状态码、耗时

### 新增文件

**后端 (Java)**：
- `com.base.relay.enums.PlatformEnum`：平台枚举（ANTHROPIC / OPENAI）
- `com.base.relay.entity.RelayChannel` / `RelayApiKey` / `RelayLog`
- `com.base.relay.mapper.RelayChannelMapper` / `RelayApiKeyMapper` / `RelayLogMapper`
- `com.base.relay.service.ChannelService` / `ApiKeyService` / `RelayLogService` / `GatewayService`
- `com.base.relay.controller.AdminController` / `GatewayController`
- `com.base.relay.dto.RelayApiKeyVO`

**修改文件**：
- `com.base.config.SecurityConfig`：放行 `/v1/**` 路径（网关使用 API Key 认证）

**前端 (Vue3)**：
- `src/views/relay/index.vue`：管理后台入口
- `src/views/relay/components/ChannelManager.vue`：渠道管理
- `src/views/relay/components/ApiKeyManager.vue`：API Key 管理
- `src/views/relay/components/LogViewer.vue`：请求日志

**数据库**：
- 新增 `relay_channel`、`relay_api_key`、`relay_log` 三张表
- 新增 sys_permission 菜单数据（AI中转服务 → 中转管理）
- SQL 文件：`backend/src/main/resources/db/relay_init.sql`

### Bug 修复记录

**2026-05-07 11:20**
1. **MySQL 保留字问题**：`relay_api_key` 表的 `key` 字段是 MySQL 保留字，导致 LambdaQuery 生成的 SQL 语法错误。修复方式：在 `RelayApiKey` 实体类的 `key` 字段上添加 `@TableField("`key`")` 注解。
2. **权限注解不匹配**：`AdminController` 使用 `@PreAuthorize("hasRole('admin')")`，但 base 项目使用 `hasAuthority('permission_code')` 模式，导致管理后台接口返回 403。修复方式：将所有 `hasRole('admin')` 改为对应的 `hasAuthority('relay:channel:list')` 等权限码。

### 自测结果
- 管理后台接口 (`/relay/admin/**`)：已调通，JWT 认证 + 权限校验正常
- 模型列表 (`GET /v1/models`)：已调通，成功返回上游模型列表
- 消息转发 (`POST /v1/messages`)：转发逻辑正常，上游 Kimi API 返回 400（请求格式限制）
- 对话转发 (`POST /v1/chat/completions`)：转发逻辑正常，上游 Kimi API 返回 403（仅支持 Coding Agents 访问）

### 关联影响
- 复用 base 项目的 `sys_user` / `sys_role` / `sys_user_role` 用户体系
- 复用 Spring Security JWT 认证机制（管理后台接口需对应权限）
- 网关接口 `/v1/**` 使用独立的 API Key 认证（从 Authorization Bearer 或 x-api-key header 提取）
