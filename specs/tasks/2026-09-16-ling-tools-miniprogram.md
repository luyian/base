# 任务清单：元灵工具箱微信小程序

- 提案：proposals/2026-09-16-ling-tools-miniprogram.md
- 设计：designs/2026-09-16-ling-tools-miniprogram.md

## 任务列表

### 搭建骨架（复用资产复制）
- [ ] 任务1：新建 `ling-tools/` 目录，复制复用资产：`app.js`（改名）、`utils/request.js`、`api/auth.js`、`api/checkin.js`、`components/nav-bar`、`sitemap.json`、`assets`
- [ ] 任务2：新建 `app.json`（pages + 主题 + 4 项 tabBar）+ `project.config.json`（独立 appid 占位）

### 登录
- [ ] 任务3：迁移 `pages/login/*`（含 register），标题改「元灵工具箱」

### 首页（新增）
- [ ] 任务4：`pages/index/*` 中式宫格入口（文件转换/打卡）

### 文件转换页（新增核心，三 tab）
- [ ] 任务5：`api/fileConvert.js` 封装上传：`uploadPdf`（to-word/to-markdown/pdf-compress，wx.uploadFile）、`uploadImages`（scan-doc 多图逐传）、复用 request.js 调 scan-doc 的 create/order/delete/finalize
- [ ] 任务6A：`pages/fileconvert/*` 三 tab 外壳 + **转换 tab**（PDF→Word/Markdown：选文件→格式→开始→下载预览）
- [ ] 任务6B：**压缩 tab**（滑块 40~250 dpi + marks 高清200/均衡150/极致100 + 低于100 tips + 预计大小→开始→结果压缩率）
- [ ] 任务6C：**整理 tab**（完整扫描工作区：创建工作区/多图追加/缩略图列表/调序/删单张/放弃/完成合 PDF→下载）

### 打卡（迁移）
- [ ] 任务7：迁移 `pages/checkin/*`（含印章主题样式）

### 我的页（新增精简）
- [ ] 任务8：`pages/profile/*`（用户信息/主题切换/退出登录）

### 收尾
- [ ] 任务9：微信开发者工具加载 `ling-tools/` 验证登录→首页→文件转换（三 tab）→打卡全链路；更新 TEMP.md + codegraph sync

## 验收标准
- 登录（账号/微信）可用，401 跳登录。
- 首页宫格可进入文件转换与打卡；文件转换在页面内三 tab（压缩/整理/转换）切换顺畅。
- 转换：PDF→Word/Markdown 端到端返回并从 COS 下载。
- 压缩：滑动条 40~250、marks 标注、低于 100 提示、预计大小、结果压缩率；端到端可用。
- 整理：创建工作区→多图追加→缩略图排序/删除→调序→完成合 PDF→下载/预览 全链路可用；压缩档位三选生效。
- 打卡功能与现有简基一致。
- 后端零新增代码。