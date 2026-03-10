package com.base.workflow.service.impl;

import cn.hutool.json.JSONUtil;
import com.base.system.entity.SysUser;
import com.base.system.mapper.SysUserMapper;
import com.base.workflow.dto.*;
import com.base.workflow.entity.*;
import com.base.workflow.enums.*;
import com.base.workflow.handler.NodeEventHandlerManager;
import com.base.workflow.handler.ProcessContext;
import com.base.workflow.mapper.*;
import com.base.workflow.service.ProcessEngineService;
import com.base.workflow.strategy.CandidateStrategyContext;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程引擎服务实现
 */
@Service
public class ProcessEngineServiceImpl implements ProcessEngineService {

    @Autowired
    private ProcessDefinitionMapper processDefinitionMapper;

    @Autowired
    private ProcessNodeMapper processNodeMapper;

    @Autowired
    private ProcessNodeRelationMapper processNodeRelationMapper;

    @Autowired
    private ProcessInstanceMapper processInstanceMapper;

    @Autowired
    private ProcessTaskMapper processTaskMapper;

    @Autowired
    private ProcessHistoryMapper processHistoryMapper;

    @Autowired
    private CandidateStrategyContext candidateStrategyContext;

    @Autowired
    private NodeEventHandlerManager eventHandlerManager;

