package com.base.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.system.common.Result;
import com.base.system.dto.role.*;
import com.base.system.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@Api(tags = "角色管理")
@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * 分页查询角色列表
     */
    @ApiOperation("分页查询角色列表")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result<Page<RoleResponse>> pageRoles(@RequestBody RoleQueryRequest request) {
        Page<RoleResponse> page = roleService.pageRoles(request);
        return Result.success(page);
    }

    /**
     * 根据ID获取角色详情
     */
    @ApiOperation("根据ID获取角色详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result<RoleResponse> getRoleById(@PathVariable Long id) {
        RoleResponse response = roleService.getRoleById(id);
        return Result.success(response);
    }

    /**
     * 新增角色
     */
    @ApiOperation("新增角色")
    @PostMapping
    @PreAuthorize("hasAuthority('system:role:add')")
    public Result<Void> addRole(@Validated @RequestBody RoleSaveRequest request) {
        roleService.addRole(request);
        return Result.success();
    }

    /**
     * 编辑角色
     */
    @ApiOperation("编辑角色")
    @PutMapping
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<Void> updateRole(@Validated @RequestBody RoleSaveRequest request) {
        roleService.updateRole(request);
        return Result.success();
    }

    /**
     * 删除角色
     */
    @ApiOperation("删除角色")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }

    /**
     * 批量删除角色
     */
    @ApiOperation("批量删除角色")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result<Void> batchDeleteRoles(@RequestBody List<Long> ids) {
        roleService.batchDeleteRoles(ids);
        return Result.success();
    }

    /**
     * 切换角色状态
     */
    @ApiOperation("切换角色状态")
    @PutMapping("/{id}/status/{status}")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<Void> toggleStatus(@PathVariable Long id, @PathVariable Integer status) {
        roleService.toggleStatus(id, status);
        return Result.success();
    }

    /**
     * 分配权限
     */
    @ApiOperation("分配权限")
    @PutMapping("/assign-permissions")
    @PreAuthorize("hasAuthority('system:role:permission')")
    public Result<Void> assignPermissions(@Validated @RequestBody RoleAssignPermissionRequest request) {
        roleService.assignPermissions(request);
        return Result.success();
    }

    /**
     * 获取角色的权限ID列表
     */
    @ApiOperation("获取角色的权限ID列表")
    @GetMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result<List<Long>> getRolePermissionIds(@PathVariable Long roleId) {
        List<Long> permissionIds = roleService.getRolePermissionIds(roleId);
        return Result.success(permissionIds);
    }

    /**
     * 获取所有角色列表（不分页）
     */
    @ApiOperation("获取所有角色列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result<List<RoleResponse>> listAllRoles() {
        List<RoleResponse> roles = roleService.listAllRoles();
        return Result.success(roles);
    }
}
