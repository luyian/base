# 开放接口文档

## 功能概述

系统为外部系统提供文件上传和下载的开放接口。采用 **appId + appSecret 换取 accessToken** 的认证模式，独立于系统用户认证体系，不限制文件格式。

## 配置步骤

### 1. 注册外部系统应用

编辑 `backend/src/main/resources/application-open-api.yml`，在 `apps` 列表中添加应用：

```yaml
open:
  api:
    enabled: true
    token-expire: 7200       # Token 有效期（秒），默认 2 小时
    apps:
      - app-id: ext_system_001
        app-secret: a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6
        name: 外部系统A
      - app-id: ext_system_002
        app-secret: p6o5n4m3l2k1j0i9h8g7f6e5d4c3b2a1
        name: 外部系统B
```

### 2. 确认配置引入

确保 `application-dev.yml`（或对应环境配置）中包含：

```yaml
spring:
  profiles:
    include: open-api
```

---

## 接口地址

所有接口的基础地址（BASE_URL）根据部署环境确定：

| 环境 | 地址 |
|------|------|
| 本地开发 | `http://localhost:8080/api` |
| 生产环境 | `http://{服务器IP或域名}/api` |

以下文档中统一使用 `{BASE_URL}` 作为占位符，调用时替换为实际地址。

---

## 认证流程

```
外部系统                              服务端
  │                                    │
  │  POST /open/auth/token             │
  │  {appId, appSecret}                │
  │ ──────────────────────────────────> │
  │                                    │  校验 appId + appSecret
  │                                    │  生成 UUID Token
  │  {accessToken, expiresIn, ...}     │  缓存到 Redis
  │ <────────────────────────────────── │
  │                                    │
  │  POST /open/file/upload            │
  │  Authorization: Bearer {token}     │
  │ ──────────────────────────────────> │
  │                                    │  拦截器校验 Token
  │  {fileId, fileName, fileUrl, ...}  │  上传文件到 FastDFS
  │ <────────────────────────────────── │
  │                                    │
  │  GET /open/file/download/{id}      │
  │  Authorization: Bearer {token}     │
  │ ──────────────────────────────────> │
  │                                    │  校验 Token
  │  [文件二进制流]                      │  从 FastDFS 下载
  │ <────────────────────────────────── │
```

---

## API 接口

### 1. 获取访问令牌

**POST** `/api/open/auth/token`

外部系统使用 appId 和 appSecret 换取 accessToken。如果已存在有效 Token（剩余有效期 > 5 分钟），则直接返回现有 Token。

**请求头**

```
Content-Type: application/json
```

**请求体**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| appId | String | 是 | 应用ID |
| appSecret | String | 是 | 应用密钥 |

**响应体**

| 字段 | 类型 | 说明 |
|------|------|------|
| accessToken | String | 访问令牌 |
| expiresIn | Long | 有效期（秒） |
| tokenType | String | 令牌类型，固定 "Bearer" |

**调用示例**

```bash
curl -X POST {BASE_URL}/open/auth/token \
  -H "Content-Type: application/json" \
  -d '{
    "appId": "ext_system_001",
    "appSecret": "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6"
  }'
```

**成功响应**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "accessToken": "8f14e45fceea167a5a36dedd4bea2543",
    "expiresIn": 7200,
    "tokenType": "Bearer"
  },
  "timestamp": 1712563200000
}
```

**失败响应 — appId 不存在**

```json
{
  "code": 600,
  "message": "应用不存在",
  "data": null,
  "timestamp": 1712563200000
}
```

**失败响应 — 密钥错误**

```json
{
  "code": 600,
  "message": "应用密钥错误",
  "data": null,
  "timestamp": 1712563200000
}
```

---

### 2. 上传文件

**POST** `/api/open/file/upload`

上传文件到服务器，不限制文件格式和类型。上传的文件归入 `open` 分组。

**请求头**

```
Authorization: Bearer {accessToken}
Content-Type: multipart/form-data
```

**请求参数**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | File | 是 | 上传的文件 |
| fileDesc | String | 否 | 文件描述 |

**响应体**

| 字段 | 类型 | 说明 |
|------|------|------|
| fileId | Long | 文件ID，下载时使用 |
| fileName | String | 存储文件名 |
| originalName | String | 原始文件名 |
| fileSize | Long | 文件大小（字节） |
| fileUrl | String | 文件访问URL |

**调用示例**

```bash
curl -X POST {BASE_URL}/open/file/upload \
  -H "Authorization: Bearer 8f14e45fceea167a5a36dedd4bea2543" \
  -F "file=@/path/to/document.pdf" \
  -F "fileDesc=季度报告"
