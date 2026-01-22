package com.base.system.controller;

import com.base.system.common.Result;
import com.base.system.dto.permission.PermissionQueryRequest;
import com.base.system.dto.permission.PermissionResponse;
import com.base.system.dto.permission.PermissionSaveRequest;
import com.base.system.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限/菜单管理控制器
 */
@RestController
@RequestMapping("/system/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * 查询权限树
     */
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:permission:list')")
    public Result<List<PermissionResponse>> treePermissions(PermissionQueryRequest request) {
        List<PermissionResponse> list = permissionService.treePermissions(request);
        return Result.success(list);
    }

    /**
     * 根据ID获取权限详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:permission:query')")
    public Result<PermissionResponse> getPermissionById(@PathVariable Long id) {
        PermissionResponse response = permissionService.getPermissionById(id);
        return Result.success(response);
    }

    /**
     * 新增权限
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:permission:add')")
    public Result<Void> addPermission(@Validated @RequestBody PermissionSaveRequest request) {
        permissionService.addPermission(request);
        return Result.success();
    }

    /**
     * 编辑权限
     */
    @PutMapping
    @PreAuthorize("hasAuthority('system:permission:edit')")
    public Result<Void> updatePermission(@Validated @RequestBody PermissionSaveRequest request) {
        permissionService.updatePermission(request);
        return Result.success();
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:permission:delete')")
    public Result<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return Result.success();
    }

    /**
     * 获取当前用户的菜单树
     */
    @GetMapping("/menu/tree")
    public Result<List<PermissionResponse>> getCurrentUserMenuTree() {
        List<PermissionResponse> list = permissionService.getCurrentUserMenuTree();
        return Result.success(list);
    }

    /**
     * 获取所有权限树（用于角色分配权限）
     */
    @GetMapping("/all/tree")
    @PreAuthorize("hasAuthority('system:permission:list')")
    public Result<List<PermissionResponse>> getAllPermissionTree() {
        List<PermissionResponse> list = permissionService.getAllPermissionTree();
        return Result.success(list);
    }
}
