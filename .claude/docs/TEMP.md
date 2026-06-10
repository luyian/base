
---

## AI 技能扩展 — Function Calling（2026-06-10）

- 新增 `AiSkillConfig` 配置类（`ai.skill` 配置节，控制 Python 路径、超时、开关）
- 新增 `PythonExecutor` 执行器，通过 ProcessBuilder 调用 Python 脚本，临时文件方式避免命令行长度限制
- 新增 `StockScripts` 脚本模板类，内置 10 个数据脚本（行情/资金流/新闻/行业排名/北向/龙虎榜/强势股/板块/融资融券/估值）
- 新增 `StockDataTools`（@Tool 注解），暴露 10 个技能方法给 LangChain4j Function Calling
- 改造 `AiServiceImpl`，新增 `chatWithSkills` 方法，使用 LangChain4j AiServices 构建带 Tools 的代理
- 改造 `AiController`，根据 `enableSkills` 字段选择对话模式
- 改造 `ChatRequest` DTO，新增 `enableSkills` 字段（默认 true）
- 前端 Dashboard 快捷问题改为股票数据相关，对话默认启用技能
- `application-dev.yml` 新增 `ai.skill` 配置节

---

- 将 `sys_enum` 单表拆分为 `sys_dict_type`（字典类型表）+ `sys_dict_data`（字典数据表）
- 后端：新建 DictType/DictData 实体、Mapper、Service、Controller，路径改为 `/system/dict/type` 和 `/system/dict/data`
- 前端：新建 `api/dict.js` 和 `views/system/Dict.vue`，支持字典类型分页管理 + 数据项弹窗 CRUD
- 权限标识从 `system:enum:*` 改为 `system:dict:*`
- 更新 DictConverter、StockServiceImpl、WatchlistServiceImpl 引用新接口
- 删除旧的 Enum 模块全部文件（实体、Mapper、Service、Controller、DTO、前端 API 和页面）
- 数据迁移 SQL：`backend/src/main/resources/db/alter_dict_split.sql`

---

## 新增分布式流水号生成工具（2026-06-09）

- 新增 `SerialNumberUtil`（`com.base.common.util`），基于 Redis Lua 脚本原子自增实现分布式唯一流水号
- 支持三种模式：日期+序号（默认6位）、自定义序号位数、带时间戳精确到秒
- 风险防护：Lua原子化INCR+EXPIRE、序号溢出校验、Redis宕机本地AtomicLong降级、时钟回拨检测

## 新增 Redis + 注解接口限流工具（2026-06-09）

- 新增 `@RateLimit` 注解（`com.base.common.annotation`），支持自定义 key、时间窗口、最大次数、提示消息
- 新增 `RateLimitAspect` 切面（`com.base.common.aspect`），基于 Redis 计数器实现限流
- 默认同一用户对同一接口 5 秒内只能调用 1 次，未登录用户按 IP 限流

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
