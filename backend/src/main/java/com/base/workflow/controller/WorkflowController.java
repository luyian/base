package com.base.workflow.controller;

import com.base.common.result.Result;
import com.base.workflow.dto.*;
import com.base.workflow.entity.ProcessDefinition;
import com.base.workflow.entity.ProcessHistory;
import com.base.workflow.entity.ProcessInstance;
import com.base.workflow.entity.ProcessTask;
import com.base.workflow.service.ProcessDefinitionService;
import com.base.workflow.service.ProcessEngineService;
import com.base.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 流程管理控制器
 */
@Api(tags = "流程管理")
@RestController
@RequestMapping("/workflow")
public class WorkflowController {

    @Autowired
    private ProcessDefinitionService processDefinitionService;

    @Autowired
    private ProcessEngineService processEngineService;

    @ApiOperation("创建流程定义")
    @PostMapping("/definition")
    public Result<ProcessDefinition> save(@Valid @RequestBody ProcessDefinitionSaveRequest request) {
        String operator = SecurityUtils.getCurrentUsername();
        ProcessDefinition definition = processDefinitionService.save(request, operator);
        return Result.success(definition);
    }

    @ApiOperation("更新流程定义")
    @PutMapping("/definition/{id}")
    public Result<ProcessDefinition> update(@PathVariable Long id,
                                            @Valid @RequestBody ProcessDefinitionSaveRequest request) {
        String operator = SecurityUtils.getCurrentUsername();
        ProcessDefinition definition = processDefinitionService.update(id, request, operator);
        return Result.success(definition);
    }

    @ApiOperation("删除流程定义")
    @DeleteMapping("/definition/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        processDefinitionService.delete(id);
        return Result.success();
    }

    @ApiOperation("发布流程定义")
    @PostMapping("/definition/{id}/publish")
    public Result<Void> publish(@PathVariable Long id) {
        processDefinitionService.publish(id);
        return Result.success();
    }

    @ApiOperation("禁用流程定义")
    @PostMapping("/definition/{id}/disable")
    public Result<Void> disable(@PathVariable Long id) {
        processDefinitionService.disable(id);
        return Result.success();
    }

    @ApiOperation("流程定义列表")
    @GetMapping("/definition/list")
    public Result<List<ProcessDefinition>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        List<ProcessDefinition> list = processDefinitionService.list(category, keyword, status);
        return Result.success(list);
    }

    @ApiOperation("获取流程定义详情")
    @GetMapping("/definition/{id}")
    public Result<ProcessDefinitionResponse> getById(@PathVariable Long id) {
        ProcessDefinitionResponse detail = processEngineService.getProcessDefinitionDetail(id);
        return Result.success(detail);
    }

    @ApiOperation("发起流程")
    @PostMapping("/instance/start")
    public Result<ProcessInstance> startProcess(@Valid @RequestBody StartProcessRequest request) {
        String operator = SecurityUtils.getCurrentUsername();
        ProcessInstance instance = processEngineService.startProcess(request, operator);
        return Result.success(instance);
    }

    @ApiOperation("终止流程")
    @PostMapping("/instance/{id}/terminate")
    public Result<Void> terminateProcess(@PathVariable Long id,
                                         @RequestParam(required = false) String comment) {
        String operator = SecurityUtils.getCurrentUsername();
        processEngineService.terminateProcess(id, operator, comment);
        return Result.success();
    }

    @ApiOperation("获取我的待办任务")
    @GetMapping("/my/tasks")
    public Result<List<TaskResponse>> getMyPendingTasks() {
        String operator = SecurityUtils.getCurrentUsername();
        List<TaskResponse> tasks = processEngineService.getMyPendingTasks(operator);
        return Result.success(tasks);
    }

    @ApiOperation("获取我发起的流程")
    @GetMapping("/my/initiated")
    public Result<List<ProcessInstance>> getMyInitiated() {
        String operator = SecurityUtils.getCurrentUsername();
        List<ProcessInstance> list = processEngineService.getMyInitiatedProcesses(operator);
        return Result.success(list);
    }

    @ApiOperation("审批任务")
    @PostMapping("/task/approve")
    public Result<Void> approveTask(@Valid @RequestBody ApproveTaskRequest request) {
        String operator = SecurityUtils.getCurrentUsername();
        processEngineService.approveTask(request, operator);
        return Result.success();
    }

    @ApiOperation("回退任务")
    @PostMapping("/task/rollback")
    public Result<Void> rollbackTask(@Valid @RequestBody RollbackTaskRequest request) {
        String operator = SecurityUtils.getCurrentUsername();
        processEngineService.rollbackTask(request, operator);
        return Result.success();
    }

    @ApiOperation("转办任务")
    @PostMapping("/task/delegate")
    public Result<Void> delegateTask(@Valid @RequestBody DelegateTaskRequest request) {
        String operator = SecurityUtils.getCurrentUsername();
        processEngineService.delegateTask(request, operator);
        return Result.success();
    }

    @ApiOperation("获取流程历史")
    @GetMapping("/instance/{id}/history")
    public Result<List<ProcessHistory>> getProcessHistory(@PathVariable Long id) {
        List<ProcessHistory> history = processEngineService.getProcessHistory(id);
        return Result.success(history);
    }

    @ApiOperation("获取当前任务列表")
    @GetMapping("/instance/{id}/tasks")
    public Result<List<ProcessTask>> getCurrentTasks(@PathVariable Long id) {
        List<ProcessTask> tasks = processEngineService.getCurrentTasks(id);
        return Result.success(tasks);
    }

    @ApiOperation("获取流程实例详情")
    @GetMapping("/instance/{id}")
    public Result<ProcessInstance> getInstanceDetail(@PathVariable Long id) {
        ProcessInstance instance = processEngineService.getProcessInstanceDetail(id);
        return Result.success(instance);
    }
}
