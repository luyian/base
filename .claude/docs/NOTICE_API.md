# 通知公告 API 文档

## 概述

通知公告模块提供了完整的通知公告管理功能，包括通知的创建、发布、查询、删除以及用户阅读状态管理。

## 基础信息

- **Base URL**: `/system/notice`
- **认证方式**: Bearer Token (JWT)
- **Content-Type**: `application/json`

## API 接口列表

### 1. 分页查询通知公告列表

**接口地址**: `GET /system/notice/page`

**权限要求**: `system:notice:list`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| title | String | 否 | 标题（模糊查询） |
| type | Integer | 否 | 类型（1-通知，2-公告） |
| level | Integer | 否 | 级别（1-普通，2-重要，3-紧急） |
| status | Integer | 否 | 状态（0-草稿，1-已发布） |

**请求示例**:
```bash
curl -X GET "http://localhost:8080/system/notice/page?pageNum=1&pageSize=10&title=系统" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "欢迎使用基础管理系统",
        "content": "欢迎使用基础管理系统，这是一个功能完善的后台管理系统。",
        "type": 1,
        "level": 1,
        "status": 1,
        "publishTime": "2026-01-13 10:00:00",
        "createTime": "2026-01-13 10:00:00",
        "createBy": "system"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

---

### 2. 获取通知公告详情

**接口地址**: `GET /system/notice/{id}`

**权限要求**: `system:notice:query`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 通知ID |

**请求示例**:
```bash
curl -X GET "http://localhost:8080/system/notice/1" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "title": "欢迎使用基础管理系统",
    "content": "欢迎使用基础管理系统，这是一个功能完善的后台管理系统。",
    "type": 1,
    "level": 1,
    "status": 1,
    "publishTime": "2026-01-13 10:00:00",
    "createTime": "2026-01-13 10:00:00",
    "createBy": "system"
  }
}
```

---

### 3. 新增通知公告

**接口地址**: `POST /system/notice`

**权限要求**: `system:notice:add`

**请求体**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| title | String | 是 | 标题 |
| content | String | 是 | 内容 |
| type | Integer | 是 | 类型（1-通知，2-公告） |
| level | Integer | 是 | 级别（1-普通，2-重要，3-紧急） |

**请求示例**:
```bash
curl -X POST "http://localhost:8080/system/notice" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "系统升级通知",
    "content": "系统将于本周末进行升级维护",
    "type": 1,
    "level": 2
  }'
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

---

### 4. 编辑通知公告

**接口地址**: `PUT /system/notice`

**权限要求**: `system:notice:edit`

**请求体**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 通知ID |
| title | String | 是 | 标题 |
| content | String | 是 | 内容 |
| type | Integer | 是 | 类型（1-通知，2-公告） |
| level | Integer | 是 | 级别（1-普通，2-重要，3-紧急） |

**请求示例**:
```bash
curl -X PUT "http://localhost:8080/system/notice" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "title": "系统升级通知（已更新）",
    "content": "系统将于本周末进行升级维护，请提前保存数据",
    "type": 1,
    "level": 2
  }'
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

---

### 5. 删除通知公告

**接口地址**: `DELETE /system/notice/{id}`

**权限要求**: `system:notice:delete`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 通知ID |

**请求示例**:
```bash
curl -X DELETE "http://localhost:8080/system/notice/1" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

---

### 6. 批量删除通知公告

**接口地址**: `DELETE /system/notice/batch`

**权限要求**: `system:notice:delete`

**请求体**:

```json
[1, 2, 3]
```

**请求示例**:
```bash
curl -X DELETE "http://localhost:8080/system/notice/batch" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '[1, 2, 3]'
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

---

### 7. 发布通知公告

**接口地址**: `PUT /system/notice/{id}/publish`

**权限要求**: `system:notice:edit`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 通知ID |

**请求示例**:
```bash
curl -X PUT "http://localhost:8080/system/notice/1/publish" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

---

### 8. 标记通知为已读

**接口地址**: `POST /system/notice/{noticeId}/read`

**权限要求**: 无（登录即可）

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| noticeId | Long | 是 | 通知ID |

**请求示例**:
```bash
curl -X POST "http://localhost:8080/system/notice/1/read" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

---

### 9. 获取未读通知数量

**接口地址**: `GET /system/notice/unread/count`

**权限要求**: 无（登录即可）

**请求示例**:
```bash
curl -X GET "http://localhost:8080/system/notice/unread/count" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 5
}
```

---

### 10. 获取我的通知列表

**接口地址**: `GET /system/notice/my`

**权限要求**: 无（登录即可）

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| title | String | 否 | 标题（模糊查询） |
| type | Integer | 否 | 类型（1-通知，2-公告） |
| level | Integer | 否 | 级别（1-普通，2-重要，3-紧急） |

**请求示例**:
```bash
curl -X GET "http://localhost:8080/system/notice/my?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "欢迎使用基础管理系统",
        "content": "欢迎使用基础管理系统，这是一个功能完善的后台管理系统。",
        "type": 1,
        "level": 1,
        "status": 1,
        "publishTime": "2026-01-13 10:00:00",
        "createTime": "2026-01-13 10:00:00",
        "createBy": "system",
        "isRead": true,
        "readTime": "2026-01-13 11:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

---

## 数据字典

### 通知类型 (type)

| 值 | 说明 |
|----|------|
| 1 | 通知 |
| 2 | 公告 |

### 通知级别 (level)
 说明 |
|----|------|
| 1 | 普通 |
| 2 | 重要 |
| 3 | 紧急 |

### 通知状态 (status)

| 值 | 说明 |
|----|------|
| 0 | 草稿 |
| 1 | 已发布 |

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未授权，请先登录 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 使用流程

### 管理员发布通知流程

1. 调用新增接口创建通知（草稿状态）
2. 调用编辑接口修改通知内容（可选）
3. 调用发布接口发布通知
4. 用户可以在"我的通知"中查看

### 用户查看通知流程

1. 调用获取未读数量接口，显示未读数量
2. 调用我的通知列表接口，查看通知列表
3. 点击通知查看详情
4. 调用标记已读接口，标记为已读

---

## 注意事项

1. 所有接口都需要在请求头中携带 JWT Token
2. 管理接口需要相应的权限才能访问
3. 只有草稿状态的通知可以编辑
4. 已发布的通知不能修改状态为草稿
5. 删阅读记录
6. 标记已读是幂等操作，重复调用不会报错
