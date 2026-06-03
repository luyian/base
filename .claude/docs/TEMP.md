
---

## 工作流任务（2026-03-10）

- 修复 `MyTask.vue` 审批表单 reactive 初始化字段错误导致的构建失败
- 审批弹窗数据结构恢复为 taskId/approveResult/comment 三字段
- 修复待办任务为空时批量查询流程实例导致的 SQL 语法错误
- 修复动态路由重复挂载导致的 vue-router 无限重定向报错
- 调整前端构建分包策略并提高警告阈值，降低大 chunk 告警
- 新增流程设计页与 404 页面，补齐缺失路由避免无限重定向
- 移除 bpmn-js 图形化设计器，流程编辑恢复为 JSON 表单模式
- 流程编辑页改为表单化节点/连线配置，避免手写 JSON
## 工作流 Flowable 启动修复（2026-05-27）

- 修复后端启动时 Flowable 查询 `ACT_GE_PROPERTY` 报表不存在的问题。
- MySQL 连接 URL 增加 `nullCatalogMeansCurrent=true`，限制 JDBC 元数据表检查只针对当前数据库，避免被其他库中的 `ACT_*` 表误导。
- 涉及配置：`application-dev.yml`、`application-test.yml`、`application-docker.yml`、`application-prod.yml`；影响 Flowable 引擎表自动建表与启动校验，不改变业务接口逻辑。

## 文件管理模块 FastDFS → 腾讯云 COS 迁移（2026-05-28）

- 新建 `CosService`，从 `sys_config` 读取 COS 配置，延迟初始化 `COSClient`
- `FileServiceImpl` 替换所有 `FastDFSClient` 调用为 `CosService`，数据库存 COS key，读时通过 `resolveFileUrl()` 转预签名 URL
- `FileUploadService`/`FileUploadUtil` 改用 COS 上传替代本地磁盘存储
- `UserProfileServiceImpl`/`AuthServiceImpl`/`UserServiceImpl` 返回用户信息时将头像 COS key 转为预签名 URL
- `OpenApiFileController` 上传文件后返回预签名 URL
- 删除 `FastDFSClient.java` 和 `FastDFSConfig.java`
- 批量下载改用 `cosService.downloadFile()` 直接从 COS 下载，移除 HTTP 下载方法

## 基金估值精度优化（2026-05-29）

- 估值算法改为按已知权重等比放大：`totalWeightedChange / totalWeight * 100`
- 新增 `rawWeightedChange` 字段（持仓计算涨跌幅），与 `totalWeight`、`estimatedChangePercent` 构成三值
- 前端详情弹窗和小程序详情页展示三值（持仓计算、持仓占比、整体估算），列表页仍只展示整体估算值
- 小程序详情页估值保留 3 位小数

## 基金估值优化：基准指数填充未覆盖仓位（2026-05-29 已实施）

- 基金配置新增 `benchmarkCode`（基准指数代码），估值计算使用基准指数涨跌填充未覆盖仓位权重
- 公式：`估算涨跌 = 持仓加权涨跌 + (100 - 持仓总权重) × 基准指数涨跌 / 100`
- 未配基准指数时降级为等比放大（原逻辑），基准指数获取失败时同样降级
- 批量刷新和单基金估值均已适配基准指数行情拉取
- 估值持久化和 DB 读取均已映射 `benchmarkCode`、`benchmarkChangePercent`
- `StockQueryRequest` 新增 `stockType` 字段，支持按股票类型（stock/index）筛选
- 前端编辑弹窗新增基准指数搜索选择器，详情弹窗展示基准指数涨跌
- 小程序编辑页新增基准指数代码输入，详情页展示基准指数涨跌
- `alter_fund_benchmark.sql` 包含表结构变更和常用指数基础数据插入

## 知识库功能开发（2026-05-27）

- 新建知识库模块后端，包含 Entity / DTO / Mapper / Service / Controller 全套代码
- 支持知识库 CRUD、多级目录树、Markdown 文档编辑/预览、标签管理、按目录/标签筛选
- API 接口：`/knowledge-base`、`/directories`、`/documents`、`/tags`
- 前端 `KnowledgeBaseList.vue` 和 `KnowledgeBaseDetail.vue` 已对接
- 前端路由新增 `/knowledge-base` 和 `/knowledge-base/:id`
- 菜单初始化 SQL：`init_knowledge_permission.sql`（ID 8/801/80101-80107）
- 已分配给超级管理员和系统管理员角色

## 知识库标签筛选修复（2026-05-29）

