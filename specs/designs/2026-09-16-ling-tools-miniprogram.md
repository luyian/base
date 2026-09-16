# 设计：元灵工具箱微信小程序（ling-tools）

## 一、定位与目标

新建一个**独立**微信小程序「**元灵工具箱**」（目录 `ling-tools/`），与现有「简基」小程序（`minservice/`）平行共存、互不影响。首版提供两大功能：

| 功能 | 实现方式 | 后端 |
|---|---|---|
| **文件转换** | 小程序端全新开发（复用 Web 端 FileConvert 能力） | 复用现有 `/system/file-convert/*` + python-tools |
| **打卡** | 整体迁移现有 minservice 打卡页（含中式设计） | 复用现有 `/checkin/*` |

登录、主题体系、request 封装、nav-bar 组件整体复用现有 minservice 资产。

## 二、目录结构

```
ling-tools/                      # 元灵工具箱（独立小程序工程）
├── app.js                       # 应用入口（沿用 minservice 主题同步/登录拦截，改名）
├── app.json                     # 页面/主题/tabBar 注册
├── project.config.json          # 独立 appid
├── sitemap.json                 # 沿用
├── api/
│   ├── auth.js                  # 复用 minservice（登录/绑定/用户信息）
│   ├── checkin.js               # 复用 minservice
│   └── fileConvert.js           # ★ 新增：文件转换 API（封装 wx.uploadFile）
├── utils/
│   └── request.js               # 复用 minservice（token/401 拦截）
├── components/
│   └── nav-bar/                 # 复用 minservice
├── pages/
│   ├── login/                   # 复用 minservice（改标题为「元灵工具箱」）
│   ├── index/                   # ★ 首页：功能宫格入口（文件转换/打卡）
│   ├── fileconvert/             # ★ 文件转换页（PDF 转 Word/Markdown/压缩）
│   ├── checkin/                 # 复用 minservice（中式印章设计）
│   └── profile/                 # ★ 我的页（精简，复用部分）
└── assets/                      # 图标（首页/宫格等，可占位）
```

## 三、后端复用（零新增）

| 接口 | 说明 | token 路径 |
|---|---|---|
| `POST /system/file-convert/pdf-to-word` | PDF→Word | file-convert:use |
| `POST /system/file-convert/pdf-to-markdown` | PDF→Markdown | file-convert:use |
| `POST /system/file-convert/pdf-compress` | PDF 压缩（dpi/quality 透传） | file-convert:use |
| `POST /system/scan-doc/create` | 扫描整理：创建工作区 | file-convert:use |
| `POST /system/scan-doc/{docId}/images` | 扫描整理：追加图片（多文件） | file-convert:use |
| `PUT /system/scan-doc/{docId}/order` | 扫描整理：调序（body: 有序 ids） | file-convert:use |
| `DELETE /system/scan-doc/{docId}/images/{imageId}` | 扫描整理：删单张 | file-convert:use |
| `DELETE /system/scan-doc/{docId}` | 扫描整理：放弃工作区 | file-convert:use |
| `POST /system/scan-doc/{docId}/finalize?maxSide&quality` | 扫描整理：完成合 PDF | file-convert:use |
| `GET /checkin/plan/list` 等一整套 | 打卡 | checkin:* |
| `POST /auth/login`、`/auth/wx-login` 等 | 登录/绑定 | — |

## 四、登录与主题

- 复用 `pages/login/login.*`，文案「简基」改「元灵工具箱」。
- 复用 `utils/request.js`（`wx.getStorageSync('token')` + Bearer + 401 跳登录）。
- 复用 `app.js` 主题体系（light/dark + `_syncThemeStyle` + `setTheme`），全局 `Page` 劫持注入 `themeClass`。
- 复用 `components/nav-bar`。

## 五、首页 index（新增，tabBar 第 1 项）

采用中式设计（与打卡一致：`--paper/--ink/--seal`）：

