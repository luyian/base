package com.base.system.controller;

import com.base.common.annotation.Log;
import com.base.common.result.Result;
import com.base.system.dto.user.UpdatePasswordRequest;
import com.base.system.dto.user.UpdateProfileRequest;
import com.base.system.dto.user.UserProfileResponse;
import com.base.system.service.UserProfileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户个人中心控制器
 *
 * @author base
 * @since 2026-01-13
 */
@Api(tags = "用户个人中心")
@RestController
@RequestMapping("/system/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @ApiOperation(value = "获取个人信息")
    @GetMapping
    public Result<UserProfileResponse> getProfile() {
        return Result.success(userProfileService.getCurrentUserProfile());
    }

    @ApiOperation(value = "更新个人信息")
    @Log(title = "个人中心", content = "更新个人信息")
    @PutMapping
    public Result<Void> updateProfile(@Validated @RequestBody UpdateProfileRequest request) {
        userProfileService.updateProfile(request);
        return Result.success();
    }

    @ApiOperation(value = "修改密码")
    @Log(title = "个人中心", content = "修改密码")
    @PutMapping("/password")
    public Result<Void> updatePassword(@Validated @RequestBody UpdatePasswordRequest request) {
        userProfileService.updatePassword(request);
        return Result.success();
    }

    @ApiOperation(value = "上传头像")
    @Log(title = "个人中心", content = "上传头像")
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String avatarUrl = userProfileService.uploadAvatar(file);
        return Result.success(avatarUrl);
    }

    @ApiOperation(value = "更新头像URL")
    @Log(title = "个人中心", content = "更新头像URL")
    @PutMapping("/avatar")
    public Result<Void> updateAvatar(@RequestParam String avatarUrl) {
        userProfileService.updateAvatar(avatarUrl);
        return Result.success();
    }
}
