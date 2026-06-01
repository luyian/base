
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
- 全局文档广场（独立顶级菜单 /document-square，非知识库内入口）：/documents/all 分页(Page)，DocumentResponse 加 knowledgeBaseId/knowledgeBaseName；前端 DocumentSquare.vue 卡片墙 + el-pagination
- 文档详情页 KnowledgeDocDetail.vue（/document/:docId）：markdown-it 正文 + 附件上传/下载/删除 + 评论/一级回复
- 菜单 SQL：init_document_square_menu.sql（sys_permission ID 910/911，分配角色 1/2）
- 建表 SQL：init_knowledge_tables.sql 追加 file_link_obj、kb_comment