```text
<nav-bar title="元灵工具箱" />
┌──────────────────────────────┐
│  顶部：文字 Logo「元灵」       │  朱砂方章点缀 + 金色年份
├──────────────────────────────┤
│  ╔════════╗  ╔════════╗       │
│  ║  📄     ║  ║  ✅     ║       │  两枚宫格卡片（歪角度印章感）
│  ║  文件转换 ║  ║  打卡    ║       │  tap → navigateTo 对应页
│  ╚════════╝  ╚════════╝       │
├──────────────────────────────┤
│  底部版本号 · 元灵工具箱 v1.0  │
└──────────────────────────────┘
```

首页仅作入口，不放真实数据；宫格点击跳转：
- 文件转换 → `/pages/fileconvert/fileconvert`
- 打卡 → `/pages/checkin/checkin`

## 六、文件转换页 fileconvert（新增）

复用一个「文件转换」页面，进入后在页面内用 **tab 切换** 三个子功能：**PDF 压缩 / PDF 整理 / PDF 转换**（用户确认：单页面 tab 切换，首页一个宫格入口）。

```
pages/fileconvert/fileconvert
  ├─ tab[压缩]   → PDF 压缩（滑动条 + 预计大小）
  ├─ tab[整理]   → 扫描图片整理（完整工作区）
  └─ tab[转换]   → PDF 转 Word / Markdown
```

> 首页单个宫格「文件转换」→ 进入本页；用户在页面内用 tab 切到所需工具。三个工具共用 nav-bar 标题「文件转换」，各自独立交互区。

### 三个 tab 与后端映射

| tab | 功能 | 调用的后端接口 |
|---|---|---|
| 压缩 | PDF 压缩 | `/system/file-convert/pdf-compress`（dpi/quality） |
| 整理 | 扫描整理（完整） | `/system/scan-doc/*`（create/append/order/delete/finalize） |
| 转换 | PDF→Word / →Markdown | `/system/file-convert/pdf-to-word`、`pdf-to-markdown` |

复用 Web 端 `frontend/src/views/system/FileConvert.vue` 与 `ScanDocConvert.vue` 的能力，小屏化。

### 加载体
`api/fileConvert.js` 用 `wx.uploadFile` 封装（小程序不支持表单上传同 fetch）：

```js
uploadPdf(url, filePath, extra) => Promise
uploadImages(url, filePaths)   // scan-doc 追加多图
request(url, data, method)     // 复用 request.js 处理 create/order/finalize 等非上传接口
```

`wx.uploadFile` 单文件一次上传；`/scan-doc/{docId}/images` 是 `files[]` 多图，小程序端逐张循环 `wx.uploadFile`（`name: 'files[]'`），再刷新全量列表。

### 能力与参数

| 类型 | 调用 | 小程序参数 | Web 端一致 |
|---|---|---|---|
| 转 Word | `pdf-to-word` | 仅 file | ✔ |
| 转 Markdown | `pdf-to-markdown` | 仅 file | ✔ |
| 压缩 | `pdf-compress` | file + dpi + quality | ✔ 滑动条 + 预计大小 |
| 整理·创建工作区 | `scan-doc/create` | docName | ✔ |
| 整理·追加图片 | `scan-doc/{docId}/images` | 多图逐张传 | ✔ |
| 整理·调序 | `scan-doc/{docId}/order` | ids[] | ✔ |
| 整理·删单张 | `scan-doc/{docId}/images/{id}` | — | ✔ |
| 整理·放弃 | `scan-doc/{docId}` | — | ✔ |
| 整理·完成 | `scan-doc/{docId}/finalize` | maxSide/quality | ✔ |

压缩档位在小程序端复用 Web 端交互：`slider`（40~250 dpi）、marks（高清 200/均衡 150/极致 100）、低于 100 显示提示、前端估算预计大小。扫描整理的压缩档位沿用三档（常规 2000/70、清晰 2400/80、压缩 1200/50）。

### 页面结构

