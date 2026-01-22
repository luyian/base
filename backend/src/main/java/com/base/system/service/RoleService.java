package com.base.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.system.dto.role.*;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService {

    /**
     * 分页查询角色列表
     *
     * @param request 查询请求参数
     * @return 分页结果
     */
    Page<RoleResponse> pageRoles(RoleQueryRequest request);

    /**
     * 根据ID获取角色详情
     *
     * @param id 角色ID
     * @return 角色详情
     */
    RoleResponse getRoleById(Long id);

    /**
     * 新增角色
     *
     * @param request 角色信息
     */
    void addRole(RoleSaveRequest request);

    /**
     * 编辑角色
     *
     * @param request 角色信息
     */
    void updateRole(RoleSaveRequest request);

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    void deleteRole(Long id);

    /**
     * 批量删除角色
     *
     * @param ids 角色ID列表
     */
    void batchDeleteRoles(List<Long> ids);

    /**
     * 切换角色状态
     *
     * @param id     角色ID
     * @param status 状态
     */
    void toggleStatus(Long id, Integer status);

    /**
     * 分配权限
     *
     * @param request 分配权限请求参数
     */
    void assignPermissions(RoleAssignPermissionRequest request);

    /**
     * 获取角色的权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getRolePermissionIds(Long roleId);

    /**
     * 获取所有角色列表（不分页）
     *
     * @return 角色列表
     */
    List<RoleResponse> listAllRoles();
}
