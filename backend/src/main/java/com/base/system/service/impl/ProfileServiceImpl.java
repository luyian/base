package com.base.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import com.base.common.util.SecurityUtils;
import com.base.system.dto.PasswordUpdateRequest;
import com.base.system.dto.ProfileUpdateRequest;
import com.base.system.dto.UserResponse;
import com.base.system.entity.User;
import com.base.system.mapper.UserMapper;
import com.base.system.service.ProfileService;
import com.base.system.service.UserService;
import com.base.system.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 个人中心服务实现类
 *
 * @author base
 * @since 2026-01-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserMapper userMapper;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final FileUploadUtil fileUploadUtil;

    @Override
    public UserResponse getProfile() {
        // 获取当前登录用户ID
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录");
        }

        // 查询用户详情
        return userService.getUserById(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(ProfileUpdateRequest request) {
        // 获取当前登录用户ID
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录");
        }

        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 更新用户信息
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }

        userMapper.updateById(user);
        log.info("用户 {} 修改个人信息成功", user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(PasswordUpdateRequest request) {
        // 获取当前登录用户ID
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录");
        }

        // 验证新密码和确认密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "两次输入的密码不一致");
        }

        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 验证旧密码是否正确
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "旧密码不正确");
        }

        // 验证新密码不能与旧密码相同
        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "新密码不能与旧密码相同");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userMapper.updateById(user);
        log.info("用户 {} 修改密码成功", user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadAvatar(MultipartFile file) {
        // 获取当前登录用户ID
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录");
        }

        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
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
        log.info("用户 {} 更新头像成功，头像URL: {}", user.getUsername(), avatarUrl);

        return avatarUrl;
    }
}