```

**成功响应**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "fileId": 42,
    "fileName": "group1/M00/00/00/abcdef1234567890.pdf",
    "originalName": "document.pdf",
    "fileSize": 1048576,
    "fileUrl": "http://xuecong.xyz/group1/M00/00/00/abcdef1234567890.pdf"
  },
  "timestamp": 1712563200000
}
```

**失败响应 — 文件为空**

```json
{
  "code": 500,
  "message": "文件不能为空",
  "data": null,
  "timestamp": 1712563200000
}
```

**失败响应 — Token 无效**

```json
{
  "code": 401,
  "message": "访问令牌无效或已过期",
  "data": null,
  "timestamp": 1712563200000
}
```

---

### 3. 下载文件

**GET** `/api/open/file/download/{id}`

通过文件ID下载文件，返回文件二进制流。

**请求头**

```
Authorization: Bearer {accessToken}
```

**路径参数**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 文件ID（上传时返回的 fileId） |

**响应头**

```
Content-Type: {文件MIME类型}
Content-Disposition: attachment; filename="{原始文件名}"
Content-Length: {文件大小}
```

**调用示例**

```bash
# 下载文件并保存
curl -O -J {BASE_URL}/open/file/download/42 \
  -H "Authorization: Bearer 8f14e45fceea167a5a36dedd4bea2543"

# 下载文件并指定保存名称
curl -o report.pdf {BASE_URL}/open/file/download/42 \
  -H "Authorization: Bearer 8f14e45fceea167a5a36dedd4bea2543"
```

---

## 完整调用示例

### Java (OkHttp)

```java
import okhttp3.*;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

public class OpenApiClient {

    // 替换为实际部署地址，如 http://your-server.com/api
    private static final String BASE_URL = "{BASE_URL}";
    private static final OkHttpClient client = new OkHttpClient();

    /**
     * 获取访问令牌
     */
    public static String getToken(String appId, String appSecret) throws Exception {
        JSONObject body = new JSONObject();
        body.put("appId", appId);
        body.put("appSecret", appSecret);

        Request request = new Request.Builder()
                .url(BASE_URL + "/open/auth/token")
                .post(RequestBody.create(body.toJSONString(),
                        MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            JSONObject result = JSON.parseObject(response.body().string());
            return result.getJSONObject("data").getString("accessToken");
        }
    }

    /**
     * 上传文件
     */
    public static JSONObject uploadFile(String token, String filePath, String fileDesc) throws Exception {
        java.io.File file = new java.io.File(filePath);
        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(file, MediaType.parse("application/octet-stream")))
                .addFormDataPart("fileDesc", fileDesc)
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "/open/file/upload")
                .header("Authorization", "Bearer " + token)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            JSONObject result = JSON.parseObject(response.body().string());
            return result.getJSONObject("data");
        }
    }

    /**
     * 下载文件
     */
    public static void downloadFile(String token, Long fileId, String savePath) throws Exception {
        Request request = new Request.Builder()
                .url(BASE_URL + "/open/file/download/" + fileId)
                .header("Authorization", "Bearer " + token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            java.nio.file.Files.write(
                    java.nio.file.Paths.get(savePath),
                    response.body().bytes()
            );
        }
    }

    public static void main(String[] args) throws Exception {
        // 1. 获取 Token
        String token = getToken("ext_system_001", "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6");
        System.out.println("Token: " + token);

        // 2. 上传文件
        JSONObject fileInfo = uploadFile(token, "/path/to/report.pdf", "季度报告");
        Long fileId = fileInfo.getLong("fileId");
        System.out.println("文件ID: " + fileId);

        // 3. 下载文件
        downloadFile(token, fileId, "/path/to/download/report.pdf");
        System.out.println("下载完成");
    }
}
```

### Python (requests)

```python
import requests

# 替换为实际部署地址，如 http://your-server.com/api
BASE_URL = "{BASE_URL}"

# 1. 获取 Token
resp = requests.post(f"{BASE_URL}/open/auth/token", json={
    "appId": "ext_system_001",
    "appSecret": "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6"
})
token = resp.json()["data"]["accessToken"]
print(f"Token: {token}")

headers = {"Authorization": f"Bearer {token}"}

# 2. 上传文件
with open("/path/to/report.pdf", "rb") as f:
    resp = requests.post(
        f"{BASE_URL}/open/file/upload",
        headers=headers,
        files={"file": ("report.pdf", f)},
        data={"fileDesc": "季度报告"}
    )
file_id = resp.json()["data"]["fileId"]
print(f"文件ID: {file_id}")

# 3. 下载文件
resp = requests.get(
    f"{BASE_URL}/open/file/download/{file_id}",
    headers=headers
)
with open("download_report.pdf", "wb") as f:
    f.write(resp.content)
print("下载完成")
```

