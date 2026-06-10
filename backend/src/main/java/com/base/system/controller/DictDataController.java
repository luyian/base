package com.base.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.system.common.Result;
import com.base.system.dto.dict.DictDataQueryRequest;
import com.base.system.dto.dict.DictDataResponse;
import com.base.system.dto.dict.DictDataSaveRequest;
import com.base.system.service.DictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据控制器
 *
 * @author base
 * @since 2026-06-09
 */
@Api(tags = "字典数据管理")
@RestController
@RequestMapping("/system/dict/data")
@RequiredArgsConstructor
public class DictDataController {

    private final DictDataService dictDataService;

    /**
     * 分页查询字典数据
     */
    @ApiOperation("分页查询字典数据")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('system:dict:list')")
    public Result<Page<DictDataResponse>> pageDictData(@RequestBody DictDataQueryRequest request) {
        Page<DictDataResponse> page = dictDataService.pageDictData(request);
        return Result.success(page);
    }

    /**
     * 根据ID获取字典数据详情
     */
    @ApiOperation("根据ID获取字典数据详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dict:query')")
    public Result<DictDataResponse> getDictDataById(@PathVariable Long id) {
        DictDataResponse response = dictDataService.getDictDataById(id);
        return Result.success(response);
    }

    /**
     * 根据字典类型查询数据列表（公开接口）
     */
    @ApiOperation("根据字典类型查询数据列表")
    @GetMapping("/type/{dictType}")
    public Result<List<DictDataResponse>> listByDictType(@PathVariable String dictType) {
        List<DictDataResponse> list = dictDataService.listByDictType(dictType);
        return Result.success(list);
    }

    /**
     * 新增字典数据
     */
    @ApiOperation("新增字典数据")
    @PostMapping
    @PreAuthorize("hasAuthority('system:dict:add')")
    public Result<Void> addDictData(@Validated @RequestBody DictDataSaveRequest request) {
        dictDataService.addDictData(request);
        return Result.success();
    }

    /**
     * 编辑字典数据
     */
    @ApiOperation("编辑字典数据")
    @PutMapping
    @PreAuthorize("hasAuthority('system:dict:edit')")
    public Result<Void> updateDictData(@Validated @RequestBody DictDataSaveRequest request) {
        dictDataService.updateDictData(request);
        return Result.success();
    }

    /**
     * 删除字典数据
     */
    @ApiOperation("删除字典数据")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dict:delete')")
    public Result<Void> deleteDictData(@PathVariable Long id) {
        dictDataService.deleteDictData(id);
        return Result.success();
    }

    /**
     * 批量删除字典数据
     */
    @ApiOperation("批量删除字典数据")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('system:dict:delete')")
    public Result<Void> batchDeleteDictData(@RequestBody List<Long> ids) {
        dictDataService.batchDeleteDictData(ids);
        return Result.success();
    }
}
