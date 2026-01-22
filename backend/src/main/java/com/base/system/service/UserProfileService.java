package com.base.system.service;

import com.base.system.dto.user.UpdatePasswordRequest;
import com.base.system.dto.user.UpdateProfileRequest;
import com.base.system.dto.user.UserProfileResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户个人中心服务接口
 *
 * @author base
 * @since 2026-01-13
 */
public interface UserProfileService {

    /**
     * 获取当前用户个人信息
     *
     * @return 个人信息
     */
    UserProfileResponse getCurrentUserProfile();

    /**
     * 更新个人信息
     *
     * @param request 更新请求
     */
    void updateProfile(UpdateProfileRequest request);

    /**
     * 修改密码
     *
     * @param request 修改密码请求
     */
    void updatePassword(UpdatePasswordRequest request);

    /**
     * 更新头像URL
     *
     * @param avatarUrl 头像URL
     */
    void updateAvatar(String avatarUrl);

    /**
     * 上传头像文件
     *
     * @param file 头像文件
     * @return 头像访问URL
     */
    String uploadAvatar(MultipartFile file);
}