- 修复按标签筛选文档永远返回空的 bug：原先写文档只存 `kb_document.tags` JSON，筛选却查空的 `kb_document_tag` 关联表
- 采用方案 B：保留 JSON 字段供前端展示标签名，新增维护 `kb_document_tag` 关联表（存 tagId）供按 tagId 筛选
- 新增 `syncDocumentTags()`：按「知识库ID+标签名」反查 tagId，先清后建重建关联；创建/更新文档时调用（更新仅在显式传 tags 时同步）
- 创建/更新文档方法加 `@Transactional`
- 删除知识库时物理清理其下文档的标签关联；删除标签时清理引用该标签的关联
- 前端 `updateDocument` 三处调用（保存/加标签/移标签）补传 `knowledgeBaseId`，修复 `@NotNull` 校验报「知识库ID不能为空」
- 遗留权衡：改标签名后旧文档 JSON 仍显示旧名（展示走 JSON，筛选走关联表不受影响）

## 知识库增强：附件 + 文档广场 + 评论（2026-06-01）

- 标签展示修复：四处 el-tag 按背景亮度自适应文字色（textColorOf），彩色底也能看清标签名
- Markdown 渲染改用 markdown-it（替换手写正则），支持代码块/加粗/列表/表格等
- 文档导入：详情页「导入文档」读取 .md/.txt 文件内容创建为新文档（纯前端）
- 通用文件关联表 file_link_obj（area_type/link_type 用枚举 FileAreaTypeEnum/FileLinkTypeEnum 区分业务域与关联类型），附件复用 /system/file/upload + CosService.getFileUrl 预签名下载
- 文档附件后端：FileLinkObj/Mapper、附件 DTO、AttachmentController(/attachments)、Service 绑定/列表/删除
- 文档评论 kb_comment 支持一级回复：KbComment/Mapper、CommentController(/comments)；评论人用 SecurityUtils 显式存（MyMetaObjectHandler 的 createBy 填 "system" 不可用）
- 全局文档广场（/document-square，作为二级菜单挂在知识库目录 ID=8 下）：/documents/all 分页(Page)，DocumentResponse 加 knowledgeBaseId/knowledgeBaseName；前端 DocumentSquare.vue 卡片墙 + el-pagination
- 文档详情页 KnowledgeDocDetail.vue（/document/:docId）：markdown-it 正文 + 附件上传/下载/删除 + 评论/一级回复
- 菜单 SQL：init_document_square_menu.sql（sys_permission ID 910/911，分配角色 1/2）
- 建表 SQL：init_knowledge_tables.sql 追加 file_link_obj、kb_comment

## 股票/基金新增北证（BJ）支持（2026-06-01）

- 新建 `MarketUtil` 工具类（stock/util）统一市场推断：4(43)/8(83/87/88)/920 开头识别为北证 BJ，纯 5 位数字为港股 HK
- 替换 5 处分散的 inferMarket：StockServiceImpl.createStock、FundServiceImpl.inferMarketByStockCode、东财/腾讯/iTick 三个 QuoteProvider 均改为委托 MarketUtil
- 腾讯接口：BJ 用 `bj` 前缀拼接（如 bj430047），解析新增 `v_bj` 前缀；修复原 9 开头/5 位一律判港股导致 920xxx 北证误判的 bug
- 东财接口：BJ 复用 `0.` secid 前缀；东财/腾讯 parseQuote 因北证与深市市场码同为 0 无法区分，改为按分组传入的市场回填（新增 defaultMarket 参数）
- StockInfo 实体与 tables.sql 的 market 注释补充 BJ-北证
- 前端 5 个页面（stock 列表/详情/自选/推荐/基金持仓）市场标签新增北证（warning 色），列表/同步弹窗筛选下拉新增「北证 BJ」选项
- 小程序 stock 页面补充北证支持：markets 数组加 BJ、列表标签支持北证显示与样式（亮/暗主题）

## 知识库标签与文档详情优化（2026-06-01）

- 修复自定义色标签关闭按钮（×）显示为灰色方块：`:deep(.el-tag__close)` 继承文字色
- 修复 tags 数组含空值导致空白标签：KnowledgeBaseDetail（列表+编辑区+加载时）、DocumentSquare 卡片标签均加 `.filter(Boolean)` 过滤
- 文档详情页新增目录大纲侧边栏：从 Markdown 标题（h1-h6）自动生成 TOC，点击跳转对应章节，IntersectionObserver 高亮当前可视标题，el-switch 开关控制展开/收起
- 暗色主题适配：KnowledgeDocDetail（详情页+TOC侧栏+评论区）、DocumentSquare（卡片+分页）、KnowledgeBaseDetail（目录树+文档列表+编辑器）三个页面基于全局 `--dk-*` 变量覆盖硬编码颜色

## 基金基准指数市场查询修复（2026-06-02）

