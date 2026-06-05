
---

## 知识库文档下载 MD 文件功能（2026-06-05）

- KnowledgeDocDetail 顶部新增"下载 MD"按钮，点击即可将当前文档内容导出为 .md 文件
- 纯前端实现，利用 Blob + URL.createObjectURL 生成下载链接

---

## 知识库编辑器图片上传自动插入（2026-06-04）

- KnowledgeBaseDetail 编辑器新增图片上传功能，支持三种方式：
  - 点击"插入图片"按钮选择文件
  - 粘贴剪贴板图片（Ctrl+V）
  - 拖拽图片文件到编辑区
- 上传到 `/system/file/upload`（fileGroup=knowledge），返回 URL 后自动在光标位置插入 `![name](url)` 语法
- 上传中显示占位符，失败自动清除

---

## 知识库 Markdown 查看功能专业化增强（2026-06-03）

- 新建 `MdViewer.vue` 通用组件，集成 highlight.js 语法高亮 + markdown-it-task-lists
- 代码块：深色主题 + 语言角标 + 一键复制按钮
- 标题：h1 底部分割线、h2 左侧蓝条、层级递进字号
- 表格：表头强调 + 斑马纹 + hover 高亮 + 圆角阴影
- 引用块：渐变背景 + 蓝色左侧条
- 链接：虚线下划线 + 外链箭头图标
- 图片：圆角阴影 + 点击放大预览
- 任务列表：自定义 checkbox 样式
- 全套暗色主题适配
- KnowledgeDocDetail / KnowledgeBaseDetail 预览区统一使用 MdViewer

---

## OCR 智能识别模块（2026-06-03）

- 新增 `com.base.ocr` 模块，采用 DDD 四层架构（interfaces/application/domain/infrastructure）
- 支持三个供应商：腾讯云、百度云、阿里云，通过工厂模式主备切换和降级
- 场景支持：身份证（正反面）、增值税发票、银行卡
- API Key 从 sys_config 表动态读取，yml 配置主/备数据源
- 前端 OCR 识别页面：Tab 切换场景 + 图片拖拽上传 + 结构化结果展示
- 菜单权限 SQL：`init_ocr_permission.sql`（ID=113，挂系统管理下）

---

## 工作流 Flowable 启动修复（2026-05-27）

- 修复后端启动时 Flowable 查询 `ACT_GE_PROPERTY` 报表不存在的问题。
- MySQL 连接 URL 增加 `nullCatalogMeansCurrent=true`，限制 JDBC 元数据表检查只针对当前数据库。

## 文件管理模块 FastDFS → 腾讯云 COS 迁移（2026-05-28）

- `CosService` 从 `sys_config` 读取配置，延迟初始化 `COSClient`
- `FileServiceImpl` 替换所有 FastDFS 调用为 COS，数据库存 COS key，读时转预签名 URL
- 删除 `FastDFSClient.java` 和 `FastDFSConfig.java`

## 基金估值精度优化（2026-05-29）

- 估值算法改为按已知权重等比放大
- 新增基准指数填充未覆盖仓位（`benchmarkCode` 字段）

## 知识库功能开发（2026-05-27）

- 后端 Entity/DTO/Mapper/Service/Controller 全套
- 支持多级目录树、Markdown 文档编辑/预览、标签管理
- 前端 KnowledgeBaseList/Detail 已对接

## 知识库增强：附件 + 文档广场 + 评论（2026-06-01）

- 通用文件关联表 file_link_obj，附件复用 CosService
- 文档评论 kb_comment 支持一级回复
- 全局文档广场（/document-square）
- Markdown 渲染改用 markdown-it

## 股票/基金新增北证（BJ）支持（2026-06-01）

- 新建 `MarketUtil` 统一市场推断，支持北证 BJ 前缀

## 飞书审批集成 + 事件回调 + 钉钉接口预留（2026-06-02）

- 第三方平台抽象层 `com.base.common.thirdparty`
- 飞书 WebSocket 事件回调 + 审批发起/撤销/同步
- 定时任务：事件重试 + 审批状态兜底同步

## 全国实时天气地图（2026-06-02）

- `com.base.weather` 模块，高德/和风/心知多数据源
- ECharts 中国地图 + 散点可视化

## 天气模块多数据源重构（2026-06-03）

- `WeatherProvider` 接口 + 工厂模式，支持主/备数据源切换和降级

## 修复首次访问登录页弹出两次"未授权"提示（2026-06-03）

- 路由守卫 + 请求拦截器 401 防抖

## 天气模块高德 QPS 超限修复（2026-06-03）

- 请求间加 200ms 间隔，QPS 超限时自动切换降级数据源
- adcode 编码补零修复
