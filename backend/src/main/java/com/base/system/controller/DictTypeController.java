package com.base.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.system.common.Result;
import com.base.system.dto.dict.DictTypeQueryRequest;
import com.base.system.dto.dict.DictTypeResponse;
import com.base.system.dto.dict.DictTypeSaveRequest;
import com.base.system.service.DictTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典类型控制器
 *
 * @author base
 * @since 2026-06-09
 */
@Api(tags = "字典类型管理")
@RestController
@RequestMapping("/system/dict/type")
@RequiredArgsConstructor
public class DictTypeController {

    private final DictTypeService dictTypeService;

    /**
     * 分页查询字典类型
     */
    @ApiOperation("分页查询字典类型")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('system:dict:list')")
    public Result<Page<DictTypeResponse>> pageDictTypes(@RequestBody DictTypeQueryRequest request) {
        Page<DictTypeResponse> page = dictTypeService.pageDictTypes(request);
        return Result.success(page);
    }

    /**
     * 根据ID获取字典类型详情
     */
    @ApiOperation("根据ID获取字典类型详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dict:query')")
    public Result<DictTypeResponse> getDictTypeById(@PathVariable Long id) {
        DictTypeResponse response = dictTypeService.getDictTypeById(id);
        return Result.success(response);
    }

    /**
     * 查询所有字典类型列表
     */
    @ApiOperation("查询所有字典类型列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:dict:list')")
    public Result<List<DictTypeResponse>> listAllDictTypes() {
        List<DictTypeResponse> list = dictTypeService.listAllDictTypes();
        return Result.success(list);
    }

    /**
     * 新增字典类型
     */
    @ApiOperation("新增字典类型")
    @PostMapping
    @PreAuthorize("hasAuthority('system:dict:add')")
    public Result<Void> addDictType(@Validated @RequestBody DictTypeSaveRequest request) {
        dictTypeService.addDictType(request);
        return Result.success();
    }

    /**
     * 编辑字典类型
     */
    @ApiOperation("编辑字典类型")
    @PutMapping
    @PreAuthorize("hasAuthority('system:dict:edit')")
    public Result<Void> updateDictType(@Validated @RequestBody DictTypeSaveRequest request) {
        dictTypeService.updateDictType(request);
        return Result.success();
    }

    /**
     * 删除字典类型
     */
    @ApiOperation("删除字典类型")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dict:delete')")
    public Result<Void> deleteDictType(@PathVariable Long id) {
        dictTypeService.deleteDictType(id);
        return Result.success();
    }

    /**
     * 批量删除字典类型
     */
    @ApiOperation("批量删除字典类型")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('system:dict:delete')")
    public Result<Void> batchDeleteDictTypes(@RequestBody List<Long> ids) {
        dictTypeService.batchDeleteDictTypes(ids);
        return Result.success();
    }

    /**
     * 刷新字典缓存
     */
    @ApiOperation("刷新字典缓存")
    @PostMapping("/refresh-cache")
    @PreAuthorize("hasAuthority('system:dict:edit')")
    public Result<Void> refreshCache() {
        dictTypeService.refreshCache();
        return Result.success();
    }
}
