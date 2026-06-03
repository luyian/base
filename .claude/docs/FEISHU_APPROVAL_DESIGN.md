# 飞书审批集成 + 事件回调 + 钉钉接口预留 — 设计文档

> 日期: 2026-06-02
> 状态: 待实施
> 范围: 飞书审批发起、事件回调（WebSocket）、审批模板管理、通讯录同步、钉钉接口抽象

---

## 一、需求背景

base 项目已有飞书基础能力（OAuth 绑定、消息发送、Token 管理），需要在此基础上集成：
1. **飞书审批** — 从业务系统发起飞书审批，跟踪审批状态
2. **事件回调** — WebSocket 长连接接收飞书审批结果、通讯录变更等事件
3. **审批模板** — 配置业务类型到飞书审批表单的映射
4. **通讯录同步** — 从飞书通讯录同步用户到系统
5. **钉钉预留** — 抽象通用接口，飞书实现具体逻辑，钉钉只留接口定义

参考 bzcsolar 项目的飞书集成方案（Play Framework），适配到 Spring Boot + MyBatis Plus 技术栈。

---

## 二、已有飞书模块现状

### 后端 (`com.base.common.feishu`)
| 类 | 职责 |
|----|------|
| `FeishuConfig` | @ConfigurationProperties 绑定 yml (appId, appSecret, baseUrl, timeout, retry, redirectUri) |
| `FeishuApiClient` | HTTP 客户端，自动注入 tenant_access_token，统一响应解析（仅 POST） |
| `FeishuTokenService/Impl` | Redis 缓存 token，提前 5 分钟刷新 |
| `FeishuMessageService/Impl` | 消息发送（策略模式路由 text/image/file） |
| `FeishuMessageHandler` | 消息处理器接口 + 3 个实现 |
| `FeishuController` | OAuth 绑定/解绑 + 消息发送 API |

### 消息推送模块 (`com.base.message`)
| 类 | 职责 |
|----|------|
| `ChannelSender` + `FeishuChannelSender` | 渠道发送器（已有飞书实现） |
| `ChannelEnum` | 已定义 FEISHU/DINGTALK/EMAIL |
| `ContentBuilder` | 内容生成器接口 |
| `MessagePushService` | 推送调度 |
| `Subscription` / `SubscriptionMapper` | msg_subscription 表 |

### 用户绑定
- `UserOauth` 实体 (`sys_user_oauth` 表) — oauthType="feishu", oauthId=open_id
- `UserOauthMapper` — BaseMapper 操作

---

## 三、架构设计

### 3.1 分层架构

```
第三方平台抽象层 (com.base.common.thirdparty)
    ├── ThirdPartyPlatform          ← 平台枚举 (FEISHU, DINGTALK)
    ├── approval/
    │   ├── ThirdPartyApprovalService   ← 审批接口
    │   ├── ApprovalCreateRequest/Response
    │   ├── ApprovalInstanceInfo
    │   └── ApprovalStatusEnum
    ├── event/
    │   ├── ThirdPartyEventService      ← 事件监听接口
    │   ├── ThirdPartyEventHandler      ← 事件处理器接口
    │   └── ThirdPartyEvent             ← 通用事件模型
    └── contact/
        ├── ThirdPartyContactService    ← 通讯录同步接口
        └── ThirdPartyUser

飞书实现层 (com.base.common.feishu)  [扩展已有模块]
    ├── approval/
    │   ├── FeishuApprovalService       ← 实现 ThirdPartyApprovalService
    │   └── FeishuApprovalFormBuilder   ← 表单映射构建
    ├── event/
    │   ├── FeishuWebSocketClient       ← WebSocket 长连接
    │   ├── FeishuEventDispatcher       ← 事件分发（先入库再处理）
    │   ├── FeishuEventService          ← 实现 ThirdPartyEventService
    │   └── handler/
    │       ├── FeishuApprovalEventHandler   ← 审批状态变更
    │       └── FeishuContactEventHandler    ← 通讯录变更
    └── contact/
        └── FeishuContactService        ← 实现 ThirdPartyContactService

审批业务层 (com.base.approval)  [新增模块]
    ├── entity/     ApprovalInstance, ApprovalTemplate, EventCallbackLog
    ├── enums/      ApprovalInstanceStatus, EventCallbackStatus
    ├── mapper/     3 个 Mapper
    ├── dto/        请求/响应 DTO
    ├── service/    ApprovalInstanceService, ApprovalTemplateService, EventCallbackService
    ├── controller/ ApprovalController, ApprovalTemplateController, EventLogController
    └── task/       EventCallbackRetryTask, ApprovalStatusSyncTask
```

