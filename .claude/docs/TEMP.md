
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
