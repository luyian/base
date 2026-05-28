
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
