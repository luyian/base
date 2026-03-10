package com.base.workflow.service;

import com.base.workflow.dto.*;
import com.base.workflow.entity.ProcessDefinition;
import com.base.workflow.entity.ProcessHistory;
import com.base.workflow.entity.ProcessInstance;
import com.base.workflow.entity.ProcessTask;

import java.util.List;

/**
 * 流程引擎服务接口
 */
public interface ProcessEngineService {

    /**
     * 发起流程
     */
    ProcessInstance startProcess(StartProcessRequest request, String operator);

    /**
     * 审批任务
     */
    void approveTask(ApproveTaskRequest request, String operator);

    /**
     * 回退任务
     */
    void rollbackTask(RollbackTaskRequest request, String operator);

    /**
     * 转办任务
     */
    void delegateTask(DelegateTaskRequest request, String operator);

    /**
     * 终止流程
     */
    void terminateProcess(Long instanceId, String operator, String comment);

    /**
     * 获取我的待办任务
     */
    List<TaskResponse> getMyPendingTasks(String assignee);

    /**
     * 获取我发起的流程
     */
    List<ProcessInstance> getMyInitiatedProcesses(String initiator);

    /**
     * 获取流程历史
     */
    List<ProcessHistory> getProcessHistory(Long instanceId);

    /**
     * 获取当前任务列表
     */
    List<ProcessTask> getCurrentTasks(Long instanceId);

    /**
     * 获取流程定义详情
     */
    ProcessDefinitionResponse getProcessDefinitionDetail(Long id);

    /**
     * 获取流程实例详情
     */
    ProcessInstance getProcessInstanceDetail(Long instanceId);
}
