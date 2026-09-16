---
name: gen-image
aliases: ["img:gen", "gen:img"]
description: 用商汤大模型生成任意图片/图标（连接 ai_video_studio 库读取渠道配置，自动去右下角水印）
---

# 商汤图片生成

用商汤 SenseNova 图片生成模型，根据文字描述生成图片。配置直接读 ai_video_studio 项目的数据库，无需手动填 key。

## 触发方式
用户输入 `/img:gen`，或要求"生成图片 / 生成图标 / 画一张图 / 用商汤生成"时触发。

## 工具位置（独立于小程序目录，避免 `__pycache__` 污染 ling-tools）
- 通用单张：`tools/img-gen/gen_image.py`（任意 prompt）
- 批量图标：`tools/img-gen/gen_icons.py`（首页 PDF 三张图标，已配置好 prompt，输出到 `ling-tools/assets/`）

## 工作流程

### 1. 确认生成意图
向用户确认：图片主题内容、用途（普通图还是图标）、期望尺寸比例。

### 2. 生成图片
配置从 ai_video_studio 库自动读取（`ai_channel.channel_code=sensenova` 的 api_key/base_url + `ai_model.model_type=IMAGE` 的模型），直接执行：

```bash
# 普通图片（保存原图）
cd tools/img-gen && python -X utf8 gen_image.py --prompt "描述..." --out /path/xxx.png

# 彩色图标（现代可爱风，保留色彩、不透明、去右下角水印 + 缩放，占满首页方块）
cd tools/img-gen && python -X utf8 gen_image.py --prompt "..." --out /path/icon-xxx.png --icon --style color --scale 300

# 白描图标（白色图形 + 透明背景，叠在彩色方块上）
cd tools/img-gen && python -X utf8 gen_image.py --prompt "..." --out /path/icon-xxx.png --icon --style white --scale 300

# 指定尺寸（须白名单内，默认 2048x2048 方形）
cd tools/img-gen && python -X utf8 gen_image.py --prompt "..." --out /path/wide.png --size 2752x1536

# 预演不调用
cd tools/img-gen && python -X utf8 gen_image.py --prompt "..." --out x.png --dry-run
```

尺寸白名单：`1664x2496 2496x1664 1760x2368 2368x1760 1824x2272 2272x1824 2048x2048 2752x1536 1536x2752 3072x1376 1344x3136 2560x720 3072x864`

### 3. 关键特性
- **自动去水印**：商汤生成图右下角有灰色文字水印，`--icon` 模式会在右下角矩形区域清除中灰像素（亮度 150~238 低饱和）：`white` 风格透明化、`color` 风格用区域背景色填充，图标主体都不受影响；普通模式保留原图。
- **两种图标风格**：`--style color` 保留彩色（现代可爱风，占满首页方块，不透明）；`--style white` 白描线性（白色图形 + 透明背景，叠在彩色方块上）。均中心裁剪正方形并缩放到 `--scale`（默认 300px）。
- **失败重试**：默认 3 次，接口超时 180s。
- 想不用数据库：`--api-key sk-xxx` 直接指定。

### 4. 数据库连接（默认值，可用参数/环境变量覆盖）
- host `127.0.0.1:3306`，user `root`，password `000000`，库 `ai_video_studio`
- 参数：`--db-host --db-port --db-user --db-password --db-name`

## 注意
- 生成一张图约 30~120s，命令需给足超时。
- Windows 控制台中文显示乱码不影响实际生成（请求是 UTF-8），用 `python -X utf8` 运行。
- 首页图标替换后记得在 `ling-tools/pages/index/index.wxml` 更新 `<image src>`，并 `git add` 新图片。
