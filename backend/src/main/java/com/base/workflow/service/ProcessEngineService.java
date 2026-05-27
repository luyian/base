package com.base.workflow.service;

import com.base.workflow.dto.*;

import java.util.List;

/**
 * 流程引擎服务接口
 */
public interface ProcessEngineService {

    /**
     * 发起流程
     */
    ProcessInstanceResponse startProcess(StartProcessRequest request, String operator);

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
    void terminateProcess(String processInstanceId, String operator, String comment);

    /**
     * 获取我的待办任务
     */
    List<TaskResponse> getMyPendingTasks(String assignee);

    /**
     * 获取我发起的流程
     */
    List<ProcessInstanceResponse> getMyInitiatedProcesses(String initiator);

    /**
     * 获取流程历史
     */
    List<ProcessHistoryResponse> getProcessHistory(String processInstanceId);

    /**
     * 获取当前任务列表
     */
    List<TaskResponse> getCurrentTasks(String processInstanceId);

    /**
     * 获取流程实例详情
     */
    ProcessInstanceResponse getProcessInstanceDetail(String processInstanceId);
}
