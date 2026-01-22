package com.base.system.service;

import com.base.system.dto.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 分页查询用户列表
     *
     * @param request 查询参数
     * @return 用户分页数据
     */
    Page<UserResponse> pageUsers(UserQueryRequest request);

    /**
     * 根据ID获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    UserResponse getUserById(Long id);

    /**
     * 新增用户
     *
     * @param request 用户信息
     * @return 用户ID
     */
    Long addUser(UserSaveRequest request);

    /**
     * 编辑用户
     *
     * @param request 用户信息
     */
    void updateUser(UserSaveRequest request);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 批量删除用户
     *
     * @param ids 用户ID列表
     */
    void batchDeleteUsers(List<Long> ids);

    /**
     * 切换用户状态
     *
     * @param id     用户ID
     * @param status 状态（0-禁用，1-启用）
     */
    void toggleStatus(Long id, Integer status);

    /**
     * 重置密码
     *
     * @param request 重置密码参数
     */
    void resetPassword(UserResetPasswordRequest request);

    /**
     * 分配角色
     *
     * @param request 分配角色参数
     */
    void assignRoles(UserAssignRoleRequest request);

    /**
     * 获取用户的角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> getUserRoleIds(Long userId);
}
