# 飞书消息发送集成文档

## 功能概述

系统集成了飞书消息发送能力，支持向飞书用户发送文字、图片、文件三种消息类型。通过绑定飞书 open_id 关联系统用户与飞书用户，飞书账号仅用于消息发送，不用于登录系统。

## 配置步骤

### 1. 创建飞书企业自建应用

1. 登录飞书开放平台：https://open.feishu.cn/app
2. 点击 **创建企业自建应用**，填写应用名称和描述
3. 进入应用详情，记录 **App ID** 和 **App Secret**
4. 在 **权限管理** 中添加以下权限：
   - `im:message:send_as_bot` — 以应用身份发送消息
   - `im:resource` — 上传图片和文件资源
5. 发布应用版本，等待管理员审批通过

### 2. 修改后端配置

编辑 `backend/src/main/resources/application-dev.yml`，填入飞书应用凭证：

```yaml
feishu:
  enabled: true                              # 启用飞书集成
  app-id: your-app-id                        # 飞书 App ID
  app-secret: your-app-secret                # 飞书 App Secret
  base-url: https://open.feishu.cn/open-apis # API 基础地址（一般不需要修改）
  timeout: 30000                             # HTTP 请求超时时间（毫秒）
  retry: 3                                   # 请求失败重试次数
```

### 3. 启动服务

启动后端服务后，飞书模块会自动初始化。可通过 Knife4j 文档（http://localhost:8080/doc.html）查看飞书相关接口。

---

## 使用流程

### 绑定飞书账号

```
用户登录系统
    ↓
进入 个人中心 → 第三方账号 标签页
    ↓
点击飞书"绑定"按钮
    ↓
输入飞书 open_id（和可选的飞书用户名）
    ↓
绑定成功，系统可向该用户发送飞书消息
```

### 获取飞书 open_id

飞书 open_id 可通过以下方式获取：
- 飞书开放平台 **事件订阅** 中获取用户事件
- 飞书管理后台 **通讯录** 中查看用户详情
- 调用飞书 API `/contact/v3/users` 查询

### 发送消息

```
方式一：指定飞书 open_id 直接发送
    POST /system/feishu/send

方式二：指定系统 userId，自动查找绑定的 open_id 发送
    POST /system/feishu/send/user/{userId}
```

### 发送图片/文件消息

```
1. 先上传图片/文件到飞书，获取 imageKey / fileKey
   POST /system/feishu/upload/image
   POST /system/feishu/upload/file

2. 再发送消息，content 中携带 imageKey / fileKey
   POST /system/feishu/send
```

---

## API 接口

### 绑定管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/system/feishu/bind` | 绑定飞书 open_id |
| DELETE | `/system/feishu/unbind` | 解绑飞书账号 |
| GET | `/system/feishu/bindInfo` | 查询当前用户飞书绑定信息 |

### 消息发送

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/system/feishu/send` | 发送消息（指定 receiveId） |
| POST | `/system/feishu/send/user/{userId}` | 发送消息给系统用户 |

### 文件上传

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/system/feishu/upload/image` | 上传图片，返回 imageKey |
| POST | `/system/feishu/upload/file` | 上传文件，返回 fileKey |

### 请求/响应示例

**绑定飞书账号**

```
POST /api/system/feishu/bind

{
  "openId": "ou_xxxxxxxxxx",
  "feishuName": "张三"
}

响应：
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

**发送文字消息**

```
POST /api/system/feishu/send

{
  "receiveId": "ou_xxxxxxxxxx",
  "receiveIdType": "open_id",
  "msgType": "text",
  "content": "{\"text\": \"你好，这是一条测试消息\"}"
}

响应：
{
  "code": 200,
  "message": "操作成功",
  "data": "om_xxxxxxxxxx"
}
```

**发送消息给系统用户**

```
POST /api/system/feishu/send/user/1

{
  "receiveId": "",
  "msgType": "text",
  "content": "{\"text\": \"你好，这是一条系统通知\"}"
}
```

**上传图片**

```
POST /api/system/feishu/upload/image
Content-Type: multipart/form-data

file: (图片文件)

响应：
{
  "code": 200,
  "data": {
    "imageKey": "img_xxxxxxxxxx"
  }
}
```

**发送图片消息**

```
POST /api/system/feishu/send

{
  "receiveId": "ou_xxxxxxxxxx",
  "receiveIdType": "open_id",
  "msgType": "image",
  "content": "{\"image_key\": \"img_xxxxxxxxxx\"}"
}
```

**上传文件**

```
POST /api/system/feishu/upload/file
Content-Type: multipart/form-data

