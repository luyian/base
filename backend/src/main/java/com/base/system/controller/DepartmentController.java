package com.base.system.controller;

import com.base.system.common.Result;
import com.base.system.dto.department.DepartmentQueryRequest;
import com.base.system.dto.department.DepartmentResponse;
import com.base.system.dto.department.DepartmentSaveRequest;
import com.base.system.service.DepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门控制器
 */
@Api(tags = "部门管理")
@RestController
@RequestMapping("/system/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * 查询部门树
     */
    @ApiOperation("查询部门树")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:dept:list')")
    public Result<List<DepartmentResponse>> treeDepartments(DepartmentQueryRequest request) {
        List<DepartmentResponse> list = departmentService.treeDepartments(request);
        return Result.success(list);
    }

    /**
     * 根据ID获取部门详情
     */
    @ApiOperation("根据ID获取部门详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dept:query')")
    public Result<DepartmentResponse> getDepartmentById(@PathVariable Long id) {
        DepartmentResponse department = departmentService.getDepartmentById(id);
        return Result.success(department);
    }

    /**
     * 新增部门
     */
    @ApiOperation("新增部门")
    @PostMapping
    @PreAuthorize("hasAuthority('system:dept:add')")
    public Result<Void> addDepartment(@Validated @RequestBody DepartmentSaveRequest request) {
        departmentService.addDepartment(request);
        return Result.success();
    }

    /**
     * 编辑部门
     */
    @ApiOperation("编辑部门")
    @PutMapping
    @PreAuthorize("hasAuthority('system:dept:edit')")
    public Result<Void> updateDepartment(@Validated @RequestBody DepartmentSaveRequest request) {
        departmentService.updateDepartment(request);
        return Result.success();
    }

    /**
     * 删除部门
     */
    @ApiOperation("删除部门")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dept:delete')")
    public Result<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return Result.success();
    }

    /**
     * 获取所有部门树（用于选择上级部门）
     */
    @ApiOperation("获取所有部门树")
    @GetMapping("/all/tree")
    @PreAuthorize("hasAuthority('system:dept:list')")
    public Result<List<DepartmentResponse>> getAllDepartmentTree() {
        List<DepartmentResponse> list = departmentService.getAllDepartmentTree();
        return Result.success(list);
    }
}
