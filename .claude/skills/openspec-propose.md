---
name: openspec-propose
aliases: ["opsx:propose"]
description: 提出新变更，生成 proposal / design / specs / tasks
---

# OpenSpec Propose

## 触发方式
用户输入 `/opsx:propose` 或描述一个新功能/变更需求时触发。

## 工作流程

### 1. 需求收集
- 理解用户描述的变更意图
- 确认变更范围和目标
- 识别受影响的模块和文件

### 2. 生成 Proposal（提案）
在 `specs/proposals/` 目录下创建提案文件：

```
specs/proposals/{date}-{slug}.md
```

提案内容包含：
- **背景**：为什么需要这个变更
- **目标**：变更要达成什么
- **范围**：影响哪些模块
- **方案概述**：高层次的实现思路

### 3. 生成 Design（设计）
在 `specs/designs/` 目录下创建设计文件：

```
specs/designs/{date}-{slug}.md
```

设计内容包含：
- **架构设计**：组件交互、数据流
- **接口设计**：API 定义、参数规范
- **数据模型**：表结构、实体关系
- **技术选型**：依赖、框架选择

### 4. 生成 Specs（规格）
在 `specs/specs/` 目录下创建规格文件：

```
specs/specs/{date}-{slug}.md
```

规格内容包含：
- **详细接口规范**：请求/响应格式
- **业务规则**：校验逻辑、边界条件
- **错误处理**：异常场景和响应
- **安全要求**：权限、数据保护

### 5. 生成 Tasks（任务清单）
在 `specs/tasks/` 目录下创建任务文件：

```
specs/tasks/{date}-{slug}.md
```

任务文件格式：
```markdown
# {变更标题} - 任务清单

## 概览
- 提案: specs/proposals/{date}-{slug}.md
- 设计: specs/designs/{date}-{slug}.md
- 规格: specs/specs/{date}-{slug}.md

## 任务列表

- [ ] 任务1：描述
- [ ] 任务2：描述
- [ ] 任务3：描述
...
```

## 输出规范
- 文件名使用 `{YYYY-MM-DD}-{kebab-case-slug}` 格式
- 所有文档使用中文撰写
- 每个文件保持聚焦，职责单一
- 任务粒度适中，每个任务可在一次对话中完成
