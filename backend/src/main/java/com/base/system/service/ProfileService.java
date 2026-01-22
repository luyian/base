package com.base.system.service;

import com.base.system.dto.PasswordUpdateRequest;
import com.base.system.dto.ProfileUpdateRequest;
import com.base.system.dto.UserResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 个人中心服务接口
 *
 * @author base
 * @since 2026-01-13
 */
public interface ProfileService {

    /**
     * 获取个人信息
     */
    UserResponse getProfile();

    /**
     * 修改个人信息
     */
    void updateProfile(ProfileUpdateRequest request);

    /**
     * 修改密码
     */
    void updatePassword(PasswordUpdateRequest request);

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @return 头像访问URL
     */
    String uploadAvatar(MultipartFile file);
}
