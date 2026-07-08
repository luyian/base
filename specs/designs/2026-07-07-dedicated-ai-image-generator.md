# 新增 AI 图片生成专用页面 - 设计

## 架构设计

### 现有链路

```text
/ai/chat
  -> AiServiceImpl
  -> AiImageServiceImpl
  -> AiImageAdapter
  -> 下载图片
  -> 上传 COS
  -> 写 sys_file
  -> 返回 Markdown
```

### 新链路

```text
/ai/image/generate
  -> DedicatedAiImageController
  -> DedicatedAiImageService
  -> DedicatedAiImageModelResolver
  -> DedicatedAiImageAdapterSelector
  -> DedicatedAiImageAdapter
  -> 返回多图 JSON
```

新链路与现有聊天链路并行，避免改动 `AiServiceImpl` 和 `AiImageServiceImpl` 的现有行为。

## 后端设计

### 包结构

```text
backend/src/main/java/com/base/ai/
├── controller/
│   └── DedicatedAiImageController.java
├── dto/image/
│   ├── DedicatedAiImageGenerateRequest.java
│   ├── DedicatedAiImageGenerateResponse.java
│   └── DedicatedAiImageItemResponse.java
├── image/dedicated/
│   ├── DedicatedAiImageContext.java
│   ├── DedicatedAiImageResult.java
│   ├── DedicatedAiImageAdapter.java
│   ├── DedicatedAiImageAdapterSelector.java
│   ├── DedicatedOpenAiImagesAdapter.java
│   ├── DedicatedChatCompletionsImageAdapter.java
│   └── DedicatedSenseNovaImageAdapter.java
└── service/
    ├── DedicatedAiImageService.java
    └── impl/DedicatedAiImageServiceImpl.java
```

说明：

- 命名使用 `Dedicated` 前缀，明确区别于现有聊天图片链路。
- 适配器可参考现有代码实现，但返回多图结构，不上传 COS。
- 如为减少重复，可抽取只读解析工具，但不得改变现有类行为。

### API 设计

```http
POST /ai/image/generate
Content-Type: application/json
Authorization: Bearer <token>
```

请求字段：

```json
{
  "prompt": "赛博朋克城市夜景",
  "negativePrompt": "低清晰度，畸形",
  "size": "1024x1024",
  "count": 4,
  "model": "gpt-image-1",
  "adapter": "openai-images",
  "style": "写实",
  "seed": 12345
}
```

响应字段：

```json
{
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
```

### 适配器策略

#### OpenAI Images

- 请求 `/v1/images/generations`。
- 使用 `n=count`。
- 解析 `data[].url`、`data[].b64_json`、`data[].revised_prompt`。

#### Chat Completions

- 请求 `/v1/chat/completions`。
- 请求中明确要求返回 `count` 张图片。
- 尽力解析文本、Markdown 图片、结构化 JSON 中的多张图片。
- 若供应商只返回一张，响应中返回实际张数。

#### SenseNova

- 请求 `/v1/images/generations`。
- 使用 `n=count`。
- 根据官方尺寸列表做尺寸映射。
- 解析 `data`、`images`、`output` 等结构中的多张图片。

## 前端设计

### 页面

路径建议：

```text
/ai/image-generator
```

文件：

```text
frontend/src/views/ai/ImageGenerator.vue
frontend/src/api/aiImage.js
```

### 页面布局

- 左侧参数面板：
  - 提示词
  - 负向提示词
  - 图片数量
  - 尺寸
  - 模型
  - 适配器
  - 风格
  - Seed
  - 生成按钮
- 右侧结果区：
  - 多图网格
  - 单张预览
  - 打开原图
  - 复制链接
  - 下载单张
  - 失败状态展示

### UI 原则

- 工具型页面，不做营销页。
- 使用 Element Plus 现有设计风格。
- 结果网格固定比例，避免图片加载引发布局抖动。
- 图片生成中显示骨架屏或加载态。

## 入口与权限

- 不新增左侧菜单。
- 不新增菜单权限初始化 SQL。
- 前端在 Dashboard 第一屏增加“AI 作图”入口卡片，点击跳转 `/ai/image-generator`。
- 新页面作为常量路由存在，和 `/ai/chat` 保持一致。
- 后端接口不单独增加菜单权限注解，依赖全局登录态拦截。

## 数据模型

本次不新增业务表。

不写入：

- `sys_file`
- COS/OSS
- 图片历史表

## 兼容性

- 不修改 `/ai/chat`。
- 不修改现有 `AiImageServiceImpl`。
- 不修改现有小程序 AI 页面。
- 新接口独立，失败不影响旧聊天链路。
