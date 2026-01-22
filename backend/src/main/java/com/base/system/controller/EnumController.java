package com.base.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.system.common.Result;
import com.base.system.dto.enums.EnumQueryRequest;
import com.base.system.dto.enums.EnumResponse;
import com.base.system.dto.enums.EnumSaveRequest;
import com.base.system.service.EnumService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 枚举控制器
 */
@Api(tags = "枚举管理")
@RestController
@RequestMapping("/system/enum")
@RequiredArgsConstructor
public class EnumController {

    private final EnumService enumService;

    /**
     * 分页查询枚举列表
     */
    @ApiOperation("分页查询枚举列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:enum:list')")
    public Result<Page<EnumResponse>> pageEnums(EnumQueryRequest request) {
        Page<EnumResponse> page = enumService.pageEnums(request);
        return Result.success(page);
    }

    /**
     * 根据ID获取枚举详情
     */
    @ApiOperation("根据ID获取枚举详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:enum:query')")
    public Result<EnumResponse> getEnumById(@PathVariable Long id) {
        EnumResponse response = enumService.getEnumById(id);
        return Result.success(response);
    }

    /**
     * 新增枚举
     */
    @ApiOperation("新增枚举")
    @PostMapping
    @PreAuthorize("hasAuthority('system:enum:add')")
    public Result<Void> addEnum(@Validated @RequestBody EnumSaveRequest request) {
        enumService.addEnum(request);
        return Result.success();
    }

    /**
     * 编辑枚举
     */
    @ApiOperation("编辑枚举")
    @PutMapping
    @PreAuthorize("hasAuthority('system:enum:edit')")
    public Result<Void> updateEnum(@Validated @RequestBody EnumSaveRequest request) {
        enumService.updateEnum(request);
        return Result.success();
    }

    /**
     * 删除枚举
     */
    @ApiOperation("删除枚举")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:enum:delete')")
    public Result<Void> deleteEnum(@PathVariable Long id) {
        enumService.deleteEnum(id);
        return Result.success();
    }

    /**
     * 批量删除枚举
     */
    @ApiOperation("批量删除枚举")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('system:enum:delete')")
    public Result<Void> batchDeleteEnums(@RequestBody List<Long> ids) {
        enumService.batchDeleteEnums(ids);
        return Result.success();
    }

    /**
     * 根据枚举类型查询枚举列表
     */
    @ApiOperation("根据枚举类型查询枚举列表")
    @GetMapping("/type/{enumType}")
    public Result<List<EnumResponse>> listByType(@PathVariable String enumType) {
        List<EnumResponse> list = enumService.listByType(enumType);
        return Result.success(list);
    }

    /**
     * 刷新枚举缓存
     */
    @ApiOperation("刷新枚举缓存")
    @PostMapping("/refresh-cache")
    @PreAuthorize("hasAuthority('system:enum:edit')")
    public Result<Void> refreshCache() {
        enumService.refreshCache();
        return Result.success();
    }
}
