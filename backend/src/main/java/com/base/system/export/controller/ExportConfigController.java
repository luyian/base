package com.base.system.export.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.result.Result;
import com.base.system.export.dto.config.ExportConfigQueryRequest;
import com.base.system.export.dto.config.ExportConfigResponse;
import com.base.system.export.dto.config.ExportConfigSaveRequest;
import com.base.system.export.dto.field.ExportFieldResponse;
import com.base.system.export.dto.field.ExportFieldSaveRequest;
import com.base.system.export.service.ExportConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 导出配置控制器
 *
 * @author base
 * @since 2026-02-04
 */
@Api(tags = "导出配置管理")
@RestController
@RequestMapping("/system/export/config")
public class ExportConfigController {

    @Autowired
    private ExportConfigService exportConfigService;

    @ApiOperation("分页查询导出配置")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:export:config:list')")
    public Result<Page<ExportConfigResponse>> page(ExportConfigQueryRequest request) {
        return Result.success(exportConfigService.pageConfigs(request));
    }

    @ApiOperation("获取配置详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:export:config:query')")
    public Result<ExportConfigResponse> getById(@PathVariable Long id) {
        return Result.success(exportConfigService.getConfigById(id));
    }

    @ApiOperation("新增配置")
    @PostMapping
    @PreAuthorize("hasAuthority('system:export:config:add')")
    public Result<Long> add(@Validated @RequestBody ExportConfigSaveRequest request) {
        return Result.success(exportConfigService.addConfig(request));
    }

    @ApiOperation("更新配置")
    @PutMapping
    @PreAuthorize("hasAuthority('system:export:config:edit')")
    public Result<Void> update(@Validated @RequestBody ExportConfigSaveRequest request) {
        exportConfigService.updateConfig(request);
        return Result.success();
    }

    @ApiOperation("删除配置")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:export:config:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        exportConfigService.deleteConfig(id);
        return Result.success();
    }

    @ApiOperation("获取字段配置列表")
    @GetMapping("/{id}/fields")
    @PreAuthorize("hasAuthority('system:export:config:query')")
    public Result<List<ExportFieldResponse>> getFields(@PathVariable Long id) {
        return Result.success(exportConfigService.getFieldsByConfigId(id));
    }

    @ApiOperation("保存字段配置")
    @PostMapping("/{id}/fields")
    @PreAuthorize("hasAuthority('system:export:config:edit')")
    public Result<Void> saveFields(@PathVariable Long id, @RequestBody List<ExportFieldSaveRequest> fields) {
        exportConfigService.saveFields(id, fields);
        return Result.success();
    }

    @ApiOperation("获取所有启用的配置列表")
    @GetMapping("/list")
    public Result<List<ExportConfigResponse>> listEnabled() {
        return Result.success(exportConfigService.listEnabledConfigs());
    }
}
