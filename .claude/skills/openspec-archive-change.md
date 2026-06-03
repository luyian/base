---
name: openspec-archive-change
aliases: ["opsx:archive"]
description: 完成后归档，合并规范
---

# OpenSpec Archive Change

## 触发方式
用户输入 `/opsx:archive` 时触发，通常在所有任务完成后执行。

## 前置条件
- `specs/tasks/` 中对应的任务文件所有任务已完成（全部 `- [x]`）
- 代码变更已实现并验证

## 工作流程

### 1. 验证完成状态
- 读取任务文件，确认所有任务都已标记完成
- 如果有未完成任务，提醒用户先完成或确认跳过

### 2. 生成归档摘要
创建归档文件：

```
specs/archive/{date}-{slug}.md
```

归档内容包含：
```markdown
# {变更标题} - 归档记录

## 基本信息
- 提案日期: {propose 日期}
- 完成日期: {当前日期}
- 状态: ✅ 已完成

## 变更摘要
{一段话描述本次变更完成了什么}

## 涉及文件
- 新增: {列出新增文件}
- 修改: {列出修改文件}
- 删除: {列出删除文件}

## 关键决策记录
{在实现过程中做出的重要技术决策}

## 相关文档
- 提案: specs/proposals/{date}-{slug}.md
- 设计: specs/designs/{date}-{slug}.md
- 规格: specs/specs/{date}-{slug}.md
- 任务: specs/tasks/{date}-{slug}.md
```

### 3. 移动源文件到归档
将以下文件移动到 `specs/archive/` 子目录中保留：

```
specs/archive/{date}-{slug}/
├── proposal.md    ← 从 specs/proposals/ 移入
├── design.md      ← 从 specs/designs/ 移入
├── specs.md       ← 从 specs/specs/ 移入
├── tasks.md       ← 从 specs/tasks/ 移入
└── summary.md     ← 归档摘要
```

### 4. 清理工作目录
- 从 `specs/proposals/`、`specs/designs/`、`specs/specs/`、`specs/tasks/` 中移除已归档文件
- 保持工作目录整洁，只保留进行中的变更

### 5. 更新变更记录
确认 `.claude/docs/TEMP.md` 中已记录本次变更的摘要。

### 6. Git 暂存
将归档相关的文件变动添加到 git 暂存区。

## 归档原则
- 归档是知识沉淀，便于未来回溯决策过程
- 保持归档文件精简，去除过程中的草稿内容
- 归档后工作目录保持干净
- 每个完成的变更都应该有对应的归档记录