- 后端 `FundServiceImpl.calculateValuation`：获取基准指数行情时优先从 `stock_info` 表查询市场，避免 `MarketUtil.inferMarket` 将沪市指数（如000300）误判为深市导致行情查不到
- 后端 `FundServiceImpl.groupStockCodesByMarket`：表内查不到市场时增加 warn 日志，提示回退到代码推断
- 小程序 `edit.js`/`edit.wxml`：基准指数输入框新增搜索建议功能，复用 `stockApi.searchStocks`，选择后自动填入指数代码

## 基金详情页基准指数估值为0修复（2026-06-02）

- 根因：`EastMoneyQuoteProvider`/`ITickQuoteProvider` 未重写带 `marketMap` 的 `getQuotes` 方法，接口默认实现直接忽略市场映射，内部重新推断市场，导致从 `stock_info` 查到的正确市场被丢弃
- 修复：`EastMoneyQuoteProvider` 和 `ITickQuoteProvider` 均新增 `getQuotes(List<String>, Map<String,String>)` 重载，groupByMarket 优先使用传入的 marketMap，只有未传入时才回退到代码推断
- 影响：基金详情页（`/stock/fund/{id}/valuation`）的基准指数行情现在能正确使用股票表中的市场信息，整体估算将按基准指数填充未覆盖仓位，不再降级为等比放大

## 小程序股票自选功能 + 自选页实时行情（2026-06-01）

- 股票列表页新增自选星标按钮：加载时获取用户自选列表构建 watchlistMap，点击星标切换自选状态
- 自选页重构：展示实时行情（现价、涨跌额、涨跌幅），支持手动刷新按钮和下拉刷新
- 后端 WatchlistController 新增 `/stock/watchlist/quotes` 接口，注入 QuoteProviderFactory 复用基金估值的行情数据源
- 小程序 api/watchlist.js 新增 getQuotes 方法调用实时行情接口

## 飞书审批集成 + 事件回调 + 钉钉接口预留（2026-06-02）

- 新增第三方平台抽象层 `com.base.common.thirdparty`：定义 ThirdPartyApprovalService/ThirdPartyEventService/ThirdPartyContactService 接口，飞书实现具体逻辑，钉钉仅预留接口
- 飞书审批服务 `FeishuApprovalService`：对接飞书审批 API（创建/撤销/查询实例），`FeishuApprovalFormBuilder` 根据模板 formMapping 配置自动构建表单
- 飞书 WebSocket 事件回调：`FeishuWebSocketClient` 长连接 + `FeishuEventDispatcher` 先入库再分发 + 审批/通讯录事件处理器
- 审批业务模块 `com.base.approval`：ApprovalInstance/ApprovalTemplate/EventCallbackLog 三表，Service 层支持发起/撤销/回调更新/兜底同步
- 定时任务：EventCallbackRetryTask（5分钟重试失败事件）、ApprovalStatusSyncTask（30分钟兜底同步 PENDING 实例）
- 通讯录同步 `FeishuContactService`：分页拉取飞书用户同步到 sys_user_oauth
- FeishuApiClient 扩展 GET 方法，FeishuConfig 新增事件配置字段（verificationToken/encryptKey/eventEnabled）
- pom.xml 新增 Java-WebSocket 1.5.4 依赖
- 前端新增审批列表/详情/模板管理/事件日志 4 个页面，路由挂在消息中心之后
- DDL 文件：`schema_approval.sql`（tp_approval_template/tp_approval_instance/tp_event_callback_log）

## 全国实时天气地图（2026-06-02）

- 新增 `com.base.weather` 后端模块：WeatherController（/weather/realtime、/weather/city/{adcode}）、WeatherService 调用高德天气 API、CityAdcodeData 静态城市坐标数据（约200个地级市）
- Redis 缓存 30 分钟，避免频繁请求高德 API
- 前端 `views/weather/index.vue`：ECharts 中国地图 + scatter 散点 + visualMap 连续色带（蓝→红表示低温→高温），支持气温/湿度切换、城市点击详情面板
- GeoJSON 数据从 DataV 在线加载（`geo.datav.aliyun.com`）
- 菜单挂在系统管理下（ID=112），菜单 SQL：`init_weather_permission.sql`

## 天气模块多数据源重构（2026-06-03）

- 新增 `WeatherProvider` 接口 + 工厂模式，参考股票模块 `QuoteProvider` 设计
- 实现三个数据源：`AmapWeatherProvider`（高德）、`HefengWeatherProvider`（和风）、`SeniverseWeatherProvider`（心知）
- `WeatherProviderFactory` 支持主/备数据源切换和降级
- `WeatherSourceConfig` 配置类绑定 `weather.*` yml 节点
- `WeatherServiceImpl` 重构：移除硬编码高德调用，改为通过工厂获取 provider，支持自动降级
- `application-dev.yml` 新增 `weather` 配置节，默认使用高德，可配置切换到和风/心知
