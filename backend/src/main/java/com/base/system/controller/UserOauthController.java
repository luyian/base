package com.base.system.controller;

import com.base.common.result.Result;
import com.base.system.entity.UserOauth;
import com.base.system.service.OauthService;
import com.base.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户第三方账号管理控制器（需要认证）
 */
@Slf4j
@RestController
@RequestMapping("/system/oauth")
@Api(tags = "第三方账号管理")
public class UserOauthController {

    @Autowired
    private OauthService oauthService;

    /**
     * 查询当前用户绑定列表
     */
    @GetMapping("/bindList")
    @ApiOperation("查询当前用户绑定列表")
    public Result<List<UserOauth>> listBindings() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<UserOauth> list = oauthService.listUserOauthBindings(userId);
        return Result.success(list);
    }

    /**
     * 解绑第三方账号
     */
    @DeleteMapping("/unbind/{oauthType}")
    @ApiOperation("解绑第三方账号")
    public Result<Void> unbind(@PathVariable String oauthType) {
        Long userId = SecurityUtils.getCurrentUserId();
        oauthService.unbind(userId, oauthType);
        return Result.success();
    }
}
