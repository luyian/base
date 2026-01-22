package com.base.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.common.exception.BusinessException;
import com.base.common.util.SecurityUtils;
import com.base.system.dto.user.UpdatePasswordRequest;
import com.base.system.dto.user.UpdateProfileRequest;
import com.base.system.dto.user.UserProfileResponse;
import com.base.system.entity.Department;
import com.base.system.entity.User;
import com.base.system.entity.UserRole;
import com.base.system.mapper.DepartmentMapper;
import com.base.system.mapper.UserMapper;
import com.base.system.mapper.UserRoleMapper;
import com.base.system.service.UserProfileService;
import com.base.system.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户个人中心服务实现类
 *
 * @author base
 * @since 2026-01-13
 */
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserMapper userMapper;
    private final DepartmentMapper departmentMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final FileUploadUtil fileUploadUtil;

    @Override
    public UserProfileResponse getCurrentUserProfile() {
        // 获取当前登录用户ID
        Long userId = SecurityUtils.getCurrentUserId();

        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 转换为响应对象
        UserProfileResponse response = BeanUtil.copyProperties(user, UserProfileResponse.class);

        // 查询部门名称
        if (user.getDeptId() != null) {
            Department department = departmentMapper.selectById(user.getDeptId());
            if (department != null) {
                response.setDepartmentName(department.getDeptName());
            }
        }

        // 查询角色列表
        List<UserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, userId)
        );
        if (!userRoles.isEmpty()) {
            String roles = userRoles.stream()
                    .map(ur -> ur.getRoleId().toString())
                    .collect(Collectors.joining(","));
            response.setRoles(roles);
        }

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(UpdateProfileRequest request) {
        // 获取当前登录用户ID
        Long userId = SecurityUtils.getCurrentUserId();

        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新用户信息
        user.setNickname(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }

        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(UpdatePasswordRequest request) {
        // 验证新密码和确认密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 获取当前登录用户ID
        Long userId = SecurityUtils.getCurrentUserId();

        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码是否正确
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAvatar(String avatarUrl) {
        // 获取当前登录用户ID
        Long userId = SecurityUtils.getCurrentUserId();

        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新头像
        user.setAvatar(avatarUrl);
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadAvatar(MultipartFile file) {
        // 获取当前登录用户ID
        Long userId = SecurityUtils.getCurrentUserId();

        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 上传头像文件
        String avatarUrl = fileUploadUtil.uploadAvatar(file);

        // 删除旧头像（如果存在）
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            fileUploadUtil.deleteFile(user.getAvatar());
        }

        // 更新头像
        user.setAvatar(avatarUrl);
        userMapper.updateById(user);

        return avatarUrl;
    }
}
