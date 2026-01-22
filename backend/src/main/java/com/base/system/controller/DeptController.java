package com.base.system.controller;

import com.base.common.result.Result;
import com.base.system.dto.DeptTreeNode;
import com.base.system.entity.Dept;
import com.base.system.service.DeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 *
 * @author base
 * @since 2026-01-13
 */
@Api(tags = "部门管理")
@RestController
@RequestMapping("/system/dept")
@RequiredArgsConstructor
public class DeptController {

    private final DeptService deptService;

    /**
     * 获取部门树
     */
    @ApiOperation(value = "获取部门树")
    @GetMapping("/tree")
    public Result<List<DeptTreeNode>> getDeptTree() {
        List<DeptTreeNode> tree = deptService.getDeptTree();
        return Result.success(tree);
    }

    /**
     * 根据父部门ID获取子部门列表
     */
    @ApiOperation(value = "根据父部门ID获取子部门列表")
    @GetMapping("/children/{parentId}")
    public Result<List<Dept>> getChildrenByParentId(@PathVariable Long parentId) {
        List<Dept> children = deptService.getChildrenByParentId(parentId);
        return Result.success(children);
    }

    /**
     * 根据ID获取部门详情
     */
    @ApiOperation(value = "根据ID获取部门详情")
    @GetMapping("/{id}")
    public Result<Dept> getDeptById(@PathVariable Long id) {
        Dept dept = deptService.getById(id);
        return Result.success(dept);
    }

    /**
     * 创建部门
     */
    @ApiOperation(value = "创建部门")
    @PostMapping
    @PreAuthorize("hasAuthority('system:dept:add')")
    public Result<Void> createDept(@Validated @RequestBody Dept dept) {
        deptService.createDept(dept);
        return Result.success();
    }

    /**
     * 更新部门
     */
    @ApiOperation(value = "更新部门")
    @PutMapping
    @PreAuthorize("hasAuthority('system:dept:edit')")
    public Result<Void> updateDept(@Validated @RequestBody Dept dept) {
        deptService.updateDept(dept);
        return Result.success();
    }

    /**
     * 删除部门
     */
    @ApiOperation(value = "删除部门")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dept:delete')")
    public Result<Void> deleteDept(@PathVariable Long id) {
        deptService.deleteDept(id);
        return Result.success();
    }
}