### Node.js (axios + form-data)

```javascript
const axios = require('axios');
const FormData = require('form-data');
const fs = require('fs');

// 替换为实际部署地址，如 http://your-server.com/api
const BASE_URL = '{BASE_URL}';

async function main() {
    // 1. 获取 Token
    const tokenResp = await axios.post(`${BASE_URL}/open/auth/token`, {
        appId: 'ext_system_001',
        appSecret: 'a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6'
    });
    const token = tokenResp.data.data.accessToken;
    console.log(`Token: ${token}`);

    const headers = { Authorization: `Bearer ${token}` };

    // 2. 上传文件
    const form = new FormData();
    form.append('file', fs.createReadStream('/path/to/report.pdf'));
    form.append('fileDesc', '季度报告');

    const uploadResp = await axios.post(`${BASE_URL}/open/file/upload`, form, {
        headers: { ...headers, ...form.getHeaders() }
    });
    const fileId = uploadResp.data.data.fileId;
    console.log(`文件ID: ${fileId}`);

    // 3. 下载文件
    const downloadResp = await axios.get(`${BASE_URL}/open/file/download/${fileId}`, {
        headers,
        responseType: 'arraybuffer'
    });
    fs.writeFileSync('download_report.pdf', downloadResp.data);
    console.log('下载完成');
}

main().catch(console.error);
```

---

## Token 管理机制

### 有效期与刷新

- Token 默认有效期 **2 小时**（可通过 `token-expire` 配置调整）
- Token 剩余有效期 > 5 分钟时，重复调用签发接口会返回同一个 Token
- Token 剩余有效期 <= 5 分钟时，自动签发新 Token 并使旧 Token 失效

### Redis 缓存结构

| Key | Value | 说明 |
|-----|-------|------|
| `open:token:{accessToken}` | appId | 通过 Token 查找应用 |
| `open:app:{appId}:token` | accessToken | 通过应用查找 Token |

### 建议：Token 自动续期

外部系统建议在本地缓存 Token，并在 Token 过期前提前调用签发接口刷新：

```python
import time

class TokenManager:
    def __init__(self, app_id, app_secret):
        self.app_id = app_id
        self.app_secret = app_secret
        self.token = None
        self.expire_at = 0

    def get_token(self):
        # 提前 5 分钟刷新
        if self.token and time.time() < self.expire_at - 300:
            return self.token

        resp = requests.post(f"{BASE_URL}/open/auth/token", json={
            "appId": self.app_id,
            "appSecret": self.app_secret
        })
        data = resp.json()["data"]
        self.token = data["accessToken"]
        self.expire_at = time.time() + data["expiresIn"]
        return self.token
```

---

## 错误码说明

| HTTP 状态码 | code | 场景 |
|------------|------|------|
| 200 | 200 | 请求成功 |
| 401 | 401 | 缺少 Token / Token 无效或已过期 |
| 403 | 403 | 开放接口未启用 |
| 500 | 500 | 文件为空 / 上传失败 / 下载失败 |
| 500 | 600 | 应用不存在 / 应用密钥错误 |

---

## 常见问题

### 1. Token 获取返回 "应用不存在"

检查 `application-open-api.yml` 中是否配置了对应的 `app-id`，以及配置文件是否被正确引入（`spring.profiles.include: open-api`）。

### 2. 请求返回 401 "访问令牌无效或已过期"

- 确认 Token 未过期（默认 2 小时有效）
- 确认请求头格式正确：`Authorization: Bearer {token}`（注意 Bearer 后有空格）
- 确认 Redis 服务正常运行

### 3. 文件上传返回 "文件上传到FastDFS失败"

FastDFS 不可用时会自动降级到本地存储。如果仍然失败，检查本地存储路径 `/fastdfs/storage/data` 是否有写入权限。

### 4. 文件下载返回空内容

确认文件ID正确，且文件在 FastDFS 或本地存储中存在。可通过系统管理后台的文件管理页面确认文件状态。

### 5. 上传大文件失败

默认最大文件大小为 10MB（Spring Boot 限制）。如需调整，修改 `application.yml`：

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 50MB
```
