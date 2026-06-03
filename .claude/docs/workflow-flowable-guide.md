# Flowable 工作流集成使用说明

## 一、概述

项目使用 **Flowable 6.8.1** 作为工作流引擎，前端使用 **bpmn.js 14** 提供可视化流程设计器。支持流程定义设计、发布、发起、审批、回退、转办、终止等完整工作流生命周期。

## 二、环境要求

| 组件 | 版本 |
|------|------|
| Java | 8+ |
| Spring Boot | 2.7.18 |
| Flowable | 6.8.1 |
| MySQL | 5.7+ |
| bpmn.js | 14.x |

## 三、数据库初始化

### 3.1 执行扩展表 SQL

```bash
mysql -u root -p your_database < backend/src/main/resources/db/schema_flowable_ext.sql
```

该脚本会：
1. 创建 `sys_flowable_definition_ext` 扩展表
2. 清空旧的 6 张自定义工作流表数据

### 3.2 Flowable 自动建表

首次启动后端服务时，Flowable 会自动创建约 40 张 `ACT_*` 前缀的表（由 `flowable.database-schema-update: true` 配置控制）。

## 四、后端架构

### 4.1 核心配置

| 文件 | 作用 |
|------|------|
| `application.yml` flowable 段 | Flowable 引擎配置（历史级别、禁用的子模块等） |
| `FlowableConfig.java` | 设置数据库类型为 MySQL、中文字体 |

**关键配置项**：
```yaml
flowable:
  database-schema-update: true    # 自动建表
  async-executor-activate: false  # 关闭异步执行器
  history-level: audit            # 审计级别历史记录
  idm.enabled: false              # 关闭身份管理模块
  cmmn.enabled: false             # 关闭案例管理
  dmn.enabled: false              # 关闭决策管理
```

### 4.2 服务层

| 接口 | 实现类 | 职责 |
|------|--------|------|
| `ProcessDefinitionService` | `FlowableProcessDefinitionServiceImpl` | 流程定义 CRUD、发布/禁用 |
| `ProcessEngineService` | `FlowableProcessEngineServiceImpl` | 流程发起、审批、回退、转办、终止、查询 |

### 4.3 扩展表设计

`sys_flowable_definition_ext` 桥接 Flowable 原生表与业务字段：

| 字段 | 说明 |
|------|------|
| `deployment_id` | 关联 Flowable 部署 |
| `process_definition_id` | 关联 Flowable 流程定义 |
| `process_key` | 流程标识（唯一） |
| `status` | 0=草稿 1=已发布 2=禁用 |
| `bpmn_xml` | BPMN XML 原文（LONGTEXT） |

### 4.4 监听器

| 类 | 类型 | 作用 |
|----|------|------|
| `CandidateAssignmentTaskListener` | TaskListener | 动态分配候选人（用户/角色/部门/部门负责人/发起人部门） |
| `FlowableNodeEventListener` | ExecutionListener | 桥接到 `NodeEventHandler` 扩展机制 |

### 4.5 事件处理扩展

通过 `NodeEventHandler` 接口扩展节点事件处理：

```java
@Component
public class MyCustomHandler implements NodeEventHandler {
    @Override
    public String getHandlerKey() {
        return "myCustomHandler";
    }

    @Override
    public void onEnter(ProcessContext context) {
        // 节点进入时的业务逻辑
    }
}
```

在 BPMN 流程变量中设置 `eventHandler_{activityId} = myCustomHandler` 即可关联。

## 五、API 接口

### 5.1 流程定义管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/workflow/definition` | 创建流程定义（草稿） |
| PUT | `/workflow/definition/{id}` | 更新流程定义 |
| DELETE | `/workflow/definition/{id}` | 删除流程定义 |
| POST | `/workflow/definition/{id}/publish` | 发布到 Flowable 引擎 |
| POST | `/workflow/definition/{id}/disable` | 禁用流程定义 |
| GET | `/workflow/definition/list` | 查询列表（支持 category/keyword/status 过滤） |
| GET | `/workflow/definition/{id}` | 获取详情 |
| GET | `/workflow/definition/{id}/bpmn` | 获取 BPMN XML |

