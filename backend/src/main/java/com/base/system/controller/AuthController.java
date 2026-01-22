package com.base.system.controller;

import com.base.common.result.Result;
import com.base.system.dto.CaptchaResponse;
import com.base.system.dto.LoginRequest;
import com.base.system.dto.LoginResponse;
import com.base.system.dto.RouterVO;
import com.base.system.dto.UserInfoResponse;
import com.base.system.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@Api(tags = "认证管理")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 获取验证码
     */
    @GetMapping("/captcha")
    @ApiOperation("获取验证码")
    public Result<CaptchaResponse> getCaptcha() {
        CaptchaResponse captcha = authService.generateCaptcha();
        return Result.success(captcha);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<LoginResponse> login(@Validated @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success(response);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @ApiOperation("用户登出")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    /**
     * 刷新 Token
     */
    @PostMapping("/refresh")
    @ApiOperation("刷新 Token")
    public Result<String> refreshToken(@RequestHeader("Authorization") String authorization) {
        // 移除 "Bearer " 前缀
        String token = authorization.replace("Bearer ", "");
        String newToken = authService.refreshToken(token);
        return Result.success(newToken);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    @ApiOperation("获取当前用户信息")
    public Result<UserInfoResponse> getUserInfo() {
        UserInfoResponse userInfo = authService.getUserInfo();
        return Result.success(userInfo);
    }

    /**
     * 获取当前用户的路由菜单
     */
    @GetMapping("/routers")
    @ApiOperation("获取当前用户的路由菜单")
    public Result<List<RouterVO>> getRouters() {
        List<RouterVO> routers = authService.getRouters();
        return Result.success(routers);
    }
}
