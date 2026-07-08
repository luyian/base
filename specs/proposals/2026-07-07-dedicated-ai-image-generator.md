# 新增 AI 图片生成专用页面 - 提案

## 背景

当前 AI 图片生成能力嵌入在 AI 对话链路中，通过 `/ai/chat` 自动识别图片生成意图后调用既有 `AiImageService`。该链路会将生成结果下载并上传到 COS，同时返回 Markdown 内容，适合聊天场景，但不适合专业图片生成工作台。

用户需要一个独立的图片生成页面，支持更完整的参数控制和一次生成多张图片，并明确要求：

- 不改动现有 AI 对话图片生成逻辑。
- 后端新写专用接口和服务。
- 图片结果不走 OSS/COS，不写文件管理记录。
- 支持一次生成多张图片。

## 目标

1. 新增独立的 AI 图片生成前端页面。
2. 新增专用后端 Controller/Service/DTO，不复用会上传 COS 的 `AiImageServiceImpl`。
3. 支持一次请求生成多张图片，返回多张图片的 URL 或 base64。
4. 支持基础图片生成参数：提示词、尺寸、数量、模型、适配器、负向提示词、风格、种子等可扩展字段。
5. 复用现有 AI 配置读取能力和图片适配器公共解析能力，但不修改现有聊天图片链路。

## 范围

### 后端

- 新增专用接口，如 `POST /ai/image/generate`。
- 新增请求/响应 DTO。
- 新增专用服务，如 `DedicatedAiImageService`。
- 新增多图结果模型。
- 新增专用多图适配器或对现有适配器能力进行非侵入式扩展。
- 不上传 COS，不写 `sys_file`。

### 前端

- 新增页面，如 `frontend/src/views/ai/ImageGenerator.vue`。
- 新增 API 封装。
- 新增独立路由，并在 Dashboard 主页增加入口卡片。
- 提供图片生成工作台式交互：参数面板、结果网格、预览、复制链接、下载单张。

### 入口与权限

- 不新增左侧菜单。
- 不新增菜单权限初始化 SQL。
- 页面入口挂在 Dashboard 主页，接口沿用登录态访问控制。

## 非目标

- 不修改现有 `/ai/chat` 图片生成行为。
- 不修改现有 `AiImageServiceImpl` 的 COS 保存策略。
- 不做图片历史记录持久化。
- 不做图片编辑上传本地文件能力，除非后续单独扩展。
- 不引入新的外部图片生成供应商，仅基于当前 AI 配置体系扩展。

## 方案概述

新增独立链路：

```text
前端 ImageGenerator.vue
    -> POST /ai/image/generate
        -> DedicatedAiImageController
        -> DedicatedAiImageService
        -> 根据 sys_ai_config 解析模型/接口/适配器
        -> 调用图片生成接口，n=count
        -> 解析多张 URL/base64
        -> 原样返回前端
```

该链路只负责生成与返回，不做下载、COS 上传、文件表写入。返回结果由前端直接展示。对于 base64 图片，前端使用 `data:image/png;base64,...` 展示。
