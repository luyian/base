package com.base.system.controller;

import com.base.system.annotation.OperationLog;
import com.base.system.common.Result;
import com.base.system.dto.monitor.CacheInfoResponse;
import com.base.system.dto.monitor.CacheKeyResponse;
import com.base.system.dto.monitor.ServerInfoResponse;
import com.base.system.service.MonitorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统监控控制器
 *
 * @author base
 * @date 2026-01-14
 */
@Slf4j
@RestController
@RequestMapping("/system/monitor")
@Api(tags = "系统监控")
public class MonitorController {

    @Autowired
    private MonitorService monitorService;

    /**
     * 获取服务器信息
     */
    @GetMapping("/server")
    @ApiOperation("获取服务器信息")
    @PreAuthorize("hasAuthority('system:monitor:server')")
    public Result<ServerInfoResponse> getServerInfo() {
        ServerInfoResponse serverInfo = monitorService.getServerInfo();
        return Result.success(serverInfo);
    }

    /**
     * 获取缓存信息
     */
    @GetMapping("/cache")
    @ApiOperation("获取缓存信息")
    @PreAuthorize("hasAuthority('system:monitor:cache')")
    public Result<CacheInfoResponse> getCacheInfo() {
        CacheInfoResponse cacheInfo = monitorService.getCacheInfo();
        return Result.success(cacheInfo);
    }

    /**
     * 获取缓存键列表
     */
    @GetMapping("/cache/keys")
    @ApiOperation("获取缓存键列表")
    @PreAuthorize("hasAuthority('system:monitor:cache')")
    public Result<List<String>> getCacheKeys(
            @ApiParam(value = "匹配模式", example = "*") @RequestParam(required = false, defaultValue = "*") String pattern) {
        List<String> keys = monitorService.getCacheKeys(pattern);
        return Result.success(keys);
    }

    /**
     * 获取缓存值
     */
    @GetMapping("/cache/value/{key}")
    @ApiOperation("获取缓存值")
    @PreAuthorize("hasAuthority('system:monitor:cache')")
    public Result<CacheKeyResponse> getCacheValue(@ApiParam(value = "缓存键") @PathVariable String key) {
        CacheKeyResponse cacheValue = monitorService.getCacheValue(key);
        return Result.success(cacheValue);
    }

    /**
     * 删除缓存键
     */
    @DeleteMapping("/cache/key/{key}")
    @ApiOperation("删除缓存键")
    @PreAuthorize("hasAuthority('system:monitor:cache')")
    @OperationLog(module = "系统监控", operation = "删除缓存键")
    public Result<Boolean> deleteCacheKey(@ApiParam(value = "缓存键") @PathVariable String key) {
        Boolean result = monitorService.deleteCacheKey(key);
        return Result.success(result);
    }

    /**
     * 清空缓存
     */
    @DeleteMapping("/cache/clear")
    @ApiOperation("清空缓存")
    @PreAuthorize("hasAuthority('system:monitor:cache')")
    @OperationLog(module = "系统监控", operation = "清空缓存")
    public Result<Boolean> clearCache() {
        Boolean result = monitorService.clearCache();
        return Result.success(result);
    }
}
