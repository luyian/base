# 第三方登录配置

## 功能概述

系统支持多种第三方平台登录，目前支持的登录方式包括：

- **飞书登录**：通过飞书 OAuth2.0 授权登录
- **微信小程序登录**：通过微信小程序wx.login授权登录
- **GitHub 登录**：（配置存在，未启用）

## 飞书登录配置

### 1. 创建飞书企业自建应用

1. 登录飞书开放平台：https://open.feishu.cn/app
2. 点击 **创建企业自建应用**，填写应用名称和描述
3. 进入应用详情，记录 **App ID** 和 **App Secret**
4. 在 **权限管理** 中添加以下权限：
   - `contact:user.base:readonly` — 获取用户基本信息
   - `contact:user.id:readonly` — 获取用户 user_id
5. 在 **重定向URL** 中添加：`http://119.45.176.101/oauth/callback?platform=feishu`
6. 发布应用版本，等待管理员审批通过

### 2. 修改后端配置

编辑 `backend/src/main/resources/application-prod.yml`：

```yaml
feishu:
  enabled: true
  app-id: your-feishu-app-id
  app-secret: your-feishu-app-secret
  base-url: https://open.feishu.cn/open-apis
  redirect-uri: http://119.45.176.101/oauth/callback?platform=feishu

oauth:
  enabled: true
  default-role-id: 2  # 新用户默认角色ID
  mini-service-role-id: 4  # 小程序用户默认角色ID
  wechat:
    enabled: true
    app-id: your-wechat-app-id
    app-secret: your-wechat-app-secret
```

## 微信小程序登录配置

### 1. 登录微信公众平台

1. 登录 https://mp.weixin.qq.com/
2. 进入 **开发管理 → 开发设置**
3. 记录 **AppID** 和 **AppSecret**
4. 设置 **服务器域名**：request 合法域名添加 `https://api.weixin.qq.com`
5. 设置 **授权回调域**：在登录配置页面设置

### 2. 修改后端配置

编辑 `backend/src/main/resources/application-prod.yml`：

```yaml
oauth:
  wechat:
    enabled: true
    app-id: your-wechat-app-id
    app-secret: your-wechat-app-secret
```

## GitHub 登录配置（未启用）

如需启用 GitHub 登录，编辑配置：

```yaml
oauth:
  enabled: true
  github:
    client-id: your-github-client-id
    client-secret: your-github-client-secret
    redirect-uri: http://localhost:3000/oauth/callback
```

## 使用流程

### 飞书/微信登录流程

```
用户点击第三方登录按钮
    ↓
跳转第三方平台授权页面
    ↓
用户确认授权
    ↓
第三方回调带 code 到前端
    ↓
前端调用后端 /auth/oauth/xxx/callback 接口
    ↓
┌─ 已绑定账号 → 直接登录，返回 Token
└─ 未绑定账号 → 跳转绑定页面
                ├─ 创建新账号：一键创建并登录
                └─ 绑定已有账号：输入用户名密码验证后绑定
```

### 个人中心管理

登录后在 **个人中心 → 第三方账号** 标签页可以：
- 查看已绑定的第三方账号
- 解绑已绑定的第三方账号
- 绑定新的第三方账号

## API 接口

### 认证接口（无需认证）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/auth/oauth/enabled` | 查询第三方登录是否启用 |
| GET | `/auth/oauth/github/url` | 获取 GitHub 授权地址 |
| POST | `/auth/oauth/github/callback` | GitHub 回调处理 |
| POST | `/auth/oauth/bindNew` | 创建新账号并绑定 |
| POST | `/auth/oauth/bindExist` | 绑定已有账号 |

### 用户接口（需要认证）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/system/oauth/bindList` | 查询当前用户绑定列表 |
| DELETE | `/system/oauth/unbind/{oauthType}` | 解绑第三方账号 |

### 飞书接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/system/feishu/bind` | 绑定飞书账号 |
| DELETE | `/system/feishu/unbind` | 解绑飞书账号 |
| GET | `/system/feishu/bindInfo` | 查询当前用户飞书绑定信息 |
| POST | `/system/feishu/send` | 发送消息（指定 receiveId） |
| POST | `/system/feishu/send/user/{userId}` | 发送消息给系统用户 |

## 数据库表

第三方账号绑定存储在 `sys_user_oauth` 表：

```sql
CREATE TABLE sys_user_oauth (
  id bigint NOT NULL AUTO_INCREMENT,
  user_id bigint NOT NULL COMMENT '系统用户ID',
  oauth_type varchar(20) NOT NULL COMMENT '第三方平台类型（github/wechat/feishu）',
  oauth_id varchar(100) NOT NULL COMMENT '第三方平台用户唯一标识',
  oauth_name varchar(100) DEFAULT NULL COMMENT '第三方平台用户名',
  oauth_avatar varchar(500) DEFAULT NULL COMMENT '第三方平台头像',
  oauth_email varchar(200) DEFAULT NULL COMMENT '第三方平台邮箱',
  access_token varchar(500) DEFAULT NULL COMMENT 'access_token',
  create_time datetime DEFAULT CURRENT_TIMESTAMP,
  update_time datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_oauth (oauth_type, oauth_id),
  KEY idx_user_id (user_id)
) COMMENT='用户第三方登录绑定表';
```

## 常见问题

### 1. 第三方登录失败

- 检查对应平台的 AppID 和 AppSecret 是否正确
- 检查回调 URL 是否配置正确
- 检查平台应用是否已发布

### 2. 绑定提示"用户已存在"

该第三方账号已绑定到其他系统账号，需要先解绑后再重新绑定。

### 3. 微信小程序登录失败

- 检查 AppID 和 AppSecret 是否正确
- 检查前端 code 是否在有效期内（5分钟）