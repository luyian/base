# AI 图片生成专用页面归档

## 状态

已完成。

## 结果

- 新增 `POST /ai/image/generate` 专用多图生成接口。
- 新增 AI 作图页面 `/ai/image-generator`，支持一次生成最多 8 张图片。
- 新增 Dashboard 主页入口卡片，不新增左侧菜单和菜单 SQL。
- 图片结果直接返回 URL/base64，不上传 COS/OSS，不写 `sys_file`。
- 现有 `/ai/chat` 图片生成链路未改动。

## 验证

- `codegraph sync`
- `mvn compile`
- `npm.cmd run build`
