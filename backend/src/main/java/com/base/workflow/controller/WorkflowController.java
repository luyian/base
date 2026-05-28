package com.base.workflow.controller;

import com.base.common.result.Result;
import com.base.system.dto.department.DepartmentQueryRequest;
import com.base.system.dto.department.DepartmentResponse;
import com.base.system.dto.role.RoleResponse;
import com.base.system.service.DepartmentService;
import com.base.system.service.RoleService;
import com.base.workflow.dto.*;
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

    @Autowired
    private RoleService roleService;

    @Autowired
    private DepartmentService departmentService;

    // ==================== 流程定义管理 ====================

    @ApiOperation("创建流程定义")
    @PostMapping("/definition")
    public Result<ProcessDefinitionResponse> save(@Valid @RequestBody ProcessDefinitionSaveRequest request) {
        String operator = SecurityUtils.getCurrentUsername();
        ProcessDefinitionResponse response = processDefinitionService.save(request, operator);
        return Result.success(response);
    }

    @ApiOperation("更新流程定义")
    @PutMapping("/definition/{id}")
    public Result<ProcessDefinitionResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody ProcessDefinitionSaveRequest request) {
        String operator = SecurityUtils.getCurrentUsername();
        ProcessDefinitionResponse response = processDefinitionService.update(id, request, operator);
        return Result.success(response);
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
    public Result<List<ProcessDefinitionResponse>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        List<ProcessDefinitionResponse> list = processDefinitionService.list(category, keyword, status);
        return Result.success(list);
    }

    @ApiOperation("获取流程定义详情")
    @GetMapping("/definition/{id}")
    public Result<ProcessDefinitionResponse> getById(@PathVariable Long id) {
        ProcessDefinitionResponse detail = processDefinitionService.getById(id);
        return Result.success(detail);
    }

    @ApiOperation("获取 BPMN XML")
    @GetMapping("/definition/{id}/bpmn")
    public Result<String> getBpmnXml(@PathVariable Long id) {
        return Result.success(processDefinitionService.getBpmnXml(id));
    }

    // ==================== 流程实例管理 ====================

    @ApiOperation("发起流程")
    @PostMapping("/instance/start")
    public Result<ProcessInstanceResponse> startProcess(@Valid @RequestBody StartProcessRequest request) {
        String operator = SecurityUtils.getCurrentUsername();
        ProcessInstanceResponse instance = processEngineService.startProcess(request, operator);
        return Result.success(instance);
    }

    @ApiOperation("终止流程")
    @PostMapping("/instance/{processInstanceId}/terminate")
    public Result<Void> terminateProcess(@PathVariable String processInstanceId,
                                         @RequestParam(required = false) String comment) {
        String operator = SecurityUtils.getCurrentUsername();
        processEngineService.terminateProcess(processInstanceId, operator, comment);
        return Result.success();
    }

    @ApiOperation("获取流程实例详情")
    @GetMapping("/instance/{processInstanceId}")
    public Result<ProcessInstanceResponse> getInstanceDetail(@PathVariable String processInstanceId) {
        ProcessInstanceResponse instance = processEngineService.getProcessInstanceDetail(processInstanceId);
        return Result.success(instance);
    }

    @ApiOperation("获取流程历史")
    @GetMapping("/instance/{processInstanceId}/history")
    public Result<List<ProcessHistoryResponse>> getProcessHistory(@PathVariable String processInstanceId) {
        List<ProcessHistoryResponse> history = processEngineService.getProcessHistory(processInstanceId);
        return Result.success(history);
    }

    @ApiOperation("获取当前任务列表")
    @GetMapping("/instance/{processInstanceId}/tasks")
    public Result<List<TaskResponse>> getCurrentTasks(@PathVariable String processInstanceId) {
        List<TaskResponse> tasks = processEngineService.getCurrentTasks(processInstanceId);
        return Result.success(tasks);
    }

    // ==================== 任务处理 ====================

    @ApiOperation("获取我的待办任务")
    @GetMapping("/my/tasks")
    public Result<List<TaskResponse>> getMyPendingTasks() {
        String operator = SecurityUtils.getCurrentUsername();
        List<TaskResponse> tasks = processEngineService.getMyPendingTasks(operator);
        return Result.success(tasks);
    }

    @ApiOperation("获取我发起的流程")
    @GetMapping("/my/initiated")
    public Result<List<ProcessInstanceResponse>> getMyInitiated() {
        String operator = SecurityUtils.getCurrentUsername();
        List<ProcessInstanceResponse> list = processEngineService.getMyInitiatedProcesses(operator);
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

    // ==================== 候选人配置辅助接口 ====================

    @ApiOperation("获取角色列表（候选人配置用）")
    @GetMapping("/candidates/roles")
    public Result<List<RoleResponse>> getCandidateRoles() {
        return Result.success(roleService.listAllRoles());
    }

    @ApiOperation("获取部门树（候选人配置用）")
    @GetMapping("/candidates/departments")
    public Result<List<DepartmentResponse>> getCandidateDepartments() {
        return Result.success(departmentService.getAllDepartmentTree());
    }
}
