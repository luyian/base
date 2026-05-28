# 流程引擎开发与使用指南

> 基于 Flowable 6.8.1 + bpmn-js 14 的工作流模块

## 目录

- [架构概览](#架构概览)
- [快速开始：创建一个审批流程](#快速开始创建一个审批流程)
- [前端流程设计器](#前端流程设计器)
- [候选人配置](#候选人配置)
- [事件回调开发](#事件回调开发)
- [API 接口](#api-接口)
- [BPMN XML 结构说明](#bpmn-xml-结构说明)
- [扩展指南](#扩展指南)

---

## 架构概览

```
前端 (Vue 3 + bpmn-js)                    后端 (Spring Boot + Flowable)
┌──────────────────────┐                  ┌──────────────────────────┐
│  ProcessDesign.vue   │  ── 保存XML ──>  │ ProcessDefinitionService │
│  (BPMN 可视化设计器)  │                  │  (流程定义 CRUD/发布)     │
├──────────────────────┤                  ├──────────────────────────┤
│  NodePropertiesPanel │                  │ ProcessEngineService     │
│  (节点属性面板)       │                  │  (发起/审批/回退/转办)    │
├──────────────────────┤                  ├──────────────────────────┤
│  MyTask.vue          │  ── 审批 ──────> │ CandidateAssignment      │
│  (我的待办)           │                  │  TaskListener (候选人分配)│
├──────────────────────┤                  ├──────────────────────────┤
│  MyInitiated.vue     │                  │ NodeEventHandler         │
│  (我发起的)           │                  │  (审批回调扩展点)         │
└──────────────────────┘                  └──────────────────────────┘
```

### 核心文件

| 模块 | 文件 | 说明 |
|------|------|------|
| 后端-定义服务 | `FlowableProcessDefinitionServiceImpl.java` | 流程定义 CRUD、发布部署到 Flowable |
| 后端-引擎服务 | `FlowableProcessEngineServiceImpl.java` | 发起流程、审批、回退、转办、终止 |
| 后端-候选人 | `CandidateAssignmentTaskListener.java` | 根据配置动态分配审批人 |
| 后端-回调 | `NodeEventHandler.java` / `NodeEventHandlerManager.java` | 审批事件回调扩展点 |
| 前端-设计器 | `ProcessDesign.vue` + `NodePropertiesPanel.vue` | BPMN 可视化设计 + 节点属性编辑 |
| 前端-API | `workflow.js` | 所有流程相关 API 调用 |
| 数据库 | `sys_flowable_definition_ext` | 流程定义扩展表（存储 BPMN XML） |

---

## 快速开始：创建一个审批流程

以「密码变更审批」为例，完整演示创建流程的步骤。

### 第一步：设计流程（前端）

1. 用管理员登录，进入 **流程管理 → 流程定义 → 新建**
2. 填写流程标识（如 `password_change`）、名称、分类
3. 在 bpmn-js 画布上拖拽创建流程：
   - 从左侧工具栏拖入 **开始事件**、**用户任务**、**结束事件**
   - 用箭头连接：开始 → 用户任务 → 结束
4. **点击用户任务节点**，右侧弹出属性面板：
   - 候选人类型：选择「指定角色」
   - 选择角色：勾选「超级管理员」
   - 事件处理器：填写 `password_change`（可选，用于审批回调）
5. 点击「应用配置」，然后点击顶部「保存」

### 第二步：发布流程

在流程定义列表页，找到刚创建的流程，点击 **发布** 按钮。

> 只有「已发布」状态的流程才能被使用。

### 第三步：发起流程（API 调用）

```javascript
import { startProcess } from '@/api/workflow'

await startProcess({
  processKey: 'password_change',           // 流程标识
  businessKey: `pwd_${userId}_${Date.now()}`, // 业务主键
  businessType: 'PASSWORD_CHANGE',          // 业务类型
  title: '张三 申请修改密码',                 // 流程标题
  variables: {                              // 流程变量（可选）
    reason: '密码过期需要更换'
  }
})
```

### 第四步：审批

审批人在 **我的任务** 页面看到待办，点击审批（通过/拒绝）。

### 第五步：查看进度

发起人在 **我发起的** 页面查看流程状态和审批历史。

---

## 前端流程设计器

### 设计器功能

- **bpmn-js 画布**：拖拽式流程图设计，支持开始/结束事件、用户任务、网关等
- **节点属性面板**：点击 UserTask 节点时右侧弹出，可配置候选人和回调
- **导出 XML**：可将设计好的流程导出为 BPMN XML 文件
- **中文翻译**：工具栏和右键菜单已汉化

### 节点属性面板

点击 **用户任务 (UserTask)** 节点时，右侧弹出属性面板，可编辑：

| 配置项 | 说明 |
|--------|------|
| 候选人类型 | 审批人的选择方式（角色/用户/部门等） |
| 候选人配置 | 根据类型选择具体的角色/用户/部门 |
| 事件处理器 | 审批完成后触发的回调处理器标识 |

配置完成后点击「应用配置」，配置会写入 BPMN XML 的 `<flowable:properties>` 中。

---

## 候选人配置

系统支持 5 种候选人分配方式：

| 类型 | 说明 | 配置示例 |
|------|------|----------|
| `ROLE` | 指定角色的所有用户 | 选择「超级管理员」「系统管理员」 |
| `USER` | 指定用户 | 搜索选择具体用户 |
| `DEPARTMENT` | 指定部门的所有成员 | 选择「研发部」 |
| `DEPARTMENT_LEADER` | 指定部门的负责人 | 选择部门 |
| `INITIATOR_DEPT` | 流程发起人所在部门的成员 | 无需配置 |

### 配置存储方式

候选人配置存储在 UserTask 节点的 BPMN 扩展属性中：

```xml
<userTask id="admin_review" name="系统管理员审核">
  <extensionElements>
    <flowable:taskListener event="create"
      delegateExpression="${candidateAssignmentListener}" />
    <flowable:properties>
      <flowable:property name="candidateType" value="ROLE" />
      <flowable:property name="candidateConfig" value='{"roleIds":[1]}' />
    </flowable:properties>
  </extensionElements>
</userTask>
```

### 运行时分配流程

1. Flowable 执行到 UserTask 时触发 `CandidateAssignmentTaskListener`
2. 监听器从节点的 `flowable:properties` 读取 `candidateType` 和 `candidateConfig`
3. 根据类型查询数据库获取候选人列表
4. 单人 → 设为 assignee；多人 → 设为 candidateUsers

---

## 事件回调开发

当审批通过或拒绝后，可以触发自定义业务逻辑。

### 创建 Handler

实现 `NodeEventHandler` 接口，加 `@Component` 注解即可自动注册：

```java
@Component
public class PasswordChangeHandler implements NodeEventHandler {

    @Override
    public String getHandlerKey() {
        return "password_change";  // 与 BPMN 中 eventHandler 属性值对应
    }

    @Override
    public void afterApprove(ProcessContext context) {
        String result = (String) context.getVariables().get("approveResult");
        String initiator = context.getInitiator();

        if ("APPROVE".equals(result)) {
            // 审批通过：执行密码修改、发送通知等
        } else if ("REJECT".equals(result)) {
            // 审批拒绝：通知申请人等
        }
    }

    @Override
    public void onTerminate(ProcessContext context) {
        // 流程被终止时的处理
    }
}
```

### 在 BPMN 中配置

在流程设计器的节点属性面板中，「事件处理器」填写 handler 的 key 值（如 `password_change`）。

对应的 BPMN XML：

```xml
<flowable:property name="eventHandler" value="password_change" />
```

### ProcessContext 可用数据

| 字段 | 说明 |
|------|------|
| `processInstanceId` | 流程实例 ID |
| `activityId` | 当前节点 ID |
| `initiator` | 流程发起人 |
| `operator` | 当前操作人 |
| `action` | 操作类型（APPROVE / REJECT） |
| `comment` | 审批意见 |
| `businessKey` | 业务主键 |
| `variables` | 全部流程变量（包含发起时传入的自定义变量） |

### 回调触发时机

| 事件 | 方法 | 触发条件 |
|------|------|----------|
| `afterApprove` | 审批完成 | 通过或拒绝任务后 |
| `onTerminate` | 流程终止 | 拒绝导致流程被终止 |
| `onEnter` | 节点进入 | 流程执行到该节点时 |
| `beforeApprove` | 审批前 | 预留，当前未使用 |
| `onComplete` | 流程完成 | 流程正常结束 |

---

## API 接口

### 流程定义

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/workflow/definition` | 创建流程定义 |
| PUT | `/workflow/definition/{id}` | 更新流程定义 |
| DELETE | `/workflow/definition/{id}` | 删除流程定义 |
| POST | `/workflow/definition/{id}/publish` | 发布流程定义 |
| POST | `/workflow/definition/{id}/disable` | 禁用流程定义 |
| GET | `/workflow/definition/list` | 查询流程定义列表 |
| GET | `/workflow/definition/{id}` | 获取流程定义详情 |
| GET | `/workflow/definition/{id}/bpmn` | 获取 BPMN XML |

### 流程实例

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/workflow/instance/start` | 发起流程 |
| POST | `/workflow/instance/{id}/terminate` | 终止流程 |
| GET | `/workflow/instance/{id}` | 获取流程实例详情 |
| GET | `/workflow/instance/{id}/history` | 获取审批历史 |
| GET | `/workflow/instance/{id}/tasks` | 获取当前任务列表 |

### 任务处理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/workflow/my/tasks` | 获取我的待办任务 |
| GET | `/workflow/my/initiated` | 获取我发起的流程 |
| POST | `/workflow/task/approve` | 审批任务 |
| POST | `/workflow/task/rollback` | 回退任务 |
| POST | `/workflow/task/delegate` | 转办任务 |

### 发起流程请求体

```json
{
  "processKey": "password_change",
  "businessKey": "pwd_1_1716868800000",
  "businessType": "PASSWORD_CHANGE",
  "title": "张三 申请修改密码",
  "variables": {
    "reason": "密码过期"
  }
}
```

### 审批请求体

```json
{
  "taskId": "12345",
  "approveResult": "APPROVE",
  "comment": "同意"
}
```

---

## BPMN XML 结构说明

### 最小流程模板

```xml
<?xml version="1.0" encoding="UTF-8"?>
<definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL"
             xmlns:flowable="http://flowable.org/bpmn"
             targetNamespace="http://www.flowable.org/processdef">

  <process id="my_process" name="我的流程" isExecutable="true">

    <startEvent id="start" name="开始" />

    <userTask id="review" name="审批节点">
      <extensionElements>
        <!-- 候选人分配监听器（必须） -->
        <flowable:taskListener event="create"
          delegateExpression="${candidateAssignmentListener}" />
        <!-- 节点配置 -->
        <flowable:properties>
          <flowable:property name="candidateType" value="ROLE" />
          <flowable:property name="candidateConfig" value='{"roleIds":[1]}' />
          <flowable:property name="eventHandler" value="my_handler" />
        </flowable:properties>
      </extensionElements>
    </userTask>

    <endEvent id="end" name="结束" />

    <sequenceFlow id="f1" sourceRef="start" targetRef="review" />
    <sequenceFlow id="f2" sourceRef="review" targetRef="end" />

  </process>
</definitions>
```

### flowable:properties 属性说明

| 属性名 | 说明 | 示例值 |
|--------|------|--------|
| `candidateType` | 候选人类型 | `ROLE` / `USER` / `DEPARTMENT` / `DEPARTMENT_LEADER` / `INITIATOR_DEPT` |
| `candidateConfig` | 候选人配置（JSON） | `{"roleIds":[1,2]}` / `{"userIds":[1]}` / `{"deptIds":[5]}` |
| `eventHandler` | 事件回调处理器标识 | `password_change` |

---

## 扩展指南

### 新增审批流程

1. 前端流程设计器创建并配置流程 → 保存 → 发布
2. 在需要发起流程的页面调用 `startProcess()` API
3. （可选）编写 `NodeEventHandler` 实现审批回调

### 新增候选人类型

1. 在 `CandidateType` 枚举中添加新类型
2. 在 `CandidateAssignmentTaskListener.resolveCandidates()` 的 switch 中添加对应分支
3. 在前端 `NodePropertiesPanel.vue` 的 `candidateTypes` 数组中添加选项

### 新增事件回调

1. 创建 Java 类实现 `NodeEventHandler` 接口
2. 加 `@Component` 注解
3. `getHandlerKey()` 返回唯一标识
4. 在流程设计器中为需要的节点配置该标识

### 流程定义生命周期

```
新建（草稿 status=0）→ 编辑 BPMN → 保存 → 发布（status=1，部署到 Flowable）→ 禁用（status=2）
```

### 流程实例生命周期

```
发起（RUNNING）→ 待办任务 → 审批通过 → 完成（COMPLETED）
                          → 审批拒绝 → 终止（TERMINATED）
                          → 手动终止 → 终止（TERMINATED）
```
