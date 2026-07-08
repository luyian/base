# 新增 AI 图片生成专用页面 - 任务清单

## 概览

- 提案: specs/proposals/2026-07-07-dedicated-ai-image-generator.md
- 设计: specs/designs/2026-07-07-dedicated-ai-image-generator.md
- 规格: specs/specs/2026-07-07-dedicated-ai-image-generator.md

## 任务列表

- [x] 任务1：新增后端专用请求/响应 DTO
- [x] 任务2：新增后端专用图片生成上下文、结果模型和多图解析工具
- [x] 任务3：新增后端专用适配器选择器和 OpenAI Images 适配器
- [x] 任务4：新增 Chat Completions 与 SenseNova 专用适配器
- [x] 任务5：新增 `DedicatedAiImageService` 和实现类，确保不上传 COS/OSS、不写文件记录
- [x] 任务6：新增 `DedicatedAiImageController`，提供 `POST /ai/image/generate`
- [x] 任务7：新增 Dashboard 主页入口卡片，不新增菜单 SQL
- [x] 任务8：新增前端 `api/aiImage.js`
- [x] 任务9：新增前端 `views/ai/ImageGenerator.vue` 专用页面
- [x] 任务10：新增前端常量路由
- [x] 任务11：记录 `.claude/docs/TEMP.md`
- [x] 任务12：执行 `codegraph sync`
- [x] 任务13：执行后端编译与前端构建验证
