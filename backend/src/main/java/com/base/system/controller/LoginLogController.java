package com.base.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.result.Result;
import com.base.system.dto.log.LoginLogQueryRequest;
import com.base.system.dto.log.LoginLogResponse;
import com.base.system.service.LoginLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 登录日志控制器
 */
@Api(tags = "登录日志管理")
@RestController
@RequestMapping("/system/log/login")
public class LoginLogController {

    @Resource
    private LoginLogService loginLogService;

    /**
     * 分页查询登录日志列表
     */
    @ApiOperation("分页查询登录日志列表")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('log:login:list')")
    public Result<Page<LoginLogResponse>> pageLoginLogs(@RequestBody LoginLogQueryRequest request) {
        Page<LoginLogResponse> page = loginLogService.pageLoginLogs(request);
        return Result.success(page);
    }

    /**
     * 根据ID获取登录日志详情
     */
    @ApiOperation("根据ID获取登录日志详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('log:login:query')")
    public Result<LoginLogResponse> getLoginLogById(@PathVariable Long id) {
        LoginLogResponse response = loginLogService.getLoginLogById(id);
        return Result.success(response);
    }

    /**
     * 删除登录日志
     */
    @ApiOperation("删除登录日志")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('log:login:delete')")
    public Result<Void> deleteLoginLog(@PathVariable Long id) {
        loginLogService.deleteLoginLog(id);
        return Result.success();
    }

    /**
     * 批量删除登录日志
     */
    @ApiOperation("批量删除登录日志")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('log:login:delete')")
    public Result<Void> batchDeleteLoginLogs(@RequestBody List<Long> ids) {
        loginLogService.batchDeleteLoginLogs(ids);
        return Result.success();
    }

    /**
     * 清空登录日志
     */
    @ApiOperation("清空登录日志")
    @DeleteMapping("/clear")
    @PreAuthorize("hasAuthority('log:login:delete')")
    public Result<Void> clearLoginLogs() {
        loginLogService.clearLoginLogs();
        return Result.success();
    }
}