```text
<nav-bar title="文件转换" />
┌──────────────────────────────────────┐
│  [tab] 压缩 | 整理 | 转换             │  ← 三 tab 切换
├──────────────────────────────────────┤
│  tab=压缩：                           │
│    [选 PDF] → wx.chooseMessageFile    │
│     支持 .pdf（≤50MB）                │
│     文件名 + 大小                     │
│     压缩强度滑块（40~250 dpi，         │
│       marks 高清200/均衡150/极致100）  │
│     低于 100 → tips「低于极致」        │
│     预计大小（前端估算）               │
│    [开始压缩] → 结果(源/目标+压缩率)   │
├──────────────────────────────────────┤
│  tab=整理：                           │
│    无工作区：                                                       │
│      [命名 + 压缩档位三选] → 开始整理 →                                │
│      [📷 逐张拍照 / 相册多选]                                        │
│    工作区：                                                         │
│      编号 + 计数 + 命名                                             │
│      图片缩略图列表（可拖/钮调序、删除）                               │
│      [拍照/选图追加] [完成并生成PDF] [放弃]                           │
│      完成后 → 下载/预览 PDF                                         │
├──────────────────────────────────────┤
│  tab=转换：                           │
│    [选 PDF] 文件名+大小               │
│    输出：Word / Markdown radio         │
│    [开始转换] → 结果 → 下载/预览       │
└──────────────────────────────────────┘
```

### 扫描整理 tab 实现要点（对齐 Web ScanDocConvert 工作区）

- 创建：`scan-doc/create?docName=` 拿到 `docId/docNo/docName`。
- 追加：`wx.chooseImage` 多选 或 `wx.chooseMedia` 拍照/相册，逐张校验（jpg/png/bmp/webp、≤50MB）→ 逐张 `wx.uploadFile`（`name:'files'`）→ 成功后刷新全量图片列表（后端返回排序后全量，本地按 id 匹配保留缩略图，同 Web `syncByIdentity`）。
- 排序：本地数组重排后代 `scan-doc/{docId}/order`（body 有序 ids[]，JSON）。
- 删除：`scan-doc/{docId}/images/{imageId}` → 本地移除。
- 放弃：`scan-doc/{docId}` + 本地清空。
- 完成：`scan-doc/{docId}/finalize?maxSide=${lv.maxSide}&quality=${lv.quality}` → 返回 `targetFile`（COS url/大小），前端下载 PDF。
- 缩略图：上传后本地存 `wx.getFileSystemManager` 临时路径或使用 `wx.createImage`？小程序用服务端返回图片 URL 预览（scan-doc 追加后返回的 image 含 url）。

> 首版包含完整扫描整理工作区（用户确认），不做 Web 端「已完成归档后禁止追加」之外的扩展功能。

### 结果处理
- 后端返回 COS URL（http 下载链），小程序不能直接 `window.open`。
- 用 `wx.downloadFile({ url: fileUrl })` 下载本地临时文件 → `wx.saveFile` 或用 `wx.openDocument` 预览。
- 对压缩/转换结果展示 `sourceFile/targetFile` 大小 + 压缩率（同 Web）；扫描整理结果展示 `targetFile.fileName/Size/count`。

## 七、打卡页 checkin（迁移复用）

整套复制 `minservice/pages/checkin/*` + `api/checkin.js`，含印章主题样式。
- `checkin.js` 内跳转 `'/pages/login/login'` 路径不变（同构），无需改。
- 保留月历、今日计划、打卡/取消、新增/编辑/删除、单日事件、日历详情全部交互。

## 八、我的页 profile（新增精简版）

复用 minservice profile 的中式视觉，仅保留核心项：
- 用户信息头（头像/昵称/微信绑定状态）
- 主题切换
- 退出登录

## 九、app.json tabBar

```json
"tabBar": {
  "color": "#94A3B8", "selectedColor": "#3B82F6",
  "backgroundColor": "#FFFFFF", "borderStyle": "white",
  "list": [
    { "pagePath": "pages/index/index", "text": "首页",  ...home.png },
    { "pagePath": "pages/fileconvert/fileconvert", "text": "转换", ...file.png },
    { "pagePath": "pages/checkin/checkin", "text": "打卡", ...checkin.png },
    { "pagePath": "pages/profile/profile", "text": "我的", ...profile.png }
  ]
}
```

## 十、复用与约定

- 用户隔离/权限/响应封装全部复用后端现有逻辑，后端**零新增代码**。
- 主题 CSS 变量体系（paper/ink/seal/gold/hairline/shadow）作为统一视觉效果基准，贯穿首页/文件转换/我的。
- appid 需用户在微信公众平台注册「元灵工具箱」后填入 `project.config.json`。