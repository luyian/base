package com.base.workflow.service.impl;

import com.base.system.entity.SysUser;
import com.base.system.mapper.SysUserMapper;
import com.base.workflow.dto.*;
import com.base.workflow.service.ProcessEngineService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.engine.task.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于 Flowable 的流程引擎服务实现
 */
@Service
public class FlowableProcessEngineServiceImpl implements ProcessEngineService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private SysUserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProcessInstanceResponse startProcess(StartProcessRequest request, String operator) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("initiator", operator);
        variables.put("businessType", request.getBusinessType());
        variables.put("title", request.getTitle());
        if (request.getVariables() != null) {
            variables.putAll(request.getVariables());
        }

        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                request.getProcessKey(),
                request.getBusinessKey(),
                variables
        );

        ProcessInstanceResponse response = new ProcessInstanceResponse();
        response.setProcessInstanceId(instance.getId());
        response.setProcessDefinitionId(instance.getProcessDefinitionId());
        response.setProcessKey(instance.getProcessDefinitionKey());
        response.setProcessName(instance.getProcessDefinitionName());
        response.setBusinessKey(instance.getBusinessKey());
        response.setBusinessType(request.getBusinessType());
        response.setTitle(request.getTitle());
        response.setInitiator(operator);
        response.setStatus("RUNNING");
        response.setStartTime(LocalDateTime.now());
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveTask(ApproveTaskRequest request, String operator) {
        Task task = taskService.createTaskQuery()
                .taskId(request.getTaskId())
                .singleResult();
        if (task == null) {
            throw new RuntimeException("任务未找到");
        }

        String processInstanceId = task.getProcessInstanceId();

        if (request.getComment() != null && !request.getComment().isEmpty()) {
            taskService.addComment(task.getId(), processInstanceId,
                    request.getApproveResult(), request.getComment());
        }

        if ("APPROVE".equals(request.getApproveResult())) {
            Map<String, Object> variables = new HashMap<>();
            variables.put("approveResult", "APPROVE");
            taskService.complete(task.getId(), variables);
        } else {
            Map<String, Object> variables = new HashMap<>();
            variables.put("approveResult", "REJECT");
            taskService.complete(task.getId(), variables);

            // 检查流程是否有条件网关根据 approveResult 处理
            // 如果没有，手动终止流程
            try {
                ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                        .processInstanceId(processInstanceId)
                        .singleResult();
                if (instance != null) {
                    runtimeService.deleteProcessInstance(processInstanceId, "审批拒绝");
                }
            } catch (Exception ignored) {
                // 流程可能已经因为条件网关自动结束
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollbackTask(RollbackTaskRequest request, String operator) {
        Task task = taskService.createTaskQuery()
                .taskId(request.getTaskId())
                .singleResult();
        if (task == null) {
            throw new RuntimeException("任务未找到");
        }

        String processInstanceId = task.getProcessInstanceId();

        if (request.getComment() != null && !request.getComment().isEmpty()) {
            taskService.addComment(task.getId(), processInstanceId,
                    "ROLLBACK", request.getComment());
        }

        String currentActivityId = task.getTaskDefinitionKey();
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(processInstanceId)
                .moveActivityIdTo(currentActivityId, request.getTargetActivityId())
                .changeState();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delegateTask(DelegateTaskRequest request, String operator) {
        Task task = taskService.createTaskQuery()
                .taskId(request.getTaskId())
                .singleResult();
        if (task == null) {
            throw new RuntimeException("任务未找到");
        }

        if (request.getComment() != null && !request.getComment().isEmpty()) {
            taskService.addComment(task.getId(), task.getProcessInstanceId(),
                    "DELEGATE", "转办给 " + request.getDelegateTo() + ": " + request.getComment());
        }

        taskService.setAssignee(task.getId(), request.getDelegateTo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void terminateProcess(String processInstanceId, String operator, String comment) {
        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (instance == null) {
            throw new RuntimeException("流程实例未找到或已结束");
        }

        if (comment != null && !comment.isEmpty()) {
            taskService.addComment(null, processInstanceId, "TERMINATE", comment);
        }

        runtimeService.deleteProcessInstance(processInstanceId, "终止: " + (comment != null ? comment : ""));
    }

    @Override
    public List<TaskResponse> getMyPendingTasks(String assignee) {
        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(assignee)
                .orderByTaskCreateTime().desc()
                .list();

        return tasks.stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProcessInstanceResponse> getMyInitiatedProcesses(String initiator) {
        List<ProcessInstanceResponse> result = new ArrayList<>();

        // 运行中的流程
        List<ProcessInstance> running = runtimeService.createProcessInstanceQuery()
                .variableValueEquals("initiator", initiator)
                .orderByProcessInstanceId().desc()
                .list();
        for (ProcessInstance pi : running) {
            result.add(convertRunningInstance(pi));
        }

        // 已结束的流程
        List<HistoricProcessInstance> completed = historyService.createHistoricProcessInstanceQuery()
                .variableValueEquals("initiator", initiator)
                .finished()
                .orderByProcessInstanceEndTime().desc()
                .list();
        for (HistoricProcessInstance hpi : completed) {
            result.add(convertHistoricInstance(hpi));
        }

        return result;
    }

    @Override
    public List<ProcessHistoryResponse> getProcessHistory(String processInstanceId) {
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime().asc()
                .list();

        // 获取审批意见
        List<Comment> comments = taskService.getProcessInstanceComments(processInstanceId);
        Map<String, List<Comment>> commentsByTask = comments.stream()
                .filter(c -> c.getTaskId() != null)
                .collect(Collectors.groupingBy(Comment::getTaskId));

        return activities.stream()
                .map(activity -> {
                    ProcessHistoryResponse response = new ProcessHistoryResponse();
                    response.setActivityId(activity.getActivityId());
                    response.setActivityName(activity.getActivityName());
                    response.setActivityType(activity.getActivityType());
                    response.setAssignee(activity.getAssignee());
                    response.setStartTime(toLocalDateTime(activity.getStartTime()));
                    response.setEndTime(toLocalDateTime(activity.getEndTime()));

                    if (activity.getAssignee() != null) {
                        SysUser user = userMapper.selectOne(
                                new LambdaQueryWrapper<SysUser>()
                                        .eq(SysUser::getUsername, activity.getAssignee())
                        );
                        if (user != null) {
                            response.setAssigneeName(user.getNickname());
                        }
                    }

                    if (activity.getTaskId() != null) {
                        List<Comment> taskComments = commentsByTask.get(activity.getTaskId());
                        if (taskComments != null && !taskComments.isEmpty()) {
                            Comment latest = taskComments.get(taskComments.size() - 1);
                            response.setAction(latest.getType());
                            response.setComment(latest.getFullMessage());
                        } else if (activity.getEndTime() != null) {
                            response.setAction("APPROVE");
                        }
                    } else if ("startEvent".equals(activity.getActivityType())) {
                        response.setAction("START");
                    } else if ("endEvent".equals(activity.getActivityType())) {
                        response.setAction("COMPLETE");
                    }

                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getCurrentTasks(String processInstanceId) {
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();

        return tasks.stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProcessInstanceResponse getProcessInstanceDetail(String processInstanceId) {
        ProcessInstance running = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (running != null) {
            return convertRunningInstance(running);
        }

        HistoricProcessInstance historic = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (historic != null) {
            return convertHistoricInstance(historic);
        }

        throw new RuntimeException("流程实例未找到");
    }

    private TaskResponse convertToTaskResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setProcessInstanceId(task.getProcessInstanceId());
        response.setProcessDefinitionId(task.getProcessDefinitionId());
        response.setActivityId(task.getTaskDefinitionKey());
        response.setActivityName(task.getName());
        response.setAssignee(task.getAssignee());
        response.setCreateTime(toLocalDateTime(task.getCreateTime()));

        // 从流程变量中获取业务信息
        Map<String, Object> variables = runtimeService.getVariables(task.getExecutionId());
        response.setTitle((String) variables.get("title"));
        response.setBusinessKey((String) variables.get("businessKey"));
        response.setInitiator((String) variables.get("initiator"));

        // 获取流程定义信息
        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .singleResult();
        if (instance != null) {
            response.setProcessKey(instance.getProcessDefinitionKey());
            response.setProcessName(instance.getProcessDefinitionName());
            if (response.getBusinessKey() == null) {
                response.setBusinessKey(instance.getBusinessKey());
            }
        }

        return response;
    }

    private ProcessInstanceResponse convertRunningInstance(ProcessInstance pi) {
        ProcessInstanceResponse response = new ProcessInstanceResponse();
        response.setProcessInstanceId(pi.getId());
        response.setProcessDefinitionId(pi.getProcessDefinitionId());
        response.setProcessKey(pi.getProcessDefinitionKey());
        response.setProcessName(pi.getProcessDefinitionName());
        response.setBusinessKey(pi.getBusinessKey());
        response.setStatus("RUNNING");
        response.setStartTime(toLocalDateTime(pi.getStartTime()));

        Map<String, Object> variables = runtimeService.getVariables(pi.getId());
        response.setInitiator((String) variables.get("initiator"));
        response.setTitle((String) variables.get("title"));
        response.setBusinessType((String) variables.get("businessType"));

        // 获取当前活动名称
        List<Task> currentTasks = taskService.createTaskQuery()
                .processInstanceId(pi.getId())
                .list();
        if (!currentTasks.isEmpty()) {
            response.setCurrentActivityName(currentTasks.get(0).getName());
        }

        return response;
    }

    private ProcessInstanceResponse convertHistoricInstance(HistoricProcessInstance hpi) {
        ProcessInstanceResponse response = new ProcessInstanceResponse();
        response.setProcessInstanceId(hpi.getId());
        response.setProcessDefinitionId(hpi.getProcessDefinitionId());
        response.setProcessKey(hpi.getProcessDefinitionKey());
        response.setProcessName(hpi.getProcessDefinitionName());
        response.setBusinessKey(hpi.getBusinessKey());
        response.setStartTime(toLocalDateTime(hpi.getStartTime()));
        response.setEndTime(toLocalDateTime(hpi.getEndTime()));

        if (hpi.getDeleteReason() != null) {
            response.setStatus("TERMINATED");
        } else {
            response.setStatus("COMPLETED");
        }

        Map<String, Object> variables = hpi.getProcessVariables();
        if (variables == null || variables.isEmpty()) {
            List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(hpi.getId())
                    .includeProcessVariables()
                    .list();
            if (!list.isEmpty()) {
                variables = list.get(0).getProcessVariables();
            }
        }
        if (variables != null) {
            response.setInitiator((String) variables.get("initiator"));
            response.setTitle((String) variables.get("title"));
            response.setBusinessType((String) variables.get("businessType"));
        }

        return response;
    }

    private LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
