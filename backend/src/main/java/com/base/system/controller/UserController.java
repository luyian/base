package com.base.system.controller;

import com.base.system.annotation.OperationLog;
import com.base.system.common.Result;
import com.base.system.dto.*;
import com.base.system.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/system/user")
@Api(tags = "用户管理")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询用户列表")
    @PreAuthorize("hasAuthority('system:user:list')")
    public Result<Page<UserResponse>> pageUsers(UserQueryRequest request) {
        Page<UserResponse> page = userService.pageUsers(request);
        return Result.success(page);
    }

    /**
     * 根据ID获取用户详情
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID获取用户详情")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Result<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return Result.success(user);
    }

    /**
     * 新增用户
     */
    @PostMapping
    @ApiOperation("新增用户")
    @PreAuthorize("hasAuthority('system:user:add')")
    @OperationLog(module = "用户管理", operation = "新增用户")
    public Result<Long> addUser(@Validated @RequestBody UserSaveRequest request) {
        Long userId = userService.addUser(request);
        return Result.success(userId);
    }

    /**
     * 编辑用户
     */
    @PutMapping
    @ApiOperation("编辑用户")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @OperationLog(module = "用户管理", operation = "编辑用户")
    public Result<Void> updateUser(@Validated @RequestBody UserSaveRequest request) {
        userService.updateUser(request);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除用户")
    @PreAuthorize("hasAuthority('system:user:delete')")
    @OperationLog(module = "用户管理", operation = "删除用户")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    @ApiOperation("批量删除用户")
    @PreAuthorize("hasAuthority('system:user:delete')")
    @OperationLog(module = "用户管理", operation = "批量删除用户")
    public Result<Void> batchDeleteUsers(@RequestBody List<Long> ids) {
        userService.batchDeleteUsers(ids);
        return Result.success();
    }

    /**
     * 切换用户状态
     */
    @PutMapping("/{id}/status/{status}")
    @ApiOperation("切换用户状态")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<Void> toggleStatus(@PathVariable Long id, @PathVariable Integer status) {
        userService.toggleStatus(id, status);
        return Result.success();
    }

    /**
     * 重置密码
     */
    @PutMapping("/reset-password")
    @ApiOperation("重置密码")
    @PreAuthorize("hasAuthority('system:user:resetPwd')")
    @OperationLog(module = "用户管理", operation = "重置密码", saveResult = false)
    public Result<Void> resetPassword(@Validated @RequestBody UserResetPasswordRequest request) {
        userService.resetPassword(request);
        return Result.success();
    }

    /**
     * 分配角色
     */
    @PutMapping("/assign-roles")
    @ApiOperation("分配角色")
    @PreAuthorize("hasAuthority('system:user:role')")
    @OperationLog(module = "用户管理", operation = "分配角色")
    public Result<Void> assignRoles(@Validated @RequestBody UserAssignRoleRequest request) {
        userService.assignRoles(request);
        return Result.success();
    }

    /**
     * 获取用户的角色ID列表
     */
    @GetMapping("/{userId}/roles")
    @ApiOperation("获取用户的角色ID列表")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Result<List<Long>> getUserRoleIds(@PathVariable Long userId) {
        List<Long> roleIds = userService.getUserRoleIds(userId);
        return Result.success(roleIds);
    }
}