file: (文件)
fileType: stream

响应：
{
  "code": 200,
  "data": {
    "fileKey": "file_xxxxxxxxxx"
  }
}
```

**发送文件消息**

```
POST /api/system/feishu/send

{
  "receiveId": "ou_xxxxxxxxxx",
  "receiveIdType": "open_id",
  "msgType": "file",
  "content": "{\"file_key\": \"file_xxxxxxxxxx\"}"
}
```

---

## 消息内容格式

| 消息类型 | msgType | content 格式 |
|----------|---------|-------------|
| 文字消息 | text | `{"text": "消息内容"}` |
| 图片消息 | image | `{"image_key": "img_xxxx"}` |
| 文件消息 | file | `{"file_key": "file_xxxx"}` |

---

## 架构设计

### 模块结构

```
backend/src/main/java/com/base/common/feishu/
├── config/
│   └── FeishuConfig.java                # 飞书应用配置（@ConfigurationProperties）
├── constant/
│   ├── FeishuMsgTypeEnum.java           # 消息类型枚举（text/image/file）
│   └── FeishuReceiveIdTypeEnum.java     # 接收者 ID 类型枚举
├── dto/
│   ├── FeishuBaseResponse.java          # 飞书 API 通用响应基类
│   ├── FeishuSendMessageRequest.java    # 发送消息请求 DTO
│   ├── FeishuSendMessageResponse.java   # 发送消息响应 DTO
│   ├── FeishuUploadImageResponse.java   # 上传图片响应 DTO
│   ├── FeishuUploadFileResponse.java    # 上传文件响应 DTO
│   └── FeishuBindRequest.java           # 绑定飞书请求 DTO
├── handler/
│   ├── FeishuMessageHandler.java        # 消息处理器接口（策略模式核心）
│   ├── TextMessageHandler.java          # 文本消息处理器
│   ├── ImageMessageHandler.java         # 图片消息处理器
│   └── FileMessageHandler.java          # 文件消息处理器
├── client/
│   └── FeishuApiClient.java             # 飞书 API 底层调用封装
└── service/
    ├── FeishuTokenService.java          # Token 管理接口
    ├── FeishuMessageService.java        # 消息发送接口
    └── impl/
        ├── FeishuTokenServiceImpl.java  # Token 实现（Redis 缓存）
        └── FeishuMessageServiceImpl.java # 消息发送实现

backend/src/main/java/com/base/system/controller/
└── FeishuController.java               # REST API 控制器

frontend/src/api/
└── feishu.js                           # 前端 API 封装
```

### 核心类说明

| 类 | 职责 |
|----|------|
| FeishuConfig | 读取 `feishu.*` 配置，注入到各组件 |
| FeishuApiClient | 封装 HTTP 调用，自动注入 tenant_access_token，统一响应解析 |
| FeishuTokenService | Redis 缓存 token，有效期 2 小时，提前 5 分钟刷新，synchronized 防并发 |
| FeishuMessageHandler | 策略接口：`getMsgType()` + `validate()` + `buildContent()` |
| FeishuMessageServiceImpl | 注入所有 Handler 建立路由 Map，按 msgType 分发到对应 Handler |
| FeishuController | REST API，绑定/解绑/发消息/上传 |

### Token 管理机制

```
请求发送消息
    ↓
FeishuTokenService.getTenantAccessToken()
    ↓
┌─ Redis 缓存存在且 TTL > 5分钟 → 直接返回缓存 Token
└─ 缓存不存在或即将过期
        ↓
    synchronized 加锁
        ↓
    双重检查（防止并发重复刷新）
        ↓
    调用飞书 /auth/v3/tenant_access_token/internal
        ↓
    缓存到 Redis（Key: feishu:tenant_access_token）
        ↓
    返回新 Token
```

### 策略模式

```
FeishuMessageServiceImpl 初始化时：
    注入所有 FeishuMessageHandler 实现类
        ↓
    构建 Map<msgType, Handler> 路由表
        ↓
发送消息时：
    根据 msgType 从路由表获取 Handler
        ↓
    handler.validate(content)  → 校验消息内容
        ↓
    handler.buildContent(content) → 构建飞书 API 格式
        ↓
    feishuApiClient.post() → 调用飞书 API
```

---

## 扩展指南

### 新增消息类型

以新增「富文本消息」为例，只需 3 步：

**第一步：添加枚举值**

```java
// FeishuMsgTypeEnum.java
POST("post", "富文本消息"),
```

**第二步：创建 Handler 实现类**

```java
@Component
public class PostMessageHandler implements FeishuMessageHandler {

