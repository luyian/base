
## 2026-07-03 AI 图片生成入口补充成功/失败耗时日志并保留业务异常原始原因

## 2026-07-03 AI 图片生成补充适配器上下文日志并放宽图片接口响应解析格式

## 2026-07-03 AI 图片生成适配器增加配置冲突校验并支持从 chat/completions 自动换算 images/generations 地址

## 2026-07-03 HTTP POST 异常消息补充透传供应商响应体便于定位图片生成失败原因

## 2026-07-03 AI 图片生成按商汤文档改为默认走 images/generations 并默认使用 sensenova-u1-fast

## 2026-07-03 AI 图片生成移除普通聊天模型兜底并对图片模型不可用错误给出明确提示

## 2026-07-03 AI 图片生成支持独立配置图片 API 地址、模型和适配器，并增强图片意图识别

## 2026-07-03 AI 图片生成抽取适配器并支持 OpenAI 图片接口与 ChatCompletions 图片生成链路

## 2026-07-03 AI 对话支持识别图片生成意图并调用 OpenAI 兼容图片生成接口返回 Markdown 图片

## 2026-07-02 高考 AI 推荐输出改为页面友好的概况/依据/分类表格结构并优化聊天表格滚动

## 2026-07-02 高考 AI 推荐默认输出10所学校、显式数量最多50所并按院校去重

## 2026-07-02 高考 AI 推荐支持理科/文科自动兜底新高考科类并将公办民办改为候选软过滤

## 2026-07-02 高考 AI 推荐模型调用失败兜底改为不依赖 OpenAI 异常文案

## 2026-07-02 高考 AI 推荐增加大模型 No user query 异常兜底回退本地推荐

## 2026-07-02 放宽高考 AI 推荐本地工具链识别条件并增加命中日志

## 2026-07-02 高考 AI 推荐识别提前到普通对话和技能对话入口的模型调用之前

## 2026-07-02 高考 AI 推荐改为本地格式化工具结果，避免 OpenAI 兼容接口提示词构建失败

## 2026-07-02 修复高考 AI 推荐在 OpenAI 兼容接口下 Function Calling 二次请求缺少用户消息导致失败的问题

## 2026-07-02 高考 AI 推荐纳入位次换算、专业/地区偏好、院校性质和学费过滤综合排序

## 2026-07-02 AI 技能新增高考报考推荐工具（调用院校分数、专业分数、招生计划查询，支持按分数推荐学校专业）

## 2026-07-02 新增高考数据查询模块（院校分数、专业分数、招生计划分页查询，支持排序字段白名单）

## 2026-07-02 补全高考数据前端查询页面（院校分数、专业分数、招生计划共享筛选表格与路由入口）

## 2026-07-02 修复高考分数实体 985/211 字段映射（is985/is211 显式映射到 is_985/is_211）

## 2026-07-02 高考数据查询接口改为按菜单权限标识校验（院校分数、专业分数、招生计划）

## 2026-07-02 高考数据前端选科要求筛选改为固定下拉选择

## 2026-07-02 新增高考专业招生计划表结构并导入河南2025招生计划数据（按考生省份维度隔离）

## 2026-07-02 新增高考志愿录取分数库表结构（院校录取分数线、专业录取分数线，按考生省份维度隔离，不保留导入溯源字段）

---

## AI 技能新增大盘资金流向查询（2026-06-24）

- Python 服务层：`stock_service.py` 新增 `get_market_fund_flow(days)` 方法，调用东财 `push2his.eastmoney.com` 日K资金流接口（secid=1.000001 上证指数）
- Python 路由层：`stock.py` 新增 `GET /api/stock/market-fund-flow?days=5`，支持 1-30 天参数
- Java Tool 层：`StockDataTools.java` 新增 `getMarketFundFlow(days)` 方法，AI 对话可自动调用查询沪深两市主力/超大单/大单/中单/小单净流入

## AI 技能新增行业板块资金流向 + 个股资金排名（2026-06-24）

- Python 服务层：`stock_service.py` 新增 `get_industry_fund_flow()` — 行业板块主力资金流向排名（fid=f62 按主力净流入排序），返回净流入前10和净流出前10；新增 `get_stock_fund_flow_rank()` — 全市场个股主力净流入TOP20
- Python 路由层：`stock.py` 新增 `GET /api/stock/industry-fund-flow` 和 `GET /api/stock/stock-fund-flow-rank`
- Java Tool 层：`StockDataTools.java` 新增 `getIndustryFundFlow()` 和 `getStockFundFlowRank()`

---

## 小程序首页自选基金置顶功能（2026-06-23）

- 数据库：`stk_fund_watchlist` 表新增 `sort_order` 字段（增量脚本 `db/alter_fund_watchlist_sort.sql`，schema.sql 同步）
- 后端：`FundWatchlist` 实体加 `sortOrder`；`listWatchlistFundsByUserId` 改为按 `sort_order ASC, create_time DESC` 排序并在 Java 层保持顺序；`FundService` 新增 `topWatchlist(fundId)`，置顶值取当前用户最小 sort_order 减一；`FundController` 新增 `PUT /stock/fund/watchlist/{fundId}/top`
- 小程序：首页「我的自选」卡片长按弹出置顶浮层，点击调用置顶接口后刷新；点击空白蒙层关闭浮层（`minservice/pages/index/`、`api/fund.js`）

