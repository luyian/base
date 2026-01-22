package com.base.system.service;

import com.base.system.dto.permission.PermissionQueryRequest;
import com.base.system.dto.permission.PermissionResponse;
import com.base.system.dto.permission.PermissionSaveRequest;

import java.util.List;

/**
 * 权限/菜单服务接口
 */
public interface PermissionService {

    /**
     * 查询权限树
     *
     * @param request 查询参数
     * @return 权限树列表
     */
    List<PermissionResponse> treePermissions(PermissionQueryRequest request);

    /**
     * 根据ID获取权限详情
     *
     * @param id 权限ID
     * @return 权限详情
     */
    PermissionResponse getPermissionById(Long id);

    /**
     * 新增权限
     *
     * @param request 权限信息
     */
    void addPermission(PermissionSaveRequest request);

    /**
     * 编辑权限
     *
     * @param request 权限信息
     */
    void updatePermission(PermissionSaveRequest request);

    /**
     * 删除权限
     *
     * @param id 权限ID
     */
    void deletePermission(Long id);

    /**
     * 获取当前用户的菜单树
     *
     * @return 菜单树列表
     */
    List<PermissionResponse> getCurrentUserMenuTree();

    /**
     * 获取所有权限树（用于角色分配权限）
     *
     * @return 权限树列表
     */
    List<PermissionResponse> getAllPermissionTree();
}
