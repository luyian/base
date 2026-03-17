package com.base.ai.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.ai.dto.SysAiConfigResponse;
import com.base.ai.dto.SysAiConfigSaveRequest;
import com.base.ai.service.SysAiConfigService;
import com.base.common.annotation.Log;
import com.base.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 大模型配置接口（系统管理-大模型配置，支持多条、选一条生效）
 *
 * @author base
 * @since 2026-03-17
 */
@Tag(name = "大模型配置")
@RestController
@RequestMapping("/system/ai-config")
@RequiredArgsConstructor
public class AiConfigController {

    private final SysAiConfigService sysAiConfigService;

    @Operation(summary = "分页查询")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:ai-config:query')")
    public Result<Page<SysAiConfigResponse>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String configName,
            @RequestParam(required = false) Integer status) {
        return Result.success(sysAiConfigService.page(current, size, configName, status));
    }

    @Operation(summary = "列表（下拉/选择生效用）")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:ai-config:query')")
    public Result<List<SysAiConfigResponse>> list() {
        return Result.success(sysAiConfigService.listAll());
    }

    @Operation(summary = "详情（编辑用，含 API Key 原文）")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:ai-config:query')")
    public Result<SysAiConfigResponse> getByIdForEdit(@PathVariable Long id) {
        return Result.success(sysAiConfigService.getByIdForEdit(id));
    }

    @Operation(summary = "新增")
    @PostMapping
    @PreAuthorize("hasAuthority('system:ai-config:add')")
    @Log(title = "大模型配置", content = "新增大模型配置", type = "OTHER")
    public Result<Long> save(@Validated @RequestBody SysAiConfigSaveRequest request) {
        return Result.success(sysAiConfigService.save(request));
    }

    @Operation(summary = "编辑")
    @PutMapping
    @PreAuthorize("hasAuthority('system:ai-config:edit')")
    @Log(title = "大模型配置", content = "编辑大模型配置", type = "OTHER")
    public Result<Void> update(@Validated @RequestBody SysAiConfigSaveRequest request) {
        sysAiConfigService.update(request);
        return Result.success();
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:ai-config:delete')")
    @Log(title = "大模型配置", content = "删除大模型配置", type = "OTHER")
    public Result<Void> delete(@PathVariable Long id) {
        sysAiConfigService.deleteById(id);
        return Result.success();
    }

    @Operation(summary = "设为生效")
    @PostMapping("/{id}/active")
    @PreAuthorize("hasAuthority('system:ai-config:edit')")
    @Log(title = "大模型配置", content = "设为生效配置", type = "OTHER")
    public Result<Void> setActive(@PathVariable Long id) {
        sysAiConfigService.setActive(id);
        return Result.success();
    }
}