---

## 分支管理模块添加访问码验证（2026-06-17）

- 新增 `DevApiTokenFilter`：拦截 `/api/dev/**`，双通道验证（JWT 放行 + X-Dev-Token 访问码）
- token 从 `sys_config` 表读取（key=`dev.api.token`），可动态修改无需重启
- 前端免登录场景：首次访问弹访问码输入框，存 localStorage 后免输；已登录用户自动跳过
- 访问码失效时（后端改密码）自动检测 403 并重新弹出验证框

---

## 文件转换新增 PDF 转 Markdown 功能（2026-06-17）

- Python-tools：`pdf_service.py` 新增 `convert_to_markdown` 方法（基于 pymupdf4llm），`pdf.py` 新增 `/api/pdf/to-markdown` 端点，`requirements.txt` 添加 `pymupdf4llm==0.0.17`
- 后端：`FileConvertService` 接口新增 `pdfToMarkdown` 方法，`FileConvertServiceImpl` 实现调用 python-tools 新端点，`FileConvertController` 新增 `/pdf-to-markdown` 端点，校验逻辑提取为 `validatePdfFile` 私有方法复用
- 前端：`FileConvert.vue` 改造为单个"PDF 转换"Tab，新增输出格式 Radio 选择（Word/Markdown），`api/fileConvert.js` 新增 `pdfToMarkdown` 接口

---

## 全局异常处理修复（2026-06-16）

- DataScopeAspect：数据权限过滤失败时抛出 AccessDeniedException 阻断请求（原先静默吞异常导致权限失效）
- GlobalExceptionHandler：BusinessException 日志级别从 error 降为 warn；NullPointerException/兜底 Exception 加 @ResponseStatus(500)；SQLIntegrityConstraintViolation 加 @ResponseStatus(409)；空指针异常提示文本不再暴露异常类型
- Controller 信息泄露修复：FileController、OpenApiFileController、FileConvertController 不再将 e.getMessage() 返回前端
- RuntimeException 替换为 BusinessException：FileConvertServiceImpl（PDF转换/源文件上传）、FileServiceImpl（批量下载）
- FlowableProcessEngineServiceImpl：静默 return/空 catch 改为记录日志
- CandidateAssignmentTaskListener：空 catch 改为 debug 日志
- SqlDataProvider/ServiceDataProvider：count 和 fetchData 失败时抛异常让 ExportEngine 标记任务失败（原先返回 0/空列表掩盖错误）
- DataFactoryImpl：对象转换失败抛异常（原先返回 null）
- ConverterRegistry/DictConverter：空 catch 加 debug/warn 日志
- ExportTaskServiceImpl：静默忽略的 catch 加 warn/debug 日志
- RegionServiceImpl：System.err.println 改为 log.warn
- SecurityUtils（common 和 system 两处）：空 catch 注释改为 debug 日志；移除遗留的 log.info 调试语句

---

- 新建 `com.base.dev` 模块（entity/dto/mapper/service/controller）
- 表 `dev_branch`：编号、标题、PRD链接、生产分支、开发分支、上线时间
- 当前生产分支从 `sys_config`（key=`branch.current_prod`）读取，页面提供更新入口
- 开发分支自动生成：`dev_from_{生产分支}_{编号}`
- 列表按上线时间正序排列（先上线的排前面）
- 卡片标题固定宽度 200px，超长省略，保证各列对齐
- 前端 API 封装 `api/branch.js`，页面由 Frontend Design 设计（卡片样式）
- 权限 SQL：`init_branch.sql`（ID=115，菜单+按钮权限）
- MybatisPlusConfig 新增 `com.base.dev.mapper` 扫描路径

---

## 新增文件转换菜单（2026-06-10）

- 后端新增 `FileConvertController`（`/system/file-convert/pdf-to-word`），通过 RestTemplate 调用 python-tools 转换
- 新增 `FileConvertService`/`FileConvertServiceImpl`，转换前后文件均上传 COS 并写入 sys_file 记录
- 前端新增 `FileConvert.vue` 页面（Tab 式布局，预留扩展位），支持拖拽上传 PDF 并下载转换后的 Word
- 前端新增 `api/fileConvert.js` 接口封装
- 菜单权限 SQL：`init_file_convert_permission.sql`（ID=114，挂系统管理下）

---

## 新增 Python 工具服务 python-tools（2026-06-10）

- 基于 FastAPI 搭建独立 Python HTTP 服务，端口 8100，供 Spring Boot 后端内部调用
- 分层架构：routers（路由）/ services（服务）/ schema（统一响应）/ config（配置）
- 首个功能：PDF 转 Word（`/api/pdf/to-word`），基于 pdf2docx 库实现
- 统一响应格式 `Result` 与后端保持一致（code/message/data）
- 支持 .env 环境变量配置，pyproject.toml 集成 ruff/mypy/pytest 规范
- 生命周期管理：启动创建临时目录，关闭自动清理

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
