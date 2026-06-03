# 飞书集成文档

## 功能概述

系统集成了飞书两大功能：

1. **飞书登录**：通过飞书 OAuth2.0 实现第三方账号登录
2. **飞书消息**：支持向飞书用户发送文字、图片、文件消息

## 配置步骤

### 1. 创建飞书企业自建应用

1. 登录飞书开放平台：https://open.feishu.cn/app
2. 点击 **创建企业自建应用**，填写应用名称和描述
3. 进入应用详情，记录 **App ID** 和 **App Secret**
4. 在 **权限管理** 中添加所需权限：

**用于登录：**
- `contact:user.base:readonly` — 获取用户基本信息
- `contact:user.id:readonly` — 获取用户 user_id

**用于消息发送：**
- `im:message:send_as_bot` — 以应用身份发送消息
- `im:resource` — 上传图片和文件资源

5. 在 **重定向URL** 中添加：`http://119.45.176.101/oauth/callback?platform=feishu`
6. 发布应用版本，等待管理员审批通过

### 2. 修改后端配置

编辑 `backend/src/main/resources/application-prod.yml`：

```yaml
feishu:
  enabled: true                              # 启用飞书集成
  app-id: your-feishu-app-id                 # 飞书 App ID
  app-secret: your-feishu-app-secret         # 飞书 App Secret
  base-url: https://open.feishu.cn/open-apis # API 基础地址
  timeout: 30000                             # HTTP 请求超时（毫秒）
  retry: 3                                   # 请求失败重试次数
  redirect-uri: http://119.45.176.101/oauth/callback?platform=feishu
```

---

## 功能一：飞书登录

### 登录流程

```
用户点击"飞书登录"按钮
    ↓
跳转到飞书授权页面
    ↓
用户在飞书确认授权
    ↓
飞书回调到前端 /oauth/callback?platform=feishu&code=xxx
    ↓
前端携带 code 调用后端 /auth/oauth/feishu/callback
    ↓
┌─ 已绑定账号 → 直接登录，返回 Token
└─ 未绑定账号 → 跳转绑定页面，创建新账号或绑定已有账号
```

### 绑定飞书账号

用户登录系统后，可在 **个人中心 → 第三方账号** 中绑定飞书账号：

```
POST /api/system/feishu/bind
{
  "openId": "ou_xxxxxxxxxx",
  "feishuName": "张三"
}
```

---

## 功能二：飞书消息发送

### 发送消息 API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/system/feishu/send` | 发送消息（指定 receiveId） |
| POST | `/system/feishu/send/user/{userId}` | 根据系统用户ID发送消息 |

### 请求示例

**发送文字消息**

```json
POST /api/system/feishu/send
{
  "receiveId": "ou_xxxxxxxxxx",
  "receiveIdType": "open_id",
  "msgType": "text",
  "content": "{\"text\": \"这是一条系统通知\"}"
}
```

**发送图片消息**

```json
POST /api/system/feishu/send
{
  "receiveId": "ou_xxxxxxxxxx",
  "receiveIdType": "open_id",
  "msgType": "image",
  "content": "{\"image_key\": \"img_xxxxxxxxxx\"}"
}
```

**发送文件消息**

```json
POST /api/system/feishu/send
{
  "receiveId": "ou_xxxxxxxxxx",
  "receiveIdType": "open_id",
  "msgType": "file",
  "content": "{\"file_key\": \"file_xxxxxxxxxx\"}"
}
```

### 上传文件 API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/system/feishu/upload/image` | 上传图片 |
| POST | `/system/feishu/upload/file` | 上传文件 |

---

## 消息内容格式

| 消息类型 | msgType | content 格式 |
|----------|---------|-------------|
| 文字消息 | text | `{"text": "消息内容"}` |
| 图片消息 | image | `{"image_key": "img_xxxx"}` |
| 文件消息 | file | `{"file_key": "file_xxxx"}` |

---

## Token 管理机制

飞书模块使用 Redis 缓存 tenant_access_token：

- 有效期 2 小时
- 提前 5 分钟自动刷新
- synchronized 防并发重复刷新

```
请求发送消息
    ↓
检查 Redis 缓存（Key: feishu:tenant_access_token）
    ↓
┌─ 缓存存在且 TTL > 5分钟 → 直接返回
└─ 缓存不存在或即将过期
        ↓
    synchronized 加锁，双重检查
        ↓
    调用飞书 /auth/v3/tenant_access_token/internal
        ↓
    缓存到 Redis，返回新 Token
```

---

## 架构设计

### 模块结构

```
backend/src/main/java/com/base/
├── common/feishu/
│   ├── config/FeishuConfig.java
│   ├── constant/
│   ├── dto/
│   ├── handler/          # 策略模式：Text/Image/File 处理器
│   ├── client/FeishuApiClient.java
│   └── service/
├── system/controller/
│   └── FeishuController.java
└── system/entity/
    └── UserOauth.java
```

---

## 常见问题

### 1. 启动报错：Bean 注入失败

确认 `application-prod.yml` 中已添加 `feishu` 配置段。

### 2. 发送消息返回 "app has no permission"

检查飞书应用是否已添加 `im:message:send_as_bot` 权限，且应用版本已发布。

### 3. 发送消息返回 "invalid receive_id"

确认 open_id 格式正确（以 `ou_` 开头），且该用户在飞书应用的可见范围内。

### 4. Token 获取失败

检查 app_id 和 app_secret 是否正确，Redis 服务是否正常运行。

### 5. 上传图片/文件失败

确认飞书应用已添加 `im:resource` 权限。图片大小限制 10MB，文件大小限制 30MB。