    @Override
    public FeishuMsgTypeEnum getMsgType() {
        return FeishuMsgTypeEnum.POST;
    }

    @Override
    public void validate(String content) {
        // 校验富文本消息格式
        if (!StringUtils.hasText(content)) {
            throw new BusinessException("富文本消息内容不能为空");
        }
    }

    @Override
    public String buildContent(String content) {
        // 飞书富文本消息格式
        return content;
    }
}
```

**第三步：完成**

无需修改 Service、Controller 或任何其他代码。`FeishuMessageServiceImpl` 会在启动时自动发现并注册新的 Handler。

### 在业务代码中发送消息

```java
@Autowired
private FeishuMessageService feishuMessageService;

// 方式一：通过系统用户 ID 发送
feishuMessageService.sendMessageToUser(userId, "text", "{\"text\": \"订单已发货\"}");

// 方式二：直接指定飞书 open_id 发送
FeishuSendMessageRequest request = new FeishuSendMessageRequest();
request.setReceiveId("ou_xxxxxxxxxx");
request.setReceiveIdType("open_id");
request.setMsgType("text");
request.setContent("{\"text\": \"订单已发货\"}");
feishuMessageService.sendMessage(request);
```

---

## 数据存储

飞书绑定复用现有 `sys_user_oauth` 表，无需新建表：

| 字段 | 值 |
|------|-----|
| oauth_type | `feishu` |
| oauth_id | 飞书 open_id |
| oauth_name | 飞书用户名（可选） |

---

## 常见问题

### 1. 启动报错：飞书相关 Bean 注入失败

确认 `application-dev.yml` 中已添加 `feishu` 配置段（即使 `enabled: false` 也需要配置段存在）。

### 2. 发送消息返回 "app has no permission" 错误

检查飞书应用是否已添加 `im:message:send_as_bot` 权限，且应用版本已发布并审批通过。

### 3. 发送消息返回 "invalid receive_id" 错误

确认 open_id 格式正确（以 `ou_` 开头），且该用户在飞书应用的可见范围内。

### 4. Token 获取失败

检查 app_id 和 app_secret 是否正确，Redis 服务是否正常运行。

### 5. 上传图片/文件失败

确认飞书应用已添加 `im:resource` 权限。图片大小限制 10MB，文件大小限制 30MB。

---

## 文件清单

### 后端

| 文件 | 说明 |
|------|------|
| `common/feishu/config/FeishuConfig.java` | 飞书应用配置 |
| `common/feishu/constant/FeishuMsgTypeEnum.java` | 消息类型枚举 |
| `common/feishu/constant/FeishuReceiveIdTypeEnum.java` | 接收者 ID 类型枚举 |
| `common/feishu/dto/FeishuBaseResponse.java` | 通用响应基类 |
| `common/feishu/dto/FeishuSendMessageRequest.java` | 发送消息请求 DTO |
| `common/feishu/dto/FeishuSendMessageResponse.java` | 发送消息响应 DTO |
| `common/feishu/dto/FeishuUploadImageResponse.java` | 上传图片响应 DTO |
| `common/feishu/dto/FeishuUploadFileResponse.java` | 上传文件响应 DTO |
| `common/feishu/dto/FeishuBindRequest.java` | 绑定请求 DTO |
| `common/feishu/handler/FeishuMessageHandler.java` | 消息处理器接口 |
| `common/feishu/handler/TextMessageHandler.java` | 文本消息处理器 |
| `common/feishu/handler/ImageMessageHandler.java` | 图片消息处理器 |
| `common/feishu/handler/FileMessageHandler.java` | 文件消息处理器 |
| `common/feishu/client/FeishuApiClient.java` | API 调用客户端 |
| `common/feishu/service/FeishuTokenService.java` | Token 管理接口 |
| `common/feishu/service/FeishuMessageService.java` | 消息发送接口 |
| `common/feishu/service/impl/FeishuTokenServiceImpl.java` | Token 实现 |
| `common/feishu/service/impl/FeishuMessageServiceImpl.java` | 消息发送实现 |
| `system/controller/FeishuController.java` | REST API 控制器 |

### 前端

| 文件 | 说明 |
|------|------|
| `frontend/src/api/feishu.js` | 飞书 API 封装 |
| `frontend/src/views/profile/Index.vue` | 个人中心（飞书绑定/解绑） |

### 配置

| 文件 | 说明 |
|------|------|
| `backend/src/main/resources/application-dev.yml` | 飞书配置段 |