### 3.2 系统交互流程

```
┌──────────────────────────────────────────────────────────────────┐
│                          业务系统                                 │
│                                                                  │
│  ┌─────────────┐    ┌──────────────────┐    ┌────────────────┐  │
│  │ 业务代码     │───→│ ApprovalInstance │───→│ FeishuApproval │  │
│  │ (发起审批)   │    │ Service          │    │ Service        │──┼──→ 飞书 API
│  └─────────────┘    └──────────────────┘    └────────────────┘  │
│                                                                  │
│  ┌─────────────┐    ┌──────────────────┐    ┌────────────────┐  │
│  │ 飞书 WS     │←───│ FeishuEvent      │───→│ EventCallback  │  │
│  │ 推送事件     │    │ Dispatcher       │    │ Service        │  │
│  └─────────────┘    └──────┬───────────┘    └────────────────┘  │
│                            │                                     │
│                    ┌───────▼───────────┐                         │
│                    │ ApprovalEvent     │                         │
│                    │ Handler           │──→ 更新审批实例状态       │
│                    └───────────────────┘                         │
└──────────────────────────────────────────────────────────────────┘
```

---

## 四、数据库设计

### 4.1 审批模板表 (tp_approval_template)

```sql
CREATE TABLE IF NOT EXISTS `tp_approval_template` (
    `id`                     BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `template_code`          VARCHAR(100)    NOT NULL                 COMMENT '模板编码（业务类型标识）',
    `template_name`          VARCHAR(200)    NOT NULL                 COMMENT '模板名称',
    `platform`               VARCHAR(20)     NOT NULL DEFAULT 'feishu' COMMENT '第三方平台（feishu/dingtalk）',
    `platform_approval_code` VARCHAR(200)    DEFAULT NULL             COMMENT '平台审批定义编码',
    `form_mapping`           TEXT            DEFAULT NULL             COMMENT '表单字段映射配置（JSON）',
    `description`            VARCHAR(500)    DEFAULT NULL             COMMENT '模板说明',
    `status`                 TINYINT         NOT NULL DEFAULT 1       COMMENT '状态（0-禁用 1-启用）',
    `create_by`              VARCHAR(64)     DEFAULT NULL             COMMENT '创建人',
    `create_time`            DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`              VARCHAR(64)     DEFAULT NULL             COMMENT '更新人',
    `update_time`            DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`                TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_template_code_platform` (`template_code`, `platform`, `deleted`),
    INDEX `idx_platform` (`platform`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='第三方审批模板表';
```

**form_mapping 字段格式**:
```json
[
  {"controlId": "widget1", "controlType": "input",  "businessField": "reason"},
  {"controlId": "widget2", "controlType": "date",   "businessField": "startDate"},
  {"controlId": "widget3", "controlType": "amount", "businessField": "amount", "currency": "CNY"}
]
```

### 4.2 审批实例表 (tp_approval_instance)

```sql
CREATE TABLE IF NOT EXISTS `tp_approval_instance` (
    `id`                    BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `template_id`           BIGINT          NOT NULL                 COMMENT '关联模板ID',
    `template_code`         VARCHAR(100)    NOT NULL                 COMMENT '模板编码（冗余）',
    `platform`              VARCHAR(20)     NOT NULL DEFAULT 'feishu' COMMENT '第三方平台',
    `platform_instance_id`  VARCHAR(200)    DEFAULT NULL             COMMENT '平台审批实例ID',
    `business_key`          VARCHAR(200)    NOT NULL                 COMMENT '业务主键',
    `business_type`         VARCHAR(100)    DEFAULT NULL             COMMENT '业务类型',
    `title`                 VARCHAR(500)    DEFAULT NULL             COMMENT '审批标题',
    `applicant_user_id`     BIGINT          NOT NULL                 COMMENT '发起人系统用户ID',
    `applicant_open_id`     VARCHAR(100)    DEFAULT NULL             COMMENT '发起人平台用户ID',
    `form_data`             TEXT            DEFAULT NULL             COMMENT '提交的表单数据（JSON）',
    `status`                VARCHAR(30)     NOT NULL DEFAULT 'PENDING' COMMENT '审批状态',
    `platform_status`       VARCHAR(50)     DEFAULT NULL             COMMENT '平台原始状态',
    `result_comment`        VARCHAR(1000)   DEFAULT NULL             COMMENT '审批结果说明',
    `idempotent_key`        VARCHAR(200)    DEFAULT NULL             COMMENT '幂等键',
    `submitted_at`          DATETIME        DEFAULT NULL             COMMENT '提交时间',
    `completed_at`          DATETIME        DEFAULT NULL             COMMENT '完成时间',
    `create_by`             VARCHAR(64)     DEFAULT NULL             COMMENT '创建人',
    `create_time`           DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`             VARCHAR(64)     DEFAULT NULL             COMMENT '更新人',
    `update_time`           DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`               TINYINT         NOT NULL DEFAULT 0       COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_idempotent` (`idempotent_key`),
    UNIQUE KEY `uk_platform_instance` (`platform`, `platform_instance_id`),
    INDEX `idx_template_code` (`template_code`),
    INDEX `idx_business_key` (`business_key`),
    INDEX `idx_applicant` (`applicant_user_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='第三方审批实例表';
```

### 4.3 事件回调日志表 (tp_event_callback_log)

```sql
CREATE TABLE IF NOT EXISTS `tp_event_callback_log` (
    `id`                    BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `platform`              VARCHAR(20)     NOT NULL                 COMMENT '来源平台',
    `event_id`              VARCHAR(200)    DEFAULT NULL             COMMENT '事件唯一ID（去重用）',
    `event_type`            VARCHAR(100)    NOT NULL                 COMMENT '事件类型',
    `event_payload`         LONGTEXT        NOT NULL                 COMMENT '事件原始内容（完整JSON）',
    `status`                VARCHAR(20)     NOT NULL DEFAULT 'PENDING' COMMENT '处理状态',
    `retry_count`           INT             NOT NULL DEFAULT 0       COMMENT '已重试次数',
    `max_retry`             INT             NOT NULL DEFAULT 3       COMMENT '最大重试次数',
    `error_message`         VARCHAR(2000)   DEFAULT NULL             COMMENT '处理失败原因',
    `processed_at`          DATETIME        DEFAULT NULL             COMMENT '处理完成时间',
    `next_retry_at`         DATETIME        DEFAULT NULL             COMMENT '下次重试时间',
    `create_time`           DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_event_id` (`platform`, `event_id`),
    INDEX `idx_event_type` (`event_type`),
    INDEX `idx_status` (`status`),
    INDEX `idx_next_retry` (`status`, `next_retry_at`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='第三方事件回调日志表';
```

---

## 五、接口抽象设计

### 5.1 ThirdPartyPlatform 枚举

```java
public enum ThirdPartyPlatform {
    FEISHU("feishu", "飞书"),
    DINGTALK("dingtalk", "钉钉");

    private final String code;
    private final String desc;
}
```

### 5.2 审批服务接口

```java
public interface ThirdPartyApprovalService {
    ThirdPartyPlatform getPlatform();
    ApprovalCreateResponse createApproval(ApprovalCreateRequest request);
    void cancelApproval(String platformInstanceId, String approvalCode, String openId);
    ApprovalInstanceInfo getApprovalStatus(String platformInstanceId);
}
```

### 5.3 事件服务接口

```java
public interface ThirdPartyEventService {
    ThirdPartyPlatform getPlatform();
    void startListening();
    void stopListening();
    boolean isConnected();
}

public interface ThirdPartyEventHandler {
    String getEventType();
    ThirdPartyPlatform getPlatform();
    void handle(ThirdPartyEvent event);
}
```

### 5.4 通讯录同步接口

```java
public interface ThirdPartyContactService {
    ThirdPartyPlatform getPlatform();
    int syncAllUsers();
    ThirdPartyUser getUserByOpenId(String openId);
}
```

### 5.5 通用审批状态枚举

```java
public enum ApprovalStatusEnum {
    PENDING("PENDING", "审批中"),
    APPROVED("APPROVED", "已通过"),
    REJECTED("REJECTED", "已拒绝"),
    CANCELED("CANCELED", "已撤回"),
    DELETED("DELETED", "已删除");
}
```

---

## 六、飞书实现层详细设计

### 6.1 FeishuConfig 扩展

在现有 FeishuConfig 中新增 3 个字段：
```java
private String verificationToken;   // 事件验证令牌
private String encryptKey;          // 事件加密密钥
private Boolean eventEnabled = false; // 是否启用 WebSocket 事件监听
```

### 6.2 FeishuApiClient 扩展

新增 GET 方法（当前仅有 POST）：
```java
public JSONObject get(String path) { ... }
public JSONObject get(String path, Map<String, Object> params) { ... }
```
复用已有 `buildUrl()`、`buildHeaders()`、`checkResponse()`，调用 `HttpClientUtil.getWithRetry()`。

### 6.3 FeishuApprovalService

```java
@Service
public class FeishuApprovalService implements ThirdPartyApprovalService {
    // createApproval()  → POST /approval/v4/instances
    // cancelApproval()  → POST /approval/v4/instances/cancel
    // getApprovalStatus() → GET /approval/v4/instances/{instance_id}
}
```

### 6.4 FeishuApprovalFormBuilder

根据模板 `formMapping` JSON 配置，将业务数据 Map 转换为飞书审批表单格式：
```java
public class FeishuApprovalFormBuilder {
    public static String buildForm(String formMapping, Map<String, Object> businessData) { ... }
}
```
支持控件类型: input/textarea/number/amount/date/radio/checkbox

### 6.5 FeishuWebSocketClient

飞书 WebSocket 长连接核心组件：
1. POST `/callback/ws/endpoint` 获取 WebSocket URL
2. Java-WebSocket 库建立连接
3. onMessage → 交给 FeishuEventDispatcher
4. onClose → 自动重连（30 秒延迟）

### 6.6 FeishuEventDispatcher

效仿已有 `NodeEventHandlerManager` 的策略模式：
```java
@Component
public class FeishuEventDispatcher {
    @PostConstruct
    public void init() {
        // 收集 platform=FEISHU 的 ThirdPartyEventHandler，建 Map
    }

    public void dispatch(String rawMessage) {
        // 1. 解析 eventId/eventType
        // 2. 先入库 (EventCallbackService.saveEvent)
        // 3. 查找 handler → handle
        // 4. 标记成功或失败
    }
}
```

**核心原则：先入库再处理，保证事件不丢失。**

### 6.7 FeishuEventService

```java
@Service
public class FeishuEventService implements ThirdPartyEventService {
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        if (feishuConfig.getEnabled() && feishuConfig.getEventEnabled()) {
            startListening();
        }
    }
}
```

### 6.8 事件处理器

**FeishuApprovalEventHandler** — 处理 `approval_instance` 事件：
- 解析 instance_code + status → ApprovalInstanceService.updateStatusFromCallback()

**FeishuContactEventHandler** — 处理 `contact.user.updated` 事件：
- 更新 sys_user_oauth 中的用户信息

### 6.9 FeishuContactService

```java
@Service
public class FeishuContactService implements ThirdPartyContactService {
    // syncAllUsers(): GET /contact/v3/users 分页拉取 → upsert sys_user_oauth
    // getUserByOpenId(): GET /contact/v3/users/{open_id}
}
```

---

## 七、审批业务层

### 7.1 核心流程 — submitApproval

```
1. 根据 templateCode 查询启用的模板
2. 幂等检查（idempotentKey = templateCode:businessKey）
3. UserOauthMapper 查发起人 open_id
4. FeishuApprovalFormBuilder 构建表单
5. 入库 ApprovalInstance（status=PENDING）
6. 调用 ThirdPartyApprovalService.createApproval()
7. 回写 platformInstanceId
```

### 7.2 核心流程 — 事件回调更新

```
1. WebSocket 接收飞书事件
2. FeishuEventDispatcher 解析并入库
3. FeishuApprovalEventHandler 被触发
4. 解析 instance_code + status
5. ApprovalInstanceService.updateStatusFromCallback()
6. 幂等更新审批实例状态
```

### 7.3 定时任务

| 任务 | 频率 | 职责 |
|------|------|------|
| EventCallbackRetryTask | 每 5 分钟 | 重试 FAILED 且未超限的事件 |
| ApprovalStatusSyncTask | 每 30 分钟 | 主动拉取 PENDING 实例状态（兜底） |

### 7.4 API 接口

**ApprovalController** `/approval`:

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/submit` | 发起审批 |
| POST | `/{id}/cancel` | 撤销审批 |
| GET | `/list` | 分页查询审批实例 |
| GET | `/{id}` | 审批详情 |
| GET | `/business/{businessKey}` | 按业务键查询 |
| POST | `/{id}/sync` | 手动同步状态 |

**ApprovalTemplateController** `/approval/template`:

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/list` | 模板列表 |
| GET | `/{id}` | 模板详情 |
| POST | `` | 新增模板 |
| PUT | `/{id}` | 更新模板 |
| DELETE | `/{id}` | 删除模板 |

**EventLogController** `/approval/event-log`:

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/list` | 事件日志分页 |
| GET | `/ws-status` | WebSocket 连接状态 |
| POST | `/retry/{id}` | 手动重试事件 |

---

## 八、前端设计

### 8.1 API 文件
- `api/approval.js` — 审批实例 CRUD + 发起/撤销/同步
- `api/approvalTemplate.js` — 模板管理 + 事件日志

### 8.2 页面

| 页面 | 路由 | 功能 |
|------|------|------|
| `views/approval/index.vue` | `/approval` | 审批实例列表（筛选+表格+发起弹窗） |
| `views/approval/detail.vue` | `/approval/detail/:id` | 审批详情 |
| `views/approval/template/index.vue` | `/approval/template` | 模板管理 CRUD |
| `views/approval/eventLog/index.vue` | `/approval/event-log` | 事件日志（WS 状态灯+重试） |

### 8.3 路由
在 `frontend/src/router/index.js` Layout children 中 message 路由之后新增 4 条路由。

---

## 九、依赖新增

`backend/pom.xml`:
```xml
<!-- Java-WebSocket 客户端（飞书 WebSocket 长连接） -->
<dependency>
    <groupId>org.java-websocket</groupId>
    <artifactId>Java-WebSocket</artifactId>
    <version>1.5.4</version>
</dependency>
```

选择理由：本场景只需 WebSocket **客户端**连飞书服务器，不需要在本应用启动 WebSocket 服务端。Java-WebSocket 是纯客户端库，轻量，不引入额外的 Spring WebSocket 自动配置。

---

## 十、需要修改的现有文件

| 文件 | 改动 |
|------|------|
| `backend/pom.xml` | +Java-WebSocket 依赖 |
| `com.base.common.feishu.config.FeishuConfig` | +3 个事件配置字段 |
| `com.base.common.feishu.client.FeishuApiClient` | +2 个 GET 方法 |
| `application-dev.yml` | +feishu 事件配置 |
| `frontend/src/router/index.js` | +4 条路由 |

---

## 十一、新增文件统计

| 类别 | 数量 | 代表文件 |
|------|------|---------|
| SQL | 1 | `db/schema_approval.sql` |
| 抽象接口+DTO | ~10 | `ThirdPartyApprovalService`, `ThirdPartyEventService` 等 |
| 飞书实现 | ~7 | `FeishuApprovalService`, `FeishuWebSocketClient`, `FeishuEventDispatcher` 等 |
| 审批业务 | ~15 | entity(3) + mapper(3) + service(6) + controller(3) |
| 定时任务 | 2 | `EventCallbackRetryTask`, `ApprovalStatusSyncTask` |
| 前端 API | 2 | `approval.js`, `approvalTemplate.js` |
| 前端页面 | 4 | `approval/index.vue` 等 |
| **总计** | **~41** | |

---

## 十二、实施顺序

1. **基础设施** — pom + config 扩展 + DDL + ApiClient GET
2. **抽象接口层** — thirdparty 包所有接口和 DTO
3. **审批 entity/mapper/enums**
4. **飞书审批实现** — FeishuApprovalService + FormBuilder
5. **审批业务服务** — ApprovalTemplateService + ApprovalInstanceService
6. **审批控制器** — ApprovalController + TemplateController
7. **事件回调服务** — EventCallbackService
8. **WebSocket 客户端** — FeishuWebSocketClient + EventDispatcher + EventService
9. **事件处理器** — ApprovalEventHandler + ContactEventHandler
10. **事件日志控制器 + 定时任务** — EventLogController + RetryTask + SyncTask
11. **通讯录同步** — FeishuContactService
12. **前端** — API + 页面 + 路由

---

## 十三、关键设计决策

### WebSocket vs HTTP 回调
选择 WebSocket 长连接：无需暴露公网地址（开发和内网部署均方便），实时性优于轮询，飞书官方推荐新应用使用长连接。

### "先入库再处理"的事件模式
所有事件先写入 `tp_event_callback_log`，再分发处理。保证：事件不丢失、处理失败可重试、完整审计日志。幂等性由 `(platform, event_id)` 唯一键保证。

### 审批幂等保障
- 应用层：`idempotent_key` UK（默认 `templateCode:businessKey`）
- 飞书层：`uuid` 参数传递相同幂等键

### 钉钉预留策略
所有第三方能力通过 `com.base.common.thirdparty` 接口定义。未来添加钉钉只需：
- 创建 `com.base.common.dingtalk` 包实现各接口
- 已有 `ChannelEnum` 已含 DINGTALK，`ChannelSender` 已为钉钉预留

### 与 Flowable 工作流的关系
飞书审批与 Flowable 是两条独立的审批通道。Flowable 用于内部复杂流程编排，飞书审批用于在飞书客户端操作的场景。两者可协同：在 Flowable 的 NodeEventHandler 中触发飞书审批，通过回调推进 Flowable 流程。

---

## 十四、验证方式

1. 后端启动无报错，Knife4j 文档可访问新增 API
2. 审批模板 CRUD 接口联调
3. 发起审批 → 飞书收到审批 → 审批通过/拒绝 → WebSocket 回调 → 实例状态更新
4. WebSocket 断连后自动重连
5. 幂等测试：重复发起同一 businessKey 不会创建新实例
6. 事件日志页面可查看回调记录，手动重试功能正常
