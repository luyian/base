package com.base.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import com.base.system.dto.permission.PermissionQueryRequest;
import com.base.system.dto.permission.PermissionResponse;
import com.base.system.dto.permission.PermissionSaveRequest;
import com.base.system.entity.Permission;
import com.base.system.entity.RolePermission;
import com.base.system.mapper.PermissionMapper;
import com.base.system.mapper.RolePermissionMapper;
import com.base.system.service.PermissionService;
import com.base.system.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限/菜单服务实现类
 */
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    private final RolePermissionMapper rolePermissionMapper;

    @Override
    public List<PermissionResponse> treePermissions(PermissionQueryRequest request) {
        // 构建查询条件
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(request.getPermissionName()), Permission::getPermissionName, request.getPermissionName())
                .like(StrUtil.isNotBlank(request.getPermissionCode()), Permission::getPermissionCode, request.getPermissionCode())
                .eq(request.getType() != null, Permission::getType, request.getType())
                .eq(request.getStatus() != null, Permission::getStatus, request.getStatus())
                .orderByAsc(Permission::getSort)
                .orderByDesc(Permission::getCreateTime);

        // 查询所有权限
        List<Permission> permissionList = list(wrapper);

        // 转换为树形结构
        return buildPermissionTree(permissionList, 0L);
    }

    @Override
    public PermissionResponse getPermissionById(Long id) {
        Permission permission = getById(id);
        if (permission == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }

        PermissionResponse response = BeanUtil.copyProperties(permission, PermissionResponse.class);
        response.setTypeName(permission.getType() == 1 ? "菜单" : "按钮");
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPermission(PermissionSaveRequest request) {
        // 检查权限编码是否已存在
        long count = count(new LambdaQueryWrapper<Permission>()
                .eq(Permission::getPermissionCode, request.getPermissionCode()));
        if (count > 0) {
            throw new BusinessException(ResultCode.DATA_EXIST.getCode(), "权限编码已存在");
        }

        // 保存权限
        Permission permission = BeanUtil.copyProperties(request, Permission.class);
        save(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePermission(PermissionSaveRequest request) {
        // 检查权限是否存在
        Permission permission = getById(request.getId());
        if (permission == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }

        // 检查权限编码是否已存在（排除自己）
        long count = count(new LambdaQueryWrapper<Permission>()
                .eq(Permission::getPermissionCode, request.getPermissionCode())
                .ne(Permission::getId, request.getId()));
        if (count > 0) {
            throw new BusinessException(ResultCode.DATA_EXIST.getCode(), "权限编码已存在");
        }

        // 更新权限
        BeanUtil.copyProperties(request, permission, "id");
        updateById(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(Long id) {
        // 检查权限是否存在
        Permission permission = getById(id);
        if (permission == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }

        // 检查是否有子权限
        long count = count(new LambdaQueryWrapper<Permission>()
                .eq(Permission::getParentId, id));
        if (count > 0) {
            throw new BusinessException(ResultCode.OPERATION_NOT_ALLOWED.getCode(), "该权限下存在子权限，无法删除");
        }

        // 检查是否有角色使用该权限
        long roleCount = rolePermissionMapper.selectCount(new LambdaQueryWrapper<RolePermission>()
                .eq(RolePermission::getPermissionId, id));
        if (roleCount > 0) {
            throw new BusinessException(ResultCode.DATA_IN_USE.getCode(), "该权限已被角色使用，无法删除");
        }

        // 删除权限
        removeById(id);
    }

    @Override
    public List<PermissionResponse> getCurrentUserMenuTree() {
        // 获取当前用户名
        String username = SecurityUtils.getCurrentUsername();

        // TODO: 根据用户角色查询权限，这里暂时返回所有菜单
        // 实际应该根据用户的角色查询对应的权限
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Permission::getType, 1, 2) // 查询目录和菜单（1-目录，2-菜单）
                .eq(Permission::getStatus, 1) // 只查询正常状态
                .eq(Permission::getVisible, 1) // 只查询可见的
                .orderByAsc(Permission::getSort)
                .orderByDesc(Permission::getCreateTime);

        List<Permission> permissionList = list(wrapper);

        // 转换为树形结构
        return buildPermissionTree(permissionList, 0L);
    }

    @Override
    public List<PermissionResponse> getAllPermissionTree() {
        // 查询所有正常状态的权限
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getStatus, 1)
                .orderByAsc(Permission::getSort)
                .orderByDesc(Permission::getCreateTime);

        List<Permission> permissionList = list(wrapper);

        // 转换为树形结构
        return buildPermissionTree(permissionList, 0L);
    }

    /**
     * 构建权限树
     *
     * @param permissionList 权限列表
     * @param parentId       父级ID
     * @return 权限树列表
     */
    private List<PermissionResponse> buildPermissionTree(List<Permission> permissionList, Long parentId) {
        if (CollUtil.isEmpty(permissionList)) {
            return new ArrayList<>();
        }

        // 按父级ID分组
        Map<Long, List<Permission>> permissionMap = permissionList.stream()
                .collect(Collectors.groupingBy(Permission::getParentId));

        // 递归构建树形结构
        return buildTree(permissionMap, parentId);
    }

    /**
     * 递归构建树形结构
     *
     * @param permissionMap 权限分组Map
     * @param parentId      父级ID
     * @return 权限树列表
     */
    private List<PermissionResponse> buildTree(Map<Long, List<Permission>> permissionMap, Long parentId) {
        List<Permission> children = permissionMap.get(parentId);
        if (CollUtil.isEmpty(children)) {
            return new ArrayList<>();
        }

        return children.stream().map(permission -> {
            PermissionResponse response = BeanUtil.copyProperties(permission, PermissionResponse.class);
            response.setTypeName(permission.getType() == 1 ? "菜单" : "按钮");

            // 递归查询子权限
            List<PermissionResponse> childrenList = buildTree(permissionMap, permission.getId());
            if (CollUtil.isNotEmpty(childrenList)) {
                response.setChildren(childrenList);
            }

            return response;
        }).collect(Collectors.toList());
    }
}
