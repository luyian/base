package com.base.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.result.ResultCode;
import com.base.system.dto.role.*;
import com.base.system.entity.Role;
import com.base.system.entity.RolePermission;
import com.base.system.entity.UserRole;
import com.base.system.exception.BusinessException;
import com.base.system.mapper.RoleMapper;
import com.base.system.mapper.RolePermissionMapper;
import com.base.system.mapper.UserRoleMapper;
import com.base.system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final RolePermissionMapper rolePermissionMapper;

    @Override
    public Page<RoleResponse> pageRoles(RoleQueryRequest request) {
        // 构建分页对象
        Page<Role> page = request.buildPage();

        // 构建查询条件
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(request.getRoleName()), Role::getRoleName, request.getRoleName())
                .like(StringUtils.hasText(request.getRoleCode()), Role::getRoleCode, request.getRoleCode())
                .eq(request.getStatus() != null, Role::getStatus, request.getStatus())
                .orderByAsc(Role::getSort)
                .orderByDesc(Role::getCreateTime);

        // 执行查询
        Page<Role> rolePage = roleMapper.selectPage(page, wrapper);

        // 转换为响应对象
        Page<RoleResponse> responsePage = new Page<>();
        BeanUtils.copyProperties(rolePage, responsePage, "records");
        List<RoleResponse> responseList = rolePage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        responsePage.setRecords(responseList);

        return responsePage;
    }

    @Override
    public RoleResponse getRoleById(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_FOUND);
        }

        RoleResponse response = convertToResponse(role);

        // 查询角色的权限ID列表
        List<Long> permissionIds = getRolePermissionIds(id);
        response.setPermissionIds(permissionIds);

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRole(RoleSaveRequest request) {
        // 检查角色编码是否已存在
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleCode, request.getRoleCode());
        if (roleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.ROLE_CODE_ALREADY_EXISTS);
        }

        // 创建角色
        Role role = new Role();
        BeanUtils.copyProperties(request, role);
        roleMapper.insert(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(RoleSaveRequest request) {
        // 检查角色是否存在
        Role role = roleMapper.selectById(request.getId());
        if (role == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_FOUND);
        }

        // 检查角色编码是否已被其他角色使用
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleCode, request.getRoleCode())
                .ne(Role::getId, request.getId());
        if (roleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.ROLE_CODE_ALREADY_EXISTS);
        }

        // 更新角色
        BeanUtils.copyProperties(request, role);
        roleMapper.updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        // 检查角色是否存在
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_FOUND);
        }

        // 检查是否有用户使用该角色
        LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(UserRole::getRoleId, id);
        if (userRoleMapper.selectCount(userRoleWrapper) > 0) {
            throw new BusinessException(ResultCode.OPERATION_NOT_ALLOWED, "该角色已分配给用户，无法删除");
        }

        // 删除角色
        roleMapper.deleteById(id);

        // 删除角色权限关联
        LambdaQueryWrapper<RolePermission> rolePermissionWrapper = new LambdaQueryWrapper<>();
        rolePermissionWrapper.eq(RolePermission::getRoleId, id);
        rolePermissionMapper.delete(rolePermissionWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteRoles(List<Long> ids) {
        for (Long id : ids) {
            deleteRole(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleStatus(Long id, Integer status) {
        // 检查角色是否存在
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_FOUND);
        }

        // 更新状态
        role.setStatus(status);
        roleMapper.updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(RoleAssignPermissionRequest request) {
        // 检查角色是否存在
        Role role = roleMapper.selectById(request.getRoleId());
        if (role == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_FOUND);
        }
        // 删除原有的角色权限关联
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, request.getRoleId());
        rolePermissionMapper.delete(wrapper);

        // 批量添加新的角色权限关联
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            List<RolePermission> rolePermissions = new ArrayList<>();
            for (Long permissionId : request.getPermissionIds()) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRoleId(request.getRoleId());
                rolePermission.setPermissionId(permissionId);
                rolePermissions.add(rolePermission);
            }
            rolePermissionMapper.batchInsert(rolePermissions);
        }
    }

    @Override
    public List<Long> getRolePermissionIds(Long roleId) {
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(wrapper);
        return rolePermissions.stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoleResponse> listAllRoles() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getStatus, 1)
                .orderByAsc(Role::getSort)
                .orderByDesc(Role::getCreateTime);
        List<Role> roles = roleMapper.selectList(wrapper);
        return roles.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 转换为响应对象
     */
    private RoleResponse convertToResponse(Role role) {
        RoleResponse response = new RoleResponse();
        BeanUtils.copyProperties(role, response);
        response.setDataScopeName(getDataScopeName(role.getDataScope()));
        return response;
    }

    /**
     * 获取数据权限范围名称
     */
    private String getDataScopeName(Integer dataScope) {
        switch (dataScope) {
            case 1:
                return "全部数据";
            case 2:
                return "本部门及以下";
            case 3:
                return "本部门";
            case 4:
                return "仅本人";
            case 5:
                return "自定义";
            default:
                return "未知";
        }
    }
}
