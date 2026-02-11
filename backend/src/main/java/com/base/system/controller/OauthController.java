package com.base.system.controller;

import com.base.common.result.Result;
import com.base.system.dto.LoginResponse;
import com.base.system.dto.oauth.*;
import com.base.system.service.OauthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * OAuth 第三方登录控制器（无需认证）
 */
@Slf4j
@RestController
@RequestMapping("/auth/oauth")
@Api(tags = "OAuth第三方登录")
public class OauthController {

    @Autowired
    private OauthService oauthService;

    /**
     * 查询第三方登录是否启用
     */
    @GetMapping("/enabled")
    @ApiOperation("查询第三方登录是否启用")
    public Result<Boolean> isOauthEnabled() {
        return Result.success(oauthService.isOauthEnabled());
    }

    /**
     * 获取 GitHub 授权地址
     */
    @GetMapping("/github/url")
    @ApiOperation("获取GitHub授权地址")
    public Result<String> getGithubAuthUrl() {
        String url = oauthService.getGithubAuthUrl();
        return Result.success(url);
    }

    /**
     * GitHub 回调处理
     */
    @PostMapping("/github/callback")
    @ApiOperation("GitHub回调处理")
    public Result<OauthCallbackResponse> handleGithubCallback(@Validated @RequestBody OauthCallbackRequest request) {
        OauthCallbackResponse response = oauthService.handleGithubCallback(request);
        return Result.success(response);
    }

    /**
     * 创建新账号并绑定
     */
    @PostMapping("/bindNew")
    @ApiOperation("创建新账号并绑定")
    public Result<LoginResponse> bindNewUser(@Validated @RequestBody OauthBindNewRequest request) {
        LoginResponse response = oauthService.bindNewUser(request);
        return Result.success(response);
    }

    /**
     * 绑定已有账号
     */
    @PostMapping("/bindExist")
    @ApiOperation("绑定已有账号")
    public Result<LoginResponse> bindExistUser(@Validated @RequestBody OauthBindExistRequest request) {
        LoginResponse response = oauthService.bindExistUser(request);
        return Result.success(response);
    }
}
