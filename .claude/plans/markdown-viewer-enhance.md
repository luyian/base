# Markdown 查看功能增强方案

## 现状分析

当前 `KnowledgeDocDetail.vue`（文档查看）和 `KnowledgeBaseDetail.vue`（编辑器预览）使用 `markdown-it` 做基础渲染，但存在以下不足：

1. **代码块无语法高亮** — 只有灰色背景，不区分语言
2. **无代码复制按钮** — 用户必须手动选中复制
3. **标题层级区分度低** — 仅靠字号区分，缺少边距和视觉标记
4. **表格样式朴素** — 无斑马纹、表头无强调
5. **不支持任务列表** — `- [ ]` / `- [x]` 不渲染为 checkbox
6. **链接无外链标识** — 无法区分内部/外部链接
7. **渲染逻辑重复** — 两个页面各自初始化 markdown-it，没有复用
8. **图片无交互** — 无法点击放大查看

## 实现方案

### 1. 安装依赖

```bash
cd frontend
npm install highlight.js markdown-it-task-lists
```

- `highlight.js` — 代码语法高亮（业界主流，支持 190+ 语言）
- `markdown-it-task-lists` — 支持 GFM 任务列表

### 2. 新建复用组件 `MdViewer.vue`

路径：`frontend/src/components/MdViewer.vue`

职责：
- 接收 `content` prop，输出渲染后的 Markdown HTML
- 集成 highlight.js 语法高亮
- 集成 task-lists 插件
- 代码块自动注入「复制」按钮（点击复制到剪贴板）
- 图片点击放大（使用 Element Plus 的 `el-image-viewer`）
- 暴露 `tocList` 事件供父组件做目录大纲

### 3. 专业排版样式 `md-viewer.css`

路径：`frontend/src/styles/md-viewer.css`

涵盖：
- **标题**：h1 底部分割线、h2 左侧色块标记、h3-h6 递进字号 + 间距
- **代码块**：语言角标 + 复制按钮 + 圆角 + 深色代码主题（`github-dark` 或 `atom-one-dark`）
- **行内代码**：带颜色高亮的 pill 样式
- **表格**：表头深色背景 + 斑马纹 + 圆角
- **引用块**：左侧渐变色条 + 浅色背景填充
- **任务列表**：自定义 checkbox 样式
- **链接**：带下划线 + 外部链接追加图标
- **图片**：圆角 + 阴影 + hover 放大提示
- **分割线**：渐变虚线样式
- **暗色主题**：全套 dark 变量适配

### 4. 改造两个页面

| 页面 | 改动 |
|------|------|
| `KnowledgeDocDetail.vue` | 移除内联 md 实例和样式，改用 `<MdViewer :content="doc.content" />` |
| `KnowledgeBaseDetail.vue` | 预览区改用 `<MdViewer :content="editingContent" />` |

### 5. 文件变更清单

| 操作 | 文件 |
|------|------|
| 新增 | `frontend/src/components/MdViewer.vue` |
| 新增 | `frontend/src/styles/md-viewer.css` |
| 修改 | `frontend/src/views/KnowledgeDocDetail.vue` |
| 修改 | `frontend/src/views/KnowledgeBaseDetail.vue` |
| 修改 | `frontend/package.json`（新增依赖） |

### 6. 不涉及后端改动

纯前端渲染层优化，无接口变更。
