# 新增 AI 图片生成专用页面 - 规格

## 功能规格

### 页面能力

系统必须提供一个新的 AI 图片生成专用页面，用户可以在该页面输入提示词并一次生成多张图片。

页面必须支持：

- 输入正向提示词。
- 输入负向提示词。
- 选择图片数量。
- 选择图片尺寸。
- 选择或填写模型。
- 选择或填写适配器。
- 可选填写风格。
- 可选填写 seed。
- 展示多张结果图片。
- 复制图片链接。
- 打开原图。
- 下载图片。

### 后端能力

系统必须提供新的图片生成专用接口，不得复用会上传 COS 的聊天图片生成方法。

接口必须：

- 接收图片生成参数。
- 使用当前生效 AI 配置中的 API Key、图片基础地址、默认模型和默认适配器。
- 支持一次生成多张图片。
- 返回结构化 JSON。
- 不上传 COS/OSS。
- 不写 `sys_file`。

## API 规格

### POST /ai/image/generate

#### 请求体

| 字段 | 类型 | 必填 | 规则 |
|------|------|------|------|
| prompt | string | 是 | 1~4000 字符 |
| negativePrompt | string | 否 | 最大 2000 字符 |
| size | string | 是 | 格式 `宽x高`，如 `1024x1024` |
| count | integer | 是 | 1~8 |
| model | string | 否 | 最大 100 字符，未传时使用配置或默认模型 |
| adapter | string | 否 | `openai-images`、`images`、`chat-completions`、`chat`、`sensenova`、`sense-nova` |
| style | string | 否 | 最大 100 字符 |
| seed | integer | 否 | 0~2147483647 |

#### 响应体

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "prompt": "赛博朋克城市夜景",
    "model": "gpt-image-1",
    "adapter": "openai-images",
    "size": "1024x1024",
    "count": 4,
    "images": [
      {
        "index": 1,
        "url": "https://example.com/image.png",
        "base64": null,
        "mimeType": "image/png",
        "revisedPrompt": "..."
      }
    ],
    "rawText": null
  }
}
```

#### 错误处理

- AI 未配置：返回业务错误 `AI_NOT_CONFIGURED`。
- 提示词为空：返回参数错误。
- 数量超限：返回参数错误。
- 尺寸格式错误：返回参数错误。
- 适配器不支持：返回参数错误。
- 供应商接口异常：返回 AI 服务不可用。
- 供应商未返回图片：返回 AI 服务不可用，并带响应摘要。

## 业务规则

1. `count` 默认值为 1，最大值为 8。
2. `size` 未传时默认 `1024x1024`。
3. 如果 AI 配置中设置了 `imageModel`，请求未指定 `model` 时使用该值。
4. 如果 AI 配置中设置了 `imageAdapter`，请求未指定 `adapter` 时使用该值。
5. 如果请求指定 `model` 或 `adapter`，优先使用请求值。
6. 返回的图片按供应商实际返回数量展示，不强行补齐。
7. 任何结果图片不得在后端上传 COS/OSS。
8. 后端不得写入文件管理记录。

## 安全要求

- 接口需要登录态。
- 页面入口挂在 Dashboard 主页，不新增左侧菜单权限。
- 图片提示词长度必须校验。
- 请求数量必须限制，防止一次请求成本过高。
- API Key 不得返回前端。
- 后端日志不得打印 API Key。

## 前端交互规格

1. 点击生成时禁用生成按钮并展示加载状态。
2. 生成成功后结果区展示图片网格。
3. 单张图片加载失败时展示失败占位，不影响其他图片。
4. URL 图片直接使用返回 URL 展示。
5. base64 图片使用 `data:image/{type};base64,{base64}` 展示。
6. 下载按钮在浏览器端通过 `<a download>` 或 Blob 下载实现。
7. 新页面不依赖 AI 对话历史。
