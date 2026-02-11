# GitHub OAuth 第三方登录说明文档

## 功能概述

系统支持通过 GitHub 账号进行第三方登录。首次使用 GitHub 登录时，用户可以选择"创建新账号"或"绑定已有账号"。已绑定的用户再次使用 GitHub 登录时将直接进入系统。

## 配置步骤

### 1. 创建 GitHub OAuth App

1. 登录 GitHub，进入 **Settings → Developer settings → OAuth Apps → New OAuth App**
   - 直接访问：https://github.com/settings/developers
2. 填写以下信息：
   - **Application name**：自定义应用名称，如 `Base System`
   - **Homepage URL**：`http://localhost:3000`
   - **Authorization callback URL**：`http://localhost:3000/oauth/callback`
3. 点击 **Register application** 创建应用
4. 创建完成后记录 **Client ID**，点击 **Generate a new client secret** 生成并记录 **Client Secret**

### 2. 初始化数据库

执行 SQL 脚本创建第三方登录绑定表：

```sql
-- 文件路径：backend/src/main/resources/db/oauth_schema.sql
source backend/src/main/resources/db/oauth_schema.sql
```

### 3. 修改后端配置

编辑 `backend/src/main/resources/application-dev.yml`，填入 GitHub OAuth App 的凭证：

```yaml
oauth:
  github:
    client-id: 你的Client ID
    client-secret: 你的Client Secret
    redirect-uri: http://localhost:3000/oauth/callback
  default-role-id: 2  # 新用户默认角色ID
```

### 4. 启动服务

分别启动后端和前端服务，在登录页面即可看到 GitHub 登录按钮。

## 使用流程

### 登录流程

```
用户点击 "GitHub 登录" 按钮
    ↓
跳转到 GitHub 授权页面
    ↓
用户在 GitHub 上确认授权
    ↓
GitHub 回调到前端 /oauth/callback 页面
    ↓
前端携带 code + state 调用后端接口
    ↓
┌─ 已绑定账号 → 直接生成 JWT Token → 进入系统
└─ 未绑定账号 → 跳转绑定页面
                    ├─ 创建新账号：一键创建并登录
                    └─ 绑定已有账号：输入用户名密码验证后绑定
```

### 个人中心管理

登录后在 **个人中心 → 第三方账号** 标签页中可以：
- 查看已绑定的第三方账号
- 解绑已绑定的第三方账号
- 绑定新的第三方账号

## API 接口

### 无需认证的接口（/auth/oauth）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/auth/oauth/github/url` | 获取 GitHub 授权地址 |
| POST | `/auth/oauth/github/callback` | GitHub 回调处理 |
| POST | `/auth/oauth/bindNew` | 创建新账号并绑定 |
| POST | `/auth/oauth/bindExist` | 绑定已有账号 |

### 需要认证的接口（/system/oauth）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/system/oauth/bindList` | 查询当前用户绑定列表 |
| DELETE | `/system/oauth/unbind/{oauthType}` | 解绑第三方账号 |

### 请求/响应示例

**获取授权地址**

```
GET /api/auth/oauth/github/url

响应：
{
  "code": 200,
  "message": "操作成功",
  "data": "https://github.com/login/oauth/authorize?client_id=xxx&redirect_uri=xxx&scope=read:user user:email&state=xxx"
}
```

**回调处理**

```
POST /api/auth/oauth/github/callback
{
  "code": "GitHub返回的授权码",
  "state": "GitHub返回的state参数"
}

响应（已绑定）：
{
  "code": 200,
  "data": {
    "token": "jwt-token",
    "expiresIn": 7200000,
    "needBind": false
  }
}

响应（未绑定）：
{
  "code": 200,
  "data": {
    "oauthToken": "临时绑定凭证",
    "needBind": true,
    "oauthName": "GitHub用户名",
    "oauthAvatar": "GitHub头像URL"
  }
}
```

**创建新账号并绑定**

```
POST /api/auth/oauth/bindNew
{
  "oauthToken": "临时绑定凭证"
}
```

**绑定已有账号**

```
POST /api/auth/oauth/bindExist
{
  "oauthToken": "临时绑定凭证",
  "username": "已有账号用户名",
  "password": "已有账号密码"
}
```

## 数据库表结构

```sql
CREATE TABLE sys_user_oauth (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  user_id bigint NOT NULL COMMENT '系统用户ID',
  oauth_type varchar(20) NOT NULL COMMENT '第三方平台类型（github/wechat/gitee）',
  oauth_id varchar(100) NOT NULL COMMENT '第三方平台用户唯一标识',
  oauth_name varchar(100) DEFAULT NULL COMMENT '第三方平台用户名',
  oauth_avatar varchar(500) DEFAULT NULL COMMENT '第三方平台头像',
  oauth_email varchar(200) DEFAULT NULL COMMENT '第三方平台邮箱',
  access_token varchar(500) DEFAULT NULL COMMENT 'access_token',
  create_time datetime DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
  update_time datEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE INDEX uk_oauth (oauth_type, oauth_id),
  INDEX idx_user_id (user_id)
) COMMENT='用户第三方登录绑定表';
```

## 安全机制

| 机制 | 说明 |
|------|------|
| state 防 CSRF | 后端生成随机 state 存入 Redis（3分钟过期），回调时校验并删除 |
| oauthToken 临时凭证 | 存入 Redis（5分钟过期），绑定成功后立即删除 |
| client-secret 保护 | 仅存在后端配置文件，不暴露给前端 |
| 密码验证 | 绑定已有账号时需验证用户名和密码 |

## 扩展其他平台

当前架构预留了扩展点，新增其他平台（如 Gitee、微信）只需：

1. `application-dev.yml` 中添加对应平台的配置（`oauth.gitee.*`）
2. `OauthServiceImpl` 中添加对应平台的 token 交换和用户信息获取方法
3. `OauthController` 中添加对应平台的接口
4. 前端登录页添加对应平台的按钮
5. `sys_user.oauth_type` 字段已支持多平台区分

## 文件清单

### 后端

| 文件 | 说明 |
|------|------|
| `backend/src/main/resources/db/oauth_schema.sql` | 数据库表结构 |
| `backend/src/main/java/com/base/config/OauthConfig.java` | OAuth 配置属性类 |
| `backend/src/main/java/com/base/system/entity/UserOauth.java` | 第三方绑定实体 |
| `backend/src/main/java/com/base/system/mapper/UserOauthMapper.java` | Mapper 接口 |
| `backend/src/main/java/com/base/system/dto/oauth/*.java` | 5个 DTO 类 |
| `backend/src/main/java/com/base/system/service/OauthService.java` | 服务接口 |
| `backend/src/main/java/com/base/system/service/impl/OauthServiceImpl.java` | 服务实现 |
| `backend/src/maom/base/system/controller/OauthController.java` | OAuth 控制器 |
| `backend/src/main/java/com/base/system/controller/UserOauthController.java` | 账号管理控制器 |

### 前端

| 文件 | 说明 |
|------|------|
| `frontend/src/api/oauth.js` | OAuth API 封装 |
| `frontend/src/views/OauthCallback.vue` | 回调中转页 |
| `frontend/src/views/OauthBind.vue` | 绑定选择页 |
| `frontend/src/views/Login.vue` | 登录页（添加 GitHub 按钮） |
| `frontend/src/router/index.js` | 路由配置（添加 OAuth 路由） |
| `frontend/src/views/profile/Index.vue` | 个人中心（添加第三方账号管理） |