### 5.2 流程实例管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/workflow/instance/start` | 发起流程 |
| POST | `/workflow/instance/{processInstanceId}/terminate` | 终止流程 |
| GET | `/workflow/instance/{processInstanceId}` | 获取实例详情 |
| GET | `/workflow/instance/{processInstanceId}/history` | 获取审批历史 |
| GET | `/workflow/instance/{processInstanceId}/tasks` | 获取当前任务 |

### 5.3 任务处理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/workflow/my/tasks` | 我的待办任务 |
| GET | `/workflow/my/initiated` | 我发起的流程 |
| POST | `/workflow/task/approve` | 审批任务（通过/拒绝） |
| POST | `/workflow/task/rollback` | 回退任务 |
| POST | `/workflow/task/delegate` | 转办任务 |

### 5.4 请求示例

**发起流程：**
```json
POST /workflow/instance/start
{
    "processKey": "leave_approval",
    "businessKey": "LEAVE-2026-001",
    "businessType": "请假审批",
    "title": "张三的请假申请",
    "variables": {
        "days": 3,
        "reason": "个人事务"
    }
}
```

**审批任务：**
```json
POST /workflow/task/approve
{
    "taskId": "12345",
    "approveResult": "APPROVE",
    "comment": "同意请假"
}
```

`approveResult` 取值：`APPROVE`（通过）、`REJECT`（拒绝）。

## 六、前端使用

### 6.1 依赖安装

```bash
cd frontend
npm install bpmn-js@14 --save
```

### 6.2 页面说明

| 页面 | 路径 | 功能 |
|------|------|------|
| ProcessDefinition.vue | `/workflow/definition` | 流程定义列表管理 |
| ProcessDesign.vue | `/workflow/design` | bpmn.js 流程设计器 |
| MyTask.vue | `/workflow/my-tasks` | 我的待办审批 |
| MyInitiated.vue | `/workflow/my-initiated` | 我发起的流程 |

### 6.3 流程设计器功能

- 拖拽式 BPMN 流程设计（基于 bpmn.js）
- 支持新建、编辑、查看模式
- 支持导出 BPMN XML 文件
- 保存时自动将 XML 提交到后端

## 七、流程定义生命周期

```
新建（草稿）→ 编辑 BPMN XML → 保存 → 发布（部署到 Flowable）→ 可发起流程
                                         ↓
                                       禁用（挂起）→ 无法发起新流程
```

- **草稿状态**：仅保存 XML 到扩展表，不部署到 Flowable
- **已发布状态**：XML 已部署，生成 Flowable 流程定义，可发起流程实例
- **禁用状态**：Flowable 流程定义被挂起，无法发起新实例

## 八、候选人配置

在流程变量中设置以下变量控制任务分配：

| candidateType 值 | 说明 | candidateConfig 示例 |
|-------------------|------|---------------------|
| `USER` | 指定用户 | `{"userIds": [1, 2, 3]}` |
| `ROLE` | 按角色分配 | `{"roleIds": [1, 2]}` |
| `DEPARTMENT` | 按部门分配 | `{"deptIds": [10, 20]}` |
| `DEPARTMENT_LEADER` | 部门负责人 | `{"deptIds": [10]}` |
| `INITIATOR_DEPT` | 发起人所在部门 | `{}` |

变量名格式：`candidateType_{activityId}` 和 `candidateConfig_{activityId}`

## 九、注意事项

1. **Java 版本限制**：必须使用 Flowable 6.8.1（7.x 需要 Java 17）
2. **MyBatis 冲突**：已通过 pom.xml exclusion 排除 Flowable 自带的 mybatis 和 mybatis-spring
3. **Liquibase 冲突**：已排除 liquibase-core
4. **ID 类型**：Flowable 的 taskId、processInstanceId 均为 String 类型
5. **历史记录**：配置为 `audit` 级别，记录流程实例、任务、变量等
