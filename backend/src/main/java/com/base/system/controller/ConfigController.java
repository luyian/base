package com.base.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.system.common.Result;
import com.base.system.dto.config.ConfigQueryRequest;
import com.base.system.dto.config.ConfigResponse;
import com.base.system.dto.config.ConfigSaveRequest;
import com.base.system.service.ConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 全局变量控制器
 */
@Api(tags = "全局变量管理")
@RestController
@RequestMapping("/system/config")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    /**
     * 分页查询全局变量列表
     */
    @ApiOperation("分页查询全局变量列表")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('system:config:list')")
    public Result<Page<ConfigResponse>> pageConfigs(@RequestBody ConfigQueryRequest request) {
        Page<ConfigResponse> page = configService.pageConfigs(request);
        return Result.success(page);
    }

    /**
     * 根据ID获取全局变量详情
     */
    @ApiOperation("根据ID获取全局变量详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:config:query')")
    public Result<ConfigResponse> getConfigById(@PathVariable Long id) {
        ConfigResponse response = configService.getConfigById(id);
        return Result.success(response);
    }

    /**
     * 新增全局变量
     */
    @ApiOperation("新增全局变量")
    @PostMapping
    @PreAuthorize("hasAuthority('system:config:add')")
    public Result<Void> addConfig(@Validated @RequestBody ConfigSaveRequest request) {
        configService.addConfig(request);
        return Result.success();
    }

    /**
     * 编辑全局变量
     */
    @ApiOperation("编辑全局变量")
    @PutMapping
    @PreAuthorize("hasAuthority('system:config:edit')")
    public Result<Void> updateConfig(@Validated @RequestBody ConfigSaveRequest request) {
        configService.updateConfig(request);
        return Result.success();
    }

    /**
     * 删除全局变量
     */
    @ApiOperation("删除全局变量")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:config:delete')")
    public Result<Void> deleteConfig(@PathVariable Long id) {
        configService.deleteConfig(id);
        return Result.success();
    }

    /**
     * 批量删除全局变量
     */
    @ApiOperation("批量删除全局变量")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('system:config:delete')")
    public Result<Void> batchDeleteConfigs(@RequestBody List<Long> ids) {
        configService.batchDeleteConfigs(ids);
        return Result.success();
    }

    /**
     * 根据配置键获取配置值
     */
    @ApiOperation("根据配置键获取配置值")
    @GetMapping("/key/{configKey}")
    public Result<String> getConfigValueByKey(@PathVariable String configKey) {
        String configValue = configService.getConfigValueByKey(configKey);
        return Result.success(configValue);
    }

    /**
     * 刷新全局变量缓存
     */
    @ApiOperation("刷新全局变量缓存")
    @PostMapping("/refresh-cache")
    @PreAuthorize("hasAuthority('system:config:edit')")
    public Result<Void> refreshCache() {
        configService.refreshCache();
        return Result.success();
    }
}