    @Autowired
    private SysUserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProcessInstance startProcess(StartProcessRequest request, String operator) {
        // 获取流程定义
        ProcessDefinition definition = processDefinitionMapper.selectOne(
                new LambdaQueryWrapper<ProcessDefinition>()
                        .eq(ProcessDefinition::getProcessKey, request.getProcessKey())
                        .eq(ProcessDefinition::getStatus, DefinitionStatus.PUBLISHED.getCode())
        );

        if (definition == null) {
            throw new RuntimeException("流程未找到或未发布");
        }

        // 获取发起人信息
        SysUser initiator = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, operator)
        );

        // 创建流程实例
        ProcessInstance instance = new ProcessInstance();
        instance.setProcessKey(definition.getProcessKey());
        instance.setProcessName(definition.getProcessName());
        instance.setProcessDefinitionId(definition.getId());
        instance.setBusinessKey(request.getBusinessKey());
        instance.setBusinessType(request.getBusinessType());
        instance.setInitiator(operator);
        instance.setInitiatorDeptId(initiator != null ? initiator.getDeptId() : null);
        instance.setTitle(request.getTitle());
        instance.setStatus(ProcessStatus.RUNNING.getCode());
        instance.setVariables(request.getVariables() != null ? JSONUtil.toJsonStr(request.getVariables()) : null);
        instance.setStartTime(LocalDateTime.now());
        processInstanceMapper.insert(instance);

        // 获取开始节点
        List<ProcessNode> nodes = processNodeMapper.selectByProcessId(definition.getId());
        ProcessNode startNode = nodes.stream()
                .filter(n -> NodeType.START.getCode().equals(n.getNodeType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("流程未配置开始节点"));

        // 获取开始节点的下一个节点
        List<ProcessNodeRelation> relations = processNodeRelationMapper.selectByProcessId(definition.getId());
        ProcessNodeRelation firstRelation = relations.stream()
                .filter(r -> r.getSourceNodeKey().equals(startNode.getNodeKey()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("流程未配置连线"));

        ProcessNode nextNode = nodes.stream()
                .filter(n -> n.getNodeKey().equals(firstRelation.getTargetNodeKey()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("流程节点未找到"));

        // 更新实例当前节点
        instance.setCurrentNodeKey(nextNode.getNodeKey());
        instance.setCurrentNodeName(nextNode.getNodeName());
        processInstanceMapper.updateById(instance);

        // 创建任务
        createTasks(instance, nextNode, operator);

        // 记录历史
        saveHistory(instance, nextNode.getNodeKey(), nextNode.getNodeName(), operator, "START", null, null);

        // 触发节点进入事件
        triggerNodeEvent(instance, nextNode, null, "ENTER");

        return instance;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveTask(ApproveTaskRequest request, String operator) {
        ProcessTask task = processTaskMapper.selectById(request.getTaskId());
        if (task == null) {
            throw new RuntimeException("任务未找到");
        }

        if (!TaskStatus.PENDING.getCode().equals(task.getStatus())) {
            throw new RuntimeException("任务不是待办状态");
        }

        if (!task.getAssignee().equals(operator)) {
            throw new RuntimeException("不是您的任务");
        }

        ProcessInstance instance = processInstanceMapper.selectById(task.getInstanceId());
        ProcessDefinition definition = processDefinitionMapper.selectOne(
                new LambdaQueryWrapper<ProcessDefinition>()
                        .eq(ProcessDefinition::getProcessKey, task.getProcessKey())
                        .eq(ProcessDefinition::getStatus, DefinitionStatus.PUBLISHED.getCode())
        );
        List<ProcessNode> nodes = processNodeMapper.selectByProcessId(definition.getId());
        ProcessNode currentNode = nodes.stream()
                .filter(n -> n.getNodeKey().equals(task.getNodeKey()))
                .findFirst()
                .orElse(null);

        // 触发审批前事件
        triggerBeforeApproveEvent(instance, currentNode, task, operator);

        // 更新任务状态
        task.setStatus(TaskStatus.COMPLETED.getCode());
        task.setApproveResult(request.getApproveResult());
        task.setComment(request.getComment());
        task.setCompleteTime(LocalDateTime.now());
        processTaskMapper.updateById(task);

        // 判断审批类型
        boolean pass = "APPROVE".equals(request.getApproveResult());

        if (!pass) {
            // 拒绝
            handleReject(instance, currentNode, task, operator);
            return;
        }

        // 获取审批类型
        String approveType = currentNode != null ? currentNode.getApproveType() : null;
        if (approveType == null) {
            approveType = ApproveType.OR_SIGN.getCode();
        }

        if (ApproveType.OR_SIGN.getCode().equals(approveType)) {
            // 或签：任意一人通过即可
            handleOrSign(instance, definition, nodes, currentNode, task, operator);
        } else if (ApproveType.COUNTER_SIGN.getCode().equals(approveType)) {
            // 会签：所有人通过才算通过
            handleCounterSign(instance, definition, nodes, currentNode, task, operator);
        } else {
            // 顺序审批
            handleSequence(instance, definition, nodes, currentNode, task, operator);
        }
    }

    private void handleOrSign(ProcessInstance instance, ProcessDefinition definition,
                              List<ProcessNode> nodes, ProcessNode currentNode,
                              ProcessTask completedTask, String operator) {
        // 或签：直接推进到下一节点
        moveToNextNode(instance, definition, nodes, currentNode, operator, completedTask);
    }

    private void handleCounterSign(ProcessInstance instance, ProcessDefinition definition,
                                   List<ProcessNode> nodes, ProcessNode currentNode,
                                   ProcessTask completedTask, String operator) {
        // 会签：检查是否所有人都通过了
        List<ProcessTask> parallelTasks = processTaskMapper.selectByInstanceIdAndNodeKey(
                instance.getId(), currentNode.getNodeKey());

        boolean allApproved = parallelTasks.stream()
                .allMatch(t -> "APPROVE".equals(t.getApproveResult()));

        if (allApproved) {
            // 全部通过，推进到下一节点
            moveToNextNode(instance, definition, nodes, currentNode, operator, completedTask);
        } else {
            // 等待其他人审批
        }
    }

    private void handleSequence(ProcessInstance instance, ProcessDefinition definition,
                                 List<ProcessNode> nodes, ProcessNode currentNode,
                                 ProcessTask completedTask, String operator) {
        // 顺序审批：直接推进到下一节点
        moveToNextNode(instance, definition, nodes, currentNode, operator, completedTask);
    }

    private void moveToNextNode(ProcessInstance instance, ProcessDefinition definition,
                                List<ProcessNode> nodes, ProcessNode currentNode,
                                String operator, ProcessTask completedTask) {
        // 获取当前节点的下一个节点
        List<ProcessNodeRelation> relations = processNodeRelationMapper.selectByProcessId(definition.getId());
        ProcessNodeRelation nextRelation = relations.stream()
                .filter(r -> r.getSourceNodeKey().equals(currentNode.getNodeKey()))
                .findFirst()
                .orElse(null);

        if (nextRelation == null) {
            // 没有下一个节点，流程结束
            completeProcess(instance, operator);
            return;
        }

        ProcessNode nextNode = nodes.stream()
                .filter(n -> n.getNodeKey().equals(nextRelation.getTargetNodeKey()))
                .findFirst()
                .orElse(null);

        if (nextNode == null) {
            throw new RuntimeException("下一节点未找到");
        }

        if (NodeType.END.getCode().equals(nextNode.getNodeType())) {
            // 结束节点
            completeProcess(instance, operator);
            return;
        }

        // 更新实例当前节点
        instance.setCurrentNodeKey(nextNode.getNodeKey());
        instance.setCurrentNodeName(nextNode.getNodeName());
        processInstanceMapper.updateById(instance);

        // 创建下一节点任务
        createTasks(instance, nextNode, operator);

        // 记录历史
        saveHistory(instance, nextNode.getNodeKey(), nextNode.getNodeName(), operator, "APPROVE", null, completedTask.getComment());

        // 触发节点进入事件
        triggerNodeEvent(instance, nextNode, null, "ENTER");
    }

    private void handleReject(ProcessInstance instance, ProcessNode currentNode,
                              ProcessTask task, String operator) {
        // 拒绝：流程终止
        instance.setStatus(ProcessStatus.TERMINATED.getCode());
        instance.setEndTime(LocalDateTime.now());
        processInstanceMapper.updateById(instance);

        // 终止所有待办任务
        processTaskMapper.update(null,
                new LambdaUpdateWrapper<ProcessTask>()
                        .eq(ProcessTask::getInstanceId, instance.getId())
                        .eq(ProcessTask::getStatus, TaskStatus.PENDING.getCode())
                        .set(ProcessTask::getStatus, TaskStatus.REJECTED.getCode())
        );

        // 记录历史
        saveHistory(instance, currentNode.getNodeKey(), currentNode.getNodeName(),
                operator, "REJECT", null, task.getComment());
    }

    private void completeProcess(ProcessInstance instance, String operator) {
        instance.setStatus(ProcessStatus.COMPLETED.getCode());
        instance.setEndTime(LocalDateTime.now());
        instance.setCurrentNodeKey(null);
        instance.setCurrentNodeName(null);
        processInstanceMapper.updateById(instance);

        // 记录历史
        saveHistory(instance, "end", "结束", operator, "COMPLETE", null, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollbackTask(RollbackTaskRequest request, String operator) {
        ProcessTask task = processTaskMapper.selectById(request.getTaskId());
        if (task == null) {
            throw new RuntimeException("任务未找到");
        }

        if (!TaskStatus.PENDING.getCode().equals(task.getStatus())) {
            throw new RuntimeException("任务不是待办状态");
        }

        ProcessInstance instance = processInstanceMapper.selectById(task.getInstanceId());
        ProcessDefinition definition = processDefinitionMapper.selectOne(
                new LambdaQueryWrapper<ProcessDefinition>()
                        .eq(ProcessDefinition::getProcessKey, task.getProcessKey())
                        .eq(ProcessDefinition::getStatus, DefinitionStatus.PUBLISHED.getCode())
        );
        List<ProcessNode> nodes = processNodeMapper.selectByProcessId(definition.getId());

        // 获取目标节点
        ProcessNode targetNode = nodes.stream()
                .filter(n -> n.getNodeKey().equals(request.getTargetNodeKey()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("目标节点未找到"));

        if (targetNode.getCanRollback() == null || targetNode.getCanRollback() != 1) {
            throw new RuntimeException("该节点不允许回退");
        }

        // 检查目标节点是否在允许回退列表中
        if (targetNode.getRollbackNodes() != null && !targetNode.getRollbackNodes().isEmpty()) {
            List<String> allowedNodes = Arrays.asList(targetNode.getRollbackNodes().split(","));
            if (!allowedNodes.contains(task.getNodeKey())) {
                throw new RuntimeException("该节点不允许回退到此节点");
            }
        }

        // 终止当前待办任务
        task.setStatus(TaskStatus.REJECTED.getCode());
        task.setComment(request.getComment());
        task.setCompleteTime(LocalDateTime.now());
        processTaskMapper.updateById(task);

        // 更新实例当前节点
        instance.setCurrentNodeKey(targetNode.getNodeKey());
        instance.setCurrentNodeName(targetNode.getNodeName());
        instance.setStatus(ProcessStatus.RUNNING.getCode());
        processInstanceMapper.updateById(instance);

        // 创建回退后的任务
        createTasks(instance, targetNode, operator);

        // 记录历史
        saveHistory(instance, targetNode.getNodeKey(), targetNode.getNodeName(),
                operator, "ROLLBACK", null, request.getComment());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delegateTask(DelegateTaskRequest request, String operator) {
        ProcessTask task = processTaskMapper.selectById(request.getTaskId());
        if (task == null) {
            throw new RuntimeException("任务未找到");
        }

        if (!TaskStatus.PENDING.getCode().equals(task.getStatus())) {
            throw new RuntimeException("任务不是待办状态");
        }

        // 转办给其他人
        task.setAssignee(request.getDelegateTo());
        task.setStatus(TaskStatus.DELEGATED.getCode());
        task.setComment(request.getComment());
        task.setCompleteTime(LocalDateTime.now());
        processTaskMapper.updateById(task);

        // 创建新任务
        ProcessTask newTask = new ProcessTask();
        newTask.setInstanceId(task.getInstanceId());
        newTask.setProcessKey(task.getProcessKey());
        newTask.setNodeKey(task.getNodeKey());
        newTask.setNodeName(task.getNodeName());
        newTask.setTaskType(task.getTaskType());
        newTask.setAssignee(request.getDelegateTo());
        newTask.setStatus(TaskStatus.PENDING.getCode());
        newTask.setApproveType(task.getApproveType());
        newTask.setCreateTime(LocalDateTime.now());
        processTaskMapper.insert(newTask);

        // 记录历史
        ProcessInstance instance = processInstanceMapper.selectById(task.getInstanceId());
        saveHistory(instance, task.getNodeKey(), task.getNodeName(),
                operator, "DELEGATE", request.getDelegateTo(), request.getComment());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void terminateProcess(Long instanceId, String operator, String comment) {
        ProcessInstance instance = processInstanceMapper.selectById(instanceId);
        if (instance == null) {
            throw new RuntimeException("流程实例未找到");
        }

        if (!ProcessStatus.RUNNING.getCode().equals(instance.getStatus())) {
            throw new RuntimeException("流程不是进行中状态");
        }

        // 终止流程
        instance.setStatus(ProcessStatus.TERMINATED.getCode());
        instance.setEndTime(LocalDateTime.now());
        processInstanceMapper.updateById(instance);

        // 终止所有待办任务
        processTaskMapper.update(null,
                new LambdaUpdateWrapper<ProcessTask>()
                        .eq(ProcessTask::getInstanceId, instanceId)
                        .eq(ProcessTask::getStatus, TaskStatus.PENDING.getCode())
                        .set(ProcessTask::getStatus, TaskStatus.REJECTED.getCode())
        );

        // 记录历史
        saveHistory(instance, instance.getCurrentNodeKey(), instance.getCurrentNodeName(),
                operator, "TERMINATE", null, comment);
    }

    @Override
    public List<TaskResponse> getMyPendingTasks(String assignee) {
        List<ProcessTask> tasks = processTaskMapper.selectPendingTasks(assignee);
        return convertToTaskResponse(tasks);
    }

    @Override
    public List<ProcessInstance> getMyInitiatedProcesses(String initiator) {
        return processInstanceMapper.selectList(
                new LambdaQueryWrapper<ProcessInstance>()
                        .eq(ProcessInstance::getInitiator, initiator)
                        .orderByDesc(ProcessInstance::getCreateTime)
        );
    }

    @Override
    public List<ProcessHistory> getProcessHistory(Long instanceId) {
        return processHistoryMapper.selectByInstanceId(instanceId);
    }

    @Override
    public List<ProcessTask> getCurrentTasks(Long instanceId) {
        return processTaskMapper.selectByInstanceId(instanceId);
    }

    @Override
    public ProcessDefinitionResponse getProcessDefinitionDetail(Long id) {
        ProcessDefinition definition = processDefinitionMapper.selectById(id);
        if (definition == null) {
            return null;
        }

        ProcessDefinitionResponse response = ProcessDefinitionResponse.fromEntity(definition);

        List<ProcessNode> nodes = processNodeMapper.selectByProcessId(id);
        List<ProcessDefinitionResponse.NodeResponse> nodeResponses = nodes.stream()
                .map(n -> {
                    ProcessDefinitionResponse.NodeResponse nr = new ProcessDefinitionResponse.NodeResponse();
                    nr.setId(n.getId());
                    nr.setNodeKey(n.getNodeKey());
                    nr.setNodeName(n.getNodeName());
                    nr.setNodeType(n.getNodeType());
                    nr.setEventHandler(n.getEventHandler());
                    nr.setCandidateType(n.getCandidateType());
                    nr.setCandidateConfig(n.getCandidateConfig());
                    nr.setApproveType(n.getApproveType());
                    nr.setApproveRatio(n.getApproveRatio());
                    nr.setCanRollback(n.getCanRollback());
                    nr.setRollbackNodes(n.getRollbackNodes());
                    nr.setPositionX(n.getPositionX());
                    nr.setPositionY(n.getPositionY());
                    return nr;
                })
                .collect(Collectors.toList());
        response.setNodes(nodeResponses);

        List<ProcessNodeRelation> relations = processNodeRelationMapper.selectByProcessId(id);
        List<ProcessDefinitionResponse.NodeRelationResponse> relationResponses = relations.stream()
                .map(r -> {
                    ProcessDefinitionResponse.NodeRelationResponse rr = new ProcessDefinitionResponse.NodeRelationResponse();
                    rr.setId(r.getId());
                    rr.setSourceNodeKey(r.getSourceNodeKey());
                    rr.setTargetNodeKey(r.getTargetNodeKey());
                    rr.setConditionExpr(r.getConditionExpr());
                    rr.setRelationName(r.getRelationName());
                    return rr;
                })
                .collect(Collectors.toList());
        response.setNodeRelations(relationResponses);

        return response;
    }

    @Override
    public ProcessInstance getProcessInstanceDetail(Long instanceId) {
        return processInstanceMapper.selectById(instanceId);
    }

    private void createTasks(ProcessInstance instance, ProcessNode node, String operator) {
        if (node.getCandidateType() == null || node.getCandidateType().isEmpty()) {
            // 没有候选人配置，跳过此节点
            return;
        }

        List<Long> candidateIds = candidateStrategyContext.resolveCandidates(node, instance);
        if (candidateIds == null || candidateIds.isEmpty()) {
            // 没有找到候选人
            return;
        }

        String approveType = node.getApproveType();
        if (approveType == null) {
            approveType = ApproveType.OR_SIGN.getCode();
        }

        if (ApproveType.COUNTER_SIGN.getCode().equals(approveType)) {
            // 会签：为每个候选人创建任务
            for (Long userId : candidateIds) {
                createTask(instance, node, userId, "PARALLEL");
            }
        } else {
            // 或签/顺序：只创建一个任务
            Long userId = candidateIds.get(0);
            createTask(instance, node, userId, "NORMAL");
        }
    }

    private void createTask(ProcessInstance instance, ProcessNode node, Long userId, String taskType) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            return;
        }

        ProcessTask task = new ProcessTask();
        task.setInstanceId(instance.getId());
        task.setProcessKey(instance.getProcessKey());
        task.setNodeKey(node.getNodeKey());
        task.setNodeName(node.getNodeName());
        task.setTaskType(taskType);
        task.setAssignee(user.getUsername());
        task.setStatus(TaskStatus.PENDING.getCode());
        task.setApproveType(node.getApproveType());
        task.setCreateTime(LocalDateTime.now());
        processTaskMapper.insert(task);
    }

    private void saveHistory(ProcessInstance instance, String nodeKey, String nodeName,
                             String operator, String action, String assignee, String comment) {
        SysUser operatorUser = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, operator)
        );

        ProcessHistory history = new ProcessHistory();
        history.setInstanceId(instance.getId());
        history.setProcessKey(instance.getProcessKey());
        history.setNodeKey(nodeKey);
        history.setNodeName(nodeName);
        history.setOperator(operator);
        history.setOperatorName(operatorUser != null ? operatorUser.getNickname() : null);
        history.setAction(action);
        history.setAssignee(assignee);
        history.setComment(comment);
        history.setCreateTime(LocalDateTime.now());
        processHistoryMapper.insert(history);
    }

    private void triggerNodeEvent(ProcessInstance instance, ProcessNode node,
                                   ProcessTask task, String action) {
        ProcessContext context = new ProcessContext(instance, node);
        context.setCurrentTask(task);
        context.setAction(action);
        eventHandlerManager.triggerOnEnter(context);
    }

    private void triggerBeforeApproveEvent(ProcessInstance instance, ProcessNode node,
                                            ProcessTask task, String operator) {
        ProcessContext context = new ProcessContext(instance, node);
        context.setCurrentTask(task);
        context.setOperator(operator);
        context.setAction("BEFORE_APPROVE");
        eventHandlerManager.triggerBeforeApprove(context);
    }

    private List<TaskResponse> convertToTaskResponse(List<ProcessTask> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> instanceIds = tasks.stream()
                .map(ProcessTask::getInstanceId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, ProcessInstance> instanceMap = processInstanceMapper.selectBatchIds(instanceIds)
                .stream()
                .collect(Collectors.toMap(ProcessInstance::getId, i -> i));

        return tasks.stream()
                .map(t -> {
                    TaskResponse response = new TaskResponse();
                    response.setId(t.getId());
                    response.setInstanceId(t.getInstanceId());
                    response.setProcessKey(t.getProcessKey());
                    response.setNodeKey(t.getNodeKey());
                    response.setNodeName(t.getNodeName());
                    response.setTaskType(t.getTaskType());
                    response.setAssignee(t.getAssignee());
                    response.setStatus(t.getStatus());
                    response.setApproveType(t.getApproveType());
                    response.setCreateTime(t.getCreateTime());

                    ProcessInstance instance = instanceMap.get(t.getInstanceId());
                    if (instance != null) {
                        response.setTitle(instance.getTitle());
                        response.setBusinessKey(instance.getBusinessKey());
                        response.setInitiator(instance.getInitiator());
                        response.setProcessName(instance.getProcessName());
                    }
                    return response;
                })
                .collect(Collectors.toList());
    }
}
