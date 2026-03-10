# 流程定义处理引擎 - 开发计划

## 开发任务分解

### 第一阶段：数据库设计

| 序号 | 任务 | 文件 |
|------|------|------|
| 1.1 | 创建流程定义表 | sys_process_definition |
| 1.2 | 创建流程节点表 | sys_process_node |
| 1.3 | 创建节点关系表 | sys_process_node_relation |
| 1.4 | 创建流程实例表 | sys_process_instance |
| 1.5 | 创建流程任务表 | sys_process_task |

### 第二阶段：后端核心模块

| 序号 | 任务 | 路径 |
|------|------|------|
| 2.1 | 实体类定义 | workflow/entity/*.java |
| 2.2 | Mapper接口 | workflow/mapper/*.java |
| 2.3 | 枚举类 | workflow/enums/*.java |
| 2.4 | DTO定义 | workflow/dto/*.java |
| 2.5 | 候选人策略 | workflow/strategy/*.java |
| 2.6 | 事件处理器接口 | workflow/handler/*.java |
| 2.7 | 流程引擎核心服务 | workflow/service/ProcessEngine.java |
| 2.8 | 流程定义服务 | workflow/service/ProcessDefinitionService.java |
| 2.9 | 流程实例服务 | workflow/service/ProcessInstanceService.java |
| 2.10 | 任务管理服务 | workflow/service/ProcessTaskService.java |
| 2.11 | Controller层 | workflow/controller/*.java |

### 第三阶段：前端模块

| 序号 | 任务 | 路径 |
|------|------|------|
| 3.1 | API封装 | frontend/src/api/workflow/*.js |
| 3.2 | 流程管理页面 | frontend/src/views/workflow/*.vue |
| 3.3 | 可视化流程设计器 | frontend/src/views/workflow/components/*.vue |

### 第四阶段：初始化数据

| 序号 | 任务 |
|------|------|
| 4.1 | 流程定义初始化SQL（工单提交流程） |
| 4.2 | 菜单权限配置 |

## 核心功能清单

| 功能 | 优先级 |
|------|--------|
| 流程定义管理（CRUD、发布/禁用） | P0 |
| 可视化流程设计器 | P0 |
| 发起流程 | P0 |
| 任务审批（通过/拒绝） | P0 |
| 部门/角色/用户候选人配置 | P0 |
| 会签/或签审批规则 | P1 |
| 指定回退 | P1 |
| 转办 | P2 |
| 流程历史 | P2 |

## API接口清单

| 模块 | 接口 | 方法 |
|------|------|------|
| 流程定义 | /workflow/definition | POST |
| 流程定义 | /workflow/definition/{id} | PUT |
| 流程定义 | /workflow/definition/{id}/publish | POST |
| 流程定义 | /workflow/definition/list | GET |
| 流程定义 | /workflow/definition/{id} | GET |
| 流程实例 | /workflow/instance/start | POST |
| 流程实例 | /workflow/instance/{id}/terminate | POST |
| 流程任务 | /workflow/task/{id}/approve | POST |
| 流程任务 | /workflow/task/{id}/rollback | POST |
| 流程任务 | /workflow/task/{id}/delegate | POST |
| 流程任务 | /workflow/my/tasks | GET |
| 流程任务 | /workflow/my/initiated | GET |
| 流程历史 | /workflow/instance/{id}/history | GET |

## 初始化流程数据

**工单提交流程**（process_key: work_order_submit）

| 节点 | 节点类型 | 审核人配置 | 审批规则 |
|------|----------|------------|----------|
| 开始 | START | - | - |
| 提交到研发部 | APPROVAL | 研发部(dept_id=5)全体成员 | 或签 |
| 结束 | END | - | - |
