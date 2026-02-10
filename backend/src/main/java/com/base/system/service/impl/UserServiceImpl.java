package com.base.system.service.impl;

import com.base.common.annotation.DataScope;
import com.base.system.exception.BusinessException;
import com.base.common.result.ResultCode;
import com.base.system.dto.*;
import com.base.system.entity.SysUser;
import com.base.system.entity.SysUserRole;
import com.base.system.mapper.SysUserMapper;
import com.base.system.mapper.SysUserRoleMapper;
import com.base.system.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @DataScope(deptAlias = "", userAlias = "")
    public Page<UserResponse> pageUsers(UserQueryRequest request) {
        // 构建分页对象
        Page<SysUser> page = request.buildPage();

        // 构建查询条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(request.getUsername()), SysUser::getUsername, request.getUsername())
                .like(StringUtils.hasText(request.getNickname()), SysUser::getNickname, request.getNickname())
                .like(StringUtils.hasText(request.getPhone()), SysUser::getPhone, request.getPhone())
                .like(StringUtils.hasText(request.getEmail()), SysUser::getEmail, request.getEmail())
                .eq(request.getStatus() != null, SysUser::getStatus, request.getStatus())
                .eq(request.getDeptId() != null, SysUser::getDeptId, request.getDeptId());

        // 时间范围查询
        if (StringUtils.hasText(request.getStartTime())) {
            LocalDateTime startTime = LocalDateTime.parse(request.getStartTime() + " 00:00:00",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            wrapper.ge(SysUser::getCreateTime, startTime);
        }
        if (StringUtils.hasText(request.getEndTime())) {
            LocalDateTime endTime = LocalDateTime.parse(request.getEndTime() + " 23:59:59",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            wrapper.le(SysUser::getCreateTime, endTime);
        }

        // 排序
        wrapper.orderByDesc(SysUser::getCreateTime);

        // 查询
        Page<SysUser> userPage = userMapper.selectPage(page, wrapper);

        // 转换为响应对象
        Page<UserResponse> responsePage = new Page<>();
        BeanUtils.copyProperties(userPage, responsePage, "records");

        List<UserResponse> responseList = userPage.getRecords().stream().map(user -> {
            UserResponse response = new UserResponse();
            BeanUtils.copyProperties(user, response);

            // 设置性别名称
            if (user.getGender() != null) {
                response.setGenderName(user.getGender() == 0 ? "女" : user.getGender() == 1 ? "男" : "未知");
            }

            // 设置状态名称
            response.setStatusName(user.getStatus() == 1 ? "启用" : "禁用");

            // TODO: 查询部门名称
            // TODO: 查询角色列表

            return response;
        }).collect(Collectors.toList());

        responsePage.setRecords(responseList);

        return responsePage;
    }

    @Override
    public UserResponse getUserById(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);

        // 设置性别名称
        if (user.getGender() != null) {
            response.setGenderName(user.getGender() == 0 ? "女" : user.getGender() == 1 ? "男" : "未知");
        }

        // 设置状态名称
        response.setStatusName(user.getStatus() == 1 ? "启用" : "禁用");

        // 查询用户角色
        List<Long> roleIds = getUserRoleIds(id);
        response.setRoleIds(roleIds);

        // TODO: 查询部门名称
        // TODO: 查询角色名称列表

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addUser(UserSaveRequest request) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, request.getUsername());
        Long count = userMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.USERNAME_ALREADY_EXISTS);
        }

        // 检查密码
        if (!StringUtils.hasText(request.getPassword())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "密码不能为空");
        }

        // 创建用户对象
        SysUser user = new SysUser();
        BeanUtils.copyProperties(request, user);

        // 加密密码
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 设置默认值
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        if (user.getGender() == null) {
            user.setGender(2);
        }

        // 保存用户
        userMapper.insert(user);

        log.info("新增用户成功，username: {}, userId: {}", user.getUsername(), user.getId());

        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserSaveRequest request) {
        // 检查用户是否存在
        SysUser existUser = userMapper.selectById(request.getId());
        if (existUser == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 检查用户名是否被其他用户占用
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, request.getUsername())
                .ne(SysUser::getId, request.getId());
        Long count = userMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.USERNAME_ALREADY_EXISTS);
        }

        // 更新用户信息
        SysUser user = new SysUser();
        BeanUtils.copyProperties(request, user);

        // 如果提供了密码，则更新密码
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        } else {
            user.setPassword(null); // 不更新密码
        }

        userMapper.updateById(user);

        log.info("更新用户成功，username: {}, userId: {}", user.getUsername(), user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        // 检查用户是否存在
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 不能删除超级管理员
        if ("admin".equals(user.getUsername())) {
            throw new BusinessException(ResultCode.OPERATION_NOT_ALLOWED.getCode(), "不能删除超级管理员");
        }

        // 删除用户（逻辑删除）
        userMapper.deleteById(id);

        // 删除用户角色关联
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, id);
        userRoleMapper.delete(wrapper);

        log.info("删除用户成功，userId: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteUsers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "用户ID列表不能为空");
        }

        // 检查是否包含超级管理员
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysUser::getId, ids)
                .eq(SysUser::getUsername, "admin");
        Long count = userMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.OPERATION_NOT_ALLOWED.getCode(), "不能删除超级管理员");
        }

        // 批量删除用户
        userMapper.deleteBatchIds(ids);

        // 删除用户角色关联
        LambdaQueryWrapper<SysUserRole> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.in(SysUserRole::getUserId, ids);
        userRoleMapper.delete(roleWrapper);

        log.info("批量删除用户成功，数量: {}", ids.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleStatus(Long id, Integer status) {
        // 检查用户是否存在
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 不能禁用超级管理员
        if ("admin".equals(user.getUsername()) && status == 0) {
            throw new BusinessException(ResultCode.OPERATION_NOT_ALLOWED.getCode(), "不能禁用超级管理员");
        }

        // 更新状态
        user.setStatus(status);
        userMapper.updateById(user);

        log.info("切换用户状态成功，userId: {}, status: {}", id, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(UserResetPasswordRequest request) {
        // 检查用户是否存在
        SysUser user = userMapper.selectById(request.getUserId());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 加密新密码
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());

        // 更新密码
        user.setPassword(encodedPassword);
        userMapper.updateById(user);

        log.info("重置用户密码成功，userId: {}", request.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(UserAssignRoleRequest request) {
        // 检查用户是否存在
        SysUser user = userMapper.selectById(request.getUserId());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 删除原有角色关联
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, request.getUserId());
        userRoleMapper.delete(wrapper);

        // 添加新的角色关联
        List<SysUserRole> userRoles = request.getRoleIds().stream().map(roleId -> {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(request.getUserId());
            userRole.setRoleId(roleId);
            return userRole;
        }).collect(Collectors.toList());

        // 批量插入
        userRoles.forEach(userRoleMapper::insert);

        log.info("分配用户角色成功，userId: {}, roleIds: {}", request.getUserId(), request.getRoleIds());
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = userRoleMapper.selectList(wrapper);

        return userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
    }
}
